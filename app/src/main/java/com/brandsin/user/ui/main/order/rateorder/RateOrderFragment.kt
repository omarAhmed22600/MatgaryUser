package com.brandsin.user.ui.main.order.rateorder

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentRateOrderBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.rate.RateOrderResponse
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.map.observe
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class RateOrderFragment : BaseHomeFragment(), Observer<Any?>
{
    private lateinit var binding : HomeFragmentRateOrderBinding
    private lateinit var viewModel: RateOrderViewModel
    private val fragmentArgs : RateOrderFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_rate_order, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RateOrderViewModel::class.java)
        binding.viewModel = viewModel


        viewModel.getOrderDetails(fragmentArgs.orderId)

        setBarName(getString(R.string.rate_order))
        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        observe(viewModel.apiResponseLiveData) {
            when (it.status) {
                Status.ERROR_MESSAGE -> {
                    showToast(it.message.toString(), 1)
                }
                Status.SUCCESS_MESSAGE -> {
                    showToast(it.message.toString(), 2)
                }
                Status.SUCCESS -> {
                    when (it.data) {
                        is RateOrderResponse -> {
                            if (it.message.toString()=="null"){
                                showToast(getString(R.string.evaluation_successful), 2)
                            }else{
                                showToast(it.message.toString(), 2)
                            }
                            lifecycleScope.launch {
                                delay(2000)
                                findNavController().navigate(R.id.rate_to_my_orders)
                            }
                        }
                    }
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }
    }

    override fun onChanged(it: Any?)
    {
        if (it == null) return
        when (it)
        {
            Codes.STORE_RATING_EMPTY -> {
                showToast(getString(R.string.please_enter_store_rating) , 1)
            }
            Codes.DRIVIER_RATING_EMPTY -> {
                showToast(getString(R.string.please_enter_drvier_rating) , 1)
            }
            Codes.RATING_SUCCESS -> {
                val uri = Uri.parse(getString(R.string.app_link_android))
                val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
                try {
                    startActivity(myAppLinkToMarket)
                }
                catch (e: ActivityNotFoundException) {
                    Toasty.warning(requireActivity(), "Impossible to find an application for the market").show()
                }
            }
        }
    }
}