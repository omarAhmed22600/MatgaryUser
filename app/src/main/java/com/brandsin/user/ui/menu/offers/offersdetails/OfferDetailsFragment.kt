package com.brandsin.user.ui.menu.offers.offersdetails

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentOfferDetailsBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.menu.offers.OffersItemDetails
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.dialogs.confirm.DialogConfirmFragment
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils

class OfferDetailsFragment : BaseHomeFragment(), Observer<Any?> {

    private lateinit var binding: HomeFragmentOfferDetailsBinding

    private lateinit var viewModel: OfferDetailsViewModel

    private val fragmentArgs: OfferDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_fragment_offer_details,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[OfferDetailsViewModel::class.java]
        binding.viewModel = viewModel

        // Check if Parcelable is not null
        val offerItemDetails: OffersItemDetails? = arguments?.getParcelable("OFFER_ITEM")

        if (offerItemDetails != null) {
            viewModel.getOfferById(offerItemDetails.id!!)
        } else {
            viewModel.getOfferById(fragmentArgs.offerDetails.id!!)
        }

        // viewModel.setOfferDetails(fragmentArgs.offerDetails)

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        binding.tvOfferPrice.paintFlags =
            binding.tvOfferPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        setBarName(getString(R.string.offers))
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.ADDED_TO_CART -> {
                findNavController().navigate(R.id.offer_details_to_cart)
            }

            Codes.NOT_LOGIN -> {
                val bundle = Bundle()
                bundle.putString(
                    Params.DIALOG_CONFIRM_MESSAGE,
                    MyApp.getInstance().getString(R.string.not_login_warnning)
                )
                bundle.putString(
                    Params.DIALOG_CONFIRM_POSITIVE,
                    MyApp.getInstance().getString(R.string.confirm)
                )
                bundle.putString(
                    Params.DIALOG_CONFIRM_NEGATIVE,
                    MyApp.getInstance().getString(R.string.ignore)
                )
                Utils.startDialogActivity(
                    requireActivity(),
                    DialogConfirmFragment::class.java.name,
                    Codes.DIALOG_NOT_LOGIN_REQUEST,
                    bundle
                )
            }

            Codes.CLEAR_CART -> {
                val bundle = Bundle()
                bundle.putString(
                    Params.DIALOG_CONFIRM_MESSAGE,
                    MyApp.getInstance().getString(R.string.add_item_to_cart)
                )
                bundle.putString(
                    Params.DIALOG_CONFIRM_POSITIVE,
                    MyApp.getInstance().getString(R.string.confirm)
                )
                bundle.putString(
                    Params.DIALOG_CONFIRM_NEGATIVE,
                    MyApp.getInstance().getString(R.string.ignore)
                )
                Utils.startDialogActivity(
                    requireActivity(),
                    DialogConfirmFragment::class.java.name,
                    Codes.DIALOG_CONFIRM_REQUEST,
                    bundle
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Codes.DIALOG_NOT_LOGIN_REQUEST && data != null) {
            if (data.hasExtra(Params.DIALOG_CLICK_ACTION)) {
                when {
                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                        PrefMethods.saveIsAskedToLogin(true)
                        startActivity(
                            Intent(requireActivity(), AuthActivity::class.java).putExtra(
                                Const.ACCESS_LOGIN,
                                true
                            )
                        )
                    }
                }
            }
        }
        if (requestCode == Codes.DIALOG_CONFIRM_REQUEST && data != null) {
            if (data.hasExtra(Params.DIALOG_CLICK_ACTION)) {
                when {
                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {

                        viewModel.cartItemsList!!.clear()
                        viewModel.addOfferToCart()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        PrefMethods.getUserData()?.let {
            when {
                PrefMethods.getIsAskedToLogin() -> {
                    PrefMethods.saveIsAskedToLogin(false)
                }
            }
        }
    }
}