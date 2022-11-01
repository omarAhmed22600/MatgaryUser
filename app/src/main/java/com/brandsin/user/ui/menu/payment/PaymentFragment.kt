package com.brandsin.user.ui.menu.payment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentPaymentBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.home.BaseHomeFragment

class PaymentFragment : BaseHomeFragment(), Observer<Any?>
{
    private lateinit var viewModel: PaymentViewModel
    private lateinit var binding: HomeFragmentPaymentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_payment, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        setBarName(getString(R.string.payment))
    }

    override fun onChanged(it: Any?)
    {
        if(it == null) return
        when (it) {
            Codes.EDIT_CLICKED -> {
                // do what you want
            }
        }
    }
}