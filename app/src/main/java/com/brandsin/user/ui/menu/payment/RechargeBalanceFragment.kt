package com.brandsin.user.ui.menu.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentRechargeBalanceBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.payment.paymentsdk.PaymentSdkActivity
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkError
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode
import com.payment.paymentsdk.integrationmodels.PaymentSdkShippingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionType
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackQueryInterface
import com.payment.paymentsdk.sharedclasses.model.response.TransactionResponseBody

class RechargeBalanceFragment : BottomSheetDialogFragment(), CallbackPaymentInterface,
    CallbackQueryInterface {

    private var _binding: FragmentRechargeBalanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PaymentViewModel by viewModels()

    private var shippingAmount: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRechargeBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fun clicked
        setBtnListener()
        subscribeData()
    }

    private fun setBtnListener() {
        binding.btnBuyNow.setOnClickListener {
            if (validate()){
                configurationPayTabsPayment()
            }
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun validate(): Boolean {
        val isValid = true

        shippingAmount = binding.edtShippingAmount.text.toString().trim()

        if (shippingAmount.isNullOrEmpty()) {
            binding.edtShippingAmount.error = getString(R.string.enter_shipping_amount)
            showToast(getString(R.string.enter_shipping_amount), 1)
            return false
        } else {
            binding.edtShippingAmount.error = null
        }

        return isValid
    }

    fun showToast(msg: String, type: Int) {
        // Success 2
        // False  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(
            requireActivity(),
            DialogToastFragment::class.java.name,
            Codes.DIALOG_TOAST_REQUEST,
            bundle
        )
    }

    private fun subscribeData() {
        viewModel.updateCreditIncreaseResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view

                }

                is ResponseHandler.Error -> {
                    // show error message

                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                }

                else -> {}
            }
        }
    }

    private fun configurationPayTabsPayment() {
        val profileId = "104321"// PROFILE_ID
        val serverKey = "SZJN699M9M-JHBMRW9TKG-66LHZRD9MG"
        val clientLey = "C6KMVT-MPDD6H-2NRVP7-QDMH6N"

        val locale = if (PrefMethods.getLanguage() == "ar") {
            PaymentSdkLanguageCode.AR
        } else {
            PaymentSdkLanguageCode.EN
        }
        // val locale = PaymentSdkLanguageCode.EN
        val screenTitle = "Pay with card"
        val cartId = "123456"
        val cartDesc = "cart description"
        val currency = "SAR"
        val amount = shippingAmount?.toDouble()

        val transType = PaymentSdkTransactionType.SALE // or PaymentSdkTransactionType.AUTH
        // val transType = PaymentSdkTransactionType.AUTH

        val billingData = PaymentSdkBillingDetails(
            "Dubai",
            "ae",
            PrefMethods.getUserData()?.email, // "email1@domain.com"
            PrefMethods.getUserData()?.full_name, // "name"
            PrefMethods.getUserData()?.phoneNumber, // "phone"
            "Dubai",
            "address",
            "12345"// "zip"
        )

        val shippingData = PaymentSdkShippingDetails(
            "Dubai", // "City",
            "ae",
            PrefMethods.getUserData()?.email,
            PrefMethods.getUserData()?.full_name,
            "${PrefMethods.getUserData()?.phoneNumber}",
            "Dubai",
            "address",
            "12345"
        )
        val configData =
            PaymentSdkConfigBuilder(
                profileId,
                serverKey,
                clientLey,
                amount ?: 0.0,
                currency
            )
                .setCartDescription(cartDesc)
                .setLanguageCode(locale)
                // .setMerchantIcon(resources.getDrawable(R.drawable.))
                .setBillingData(billingData)
                .setMerchantCountryCode("SA") // ISO alpha 2
                .setShippingData(shippingData)
                .setCartId(cartId)
                .setTransactionType(transType)
                .showBillingInfo(false)
                .showShippingInfo(false) // if true show shipping details
                .forceShippingInfo(true)
                .setScreenTitle(screenTitle)
                .isDigitalProduct(false)
                .build()

        PaymentSdkActivity.startPaymentWithSavedCards(
            context = requireActivity(),
            ptConfigData = configData,
            support3DS = true,
            callback = this
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCancel() {
        Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_SHORT).show()
        Log.d("TAG_PAY_TABS", "onPaymentCancel:")
    }

    override fun onError(error: PaymentSdkError) {
        Log.d("TAG_PAY_TABS", "onError: $error")
        Toast.makeText(requireActivity(), "${error.msg}", Toast.LENGTH_SHORT).show()
    }

    override fun onResult(transactionResponseBody: TransactionResponseBody) {
        Toast.makeText(
            requireActivity(),
            "${transactionResponseBody.paymentResult?.responseMessage}",
            Toast.LENGTH_SHORT
        ).show()
        Log.d("TAG_PAY_TABS", "onResult: $transactionResponseBody")
    }

    override fun onPaymentCancel() {
        Toast.makeText(requireActivity(), "onPaymentCancel", Toast.LENGTH_SHORT).show()
        Log.d("TAG_PAY_TABS", "onPaymentCancel:")
    }

    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
        Log.d("TAG_PAY_TABS", "onPaymentFinish: $paymentSdkTransactionDetails")
        Toast.makeText(
            requireActivity(),
            "${paymentSdkTransactionDetails.paymentResult?.responseMessage}",
            Toast.LENGTH_SHORT
        ).show()
        viewModel.updateCreditIncrease(shippingAmount)
    }
}