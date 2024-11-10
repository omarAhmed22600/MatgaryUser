package com.brandsin.user.ui.main.order.orderreview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentOrderReviewBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.confirmorder.createorder.CreateOrderResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.dialogs.sendcode.DialogSendCodeFragment
import com.brandsin.user.ui.dialogs.successorder.DialogOrderSuccessFragment
import com.brandsin.user.ui.dialogs.verifycode.DialogVerifyCodeFragment
import com.brandsin.user.ui.menu.payment.PaymentViewModel
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.payment.paymentsdk.PaymentSdkActivity
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkError
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode
import com.payment.paymentsdk.integrationmodels.PaymentSdkShippingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTokenFormat
import com.payment.paymentsdk.integrationmodels.PaymentSdkTokenise
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionType
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackQueryInterface
import com.payment.paymentsdk.sharedclasses.model.response.TransactionResponseBody
import timber.log.Timber

@Suppress("DEPRECATION")
class OrderReviewFragment : BaseHomeFragment(), Observer<Any?>, OnMapReadyCallback,
    CallbackPaymentInterface, CallbackQueryInterface {

    private lateinit var binding: HomeFragmentOrderReviewBinding
    private lateinit var viewModel: OrderReviewViewModel
    private val paymentViewModel: PaymentViewModel by viewModels()

    private val orderArgs: OrderReviewFragmentArgs by navArgs()

    var deliveryTime = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment_order_review, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[OrderReviewViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        setBarName(requireActivity().getString(R.string.order_review))

        deliveryTime = orderArgs.time
        val orderData = orderArgs.orderData
        viewModel.orderData = orderData
        viewModel.obsDeliveryTime.set(orderData.deliveryTime)
        viewModel.obsPaymentMethod.set(orderData.paymentMethod)
        viewModel.obsCurrentCreditWallet.set(orderData.currentCreditWallet)
        viewModel.obsOrderAmount.set(orderData.orderCost.toString())
        viewModel.obsPackagingPrice.set(orderData.packagingPrice.toString())
        viewModel.orderItemsAdapter.updateList(orderData.orderItems as ArrayList<CartItem>)
        viewModel.isTimeChanged = orderData.isTimeChanged
        viewModel.isMapReady.value = true
        when (orderData.addressStatus) {
            1 -> {
                viewModel.isVerified = true
            }
        }

        if (orderData.discountValue == 0.0) {
            binding.tvDiscount.visibility = View.GONE
            binding.discount.visibility = View.GONE
            binding.tvDeliveryFees.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            binding.deliveryFees.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        }

        if (orderData.hasPackaging?.toInt() == 0) {
            binding.txtPackaging.visibility = View.GONE
            binding.packagingPrice.visibility = View.GONE
            binding.tvTotalPrices.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            binding.tvTotal.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        } else {
            binding.txtPackaging.visibility = View.VISIBLE
            binding.packagingPrice.visibility = View.VISIBLE
        }

        if (viewModel.obsDeliveryTime.get().isNullOrEmpty()) {
            binding.tvTime.text = deliveryTime
        } else {
            binding.tvTime.text = viewModel.obsDeliveryTime.get()
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        setBtnListener()
        subscribeData()
    }

    private var isPayment = false
    private var subTotalFromWallet = 0.0

    private fun setBtnListener() {
        binding.btnConfirm.setOnClickListener {
            when {
                /*!viewModel.isVerified -> {
                    viewModel.setValue(Codes.VERIFY_PHONE)
                }*/
                else -> {
                    if (viewModel.obsPaymentMethod.get().equals(getString(R.string.visa)) ||
                        viewModel.obsPaymentMethod.get().equals(getString(R.string.visa))
                    ) {
                        /*val intent = Intent(requireActivity(), MFPaymentActivity::class.java)
                        intent.putExtra("amountMF", viewModel.orderData.orderCost)
                        startActivityForResult(intent, 3)*/

                        configurationPayTabsPayment(viewModel.obsOrderAmount.get()?.toDouble()!!)

                    } else if (viewModel.obsPaymentMethod.get().equals(getString(R.string.cash))) {
                        viewModel.createOrder(context)
                    } else if (viewModel.obsPaymentMethod.get()
                            .equals(getString(R.string.my_wallet))
                    ) {
                        // viewModel.createOrder(context)
                        if (viewModel.obsOrderAmount.get()?.toDouble()!!
                            > viewModel.obsCurrentCreditWallet.get()!!
                        ) {
                            subTotalFromWallet = (viewModel.obsOrderAmount.get()
                                ?.toDouble()!! - viewModel.obsCurrentCreditWallet.get()!!)
                            isPayment = true
                            configurationPayTabsPayment(subTotalFromWallet)
                        } else {
                            isPayment = false
                            viewModel.createOrder(requireContext())
                        }
                    }
                }
            }
        }
    }


    private fun subscribeData() {
        observe(viewModel.apiResponseLiveData) {
            when (it.status) {
                Status.ERROR_MESSAGE -> {
                    showToast(it.message.toString(), 1)
                }

                Status.SUCCESS_MESSAGE -> {
                    showToast(it.message.toString(), 2)
                }

                Status.SUCCESS -> {
                    if (viewModel.obsPaymentMethod.get().equals(getString(R.string.my_wallet))) {
                        if (isPayment) {
                            paymentViewModel.updateCreditDecrease(subTotalFromWallet)
                        }
                        /*else {
                            paymentViewModel.updateCreditDecrease(
                                viewModel.obsOrderAmount.get()?.toDouble()
                            )
                        }*/
                    }
                    when (it.data) {
                        is CreateOrderResponse -> {
                            val bundle = Bundle()
                            bundle.putInt(Params.ORDER_ID, it.data.orders!![0]!!.id!!)
                            Utils.startDialogActivity(
                                requireActivity(),
                                DialogOrderSuccessFragment::class.java.name,
                                Codes.DIALOG_ORDER_SUCCESS,
                                bundle
                            )
                        }

                        is Int -> {
                            if (it.data == 1) {
                                findNavController().navigate(R.id.review_to_my_orders)
                            }
                        }
                    }
                }

                else -> {
                    Timber.e(it.message)
                }
            }
        }

        paymentViewModel.updateCreditDecreaseResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // viewModel.createOrder(requireContext())
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

    override fun onChanged(value: Any?) {
        when (value) {
            null -> return
            else -> when (value) {
                Codes.VERIFY_PHONE -> {
                    val bundle = Bundle()
                    bundle.putString(Params.VERIFIED_PHONE, viewModel.orderData.phoneNumber)
                    bundle.putInt(Params.ADDRESS_ID, viewModel.orderData.addressId!!)
                    Utils.startDialogActivity(
                        requireActivity(),
                        DialogSendCodeFragment::class.java.name,
                        Codes.DIALOG_SEND_CODE_REQUEST,
                        bundle
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /* Callback from dialog order success */
        when {
            requestCode == Codes.DIALOG_ORDER_SUCCESS && data != null -> {
                when {
                    data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                        when {
                            data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                                requireActivity().finishAffinity()
                            }

                            data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 2 -> {
                                when {
                                    data.getIntExtra(Params.ORDER_ID, 0) != -1 -> {
                                        val action = OrderReviewFragmentDirections.reviewToDetails(
                                            data.getIntExtra(
                                                Params.ORDER_ID,
                                                0
                                            )
                                        )
                                        findNavController().navigate(action)
                                    }

                                    else -> {
                                        startActivity(
                                            Intent(
                                                requireActivity(),
                                                HomeActivity::class.java
                                            )
                                        )
                                        requireActivity().finishAffinity()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /* Callback from dialog send code */
        when {
            requestCode == Codes.DIALOG_SEND_CODE_REQUEST && data != null -> {
                when {
                    data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                        when {
                            data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                val bundle = Bundle()
                                when {
                                    data.getStringExtra(Params.VERIFIED_PHONE) != null -> {
                                        bundle.putString(
                                            Params.VERIFIED_PHONE, data.getStringExtra(
                                                Params.VERIFIED_PHONE
                                            )
                                        )
                                    }

                                    else -> {
                                        bundle.putString(
                                            Params.VERIFIED_PHONE,
                                            viewModel.orderData.phoneNumber
                                        )
                                    }
                                }
                                when {
                                    data.getStringExtra(Params.VERIFIED_CODE) != null -> {
                                        bundle.putString(
                                            Params.VERIFIED_PHONE, data.getStringExtra(
                                                Params.VERIFIED_PHONE
                                            )
                                        )
                                        showToast(data.getStringExtra(Params.VERIFIED_CODE)!!, 2)
                                    }
                                }
                                bundle.putInt(
                                    Params.ADDRESS_ID, data.getIntExtra(
                                        Params.ADDRESS_ID,
                                        0
                                    )
                                )
                                Utils.startDialogActivity(
                                    requireActivity(),
                                    DialogVerifyCodeFragment::class.java.name,
                                    Codes.DIALOG_VERIFY_CODE_REQUEST,
                                    bundle
                                )
                            }
                        }
                    }
                }
            }
        }

        /* Callback from verify code*/
        when {
            requestCode == Codes.DIALOG_VERIFY_CODE_REQUEST && data != null -> {
                when {
                    data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                        when {
                            data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                val addressItem = PrefMethods.getDefaultAddress()
                                addressItem!!.status = 1
                                PrefMethods.saveDefaultAddress(addressItem)
                                viewModel.isVerified = true
                                viewModel.createOrder(context)
                            }
                        }
                    }
                }
            }
        }

        if (data != null) {
            when (requestCode) {
                3 -> {
                    val success = data.getBooleanExtra("success", false)
                    if (success)
                        viewModel.createOrder(context)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        observe(viewModel.isMapReady) {
            when (it) {
                true -> {
                    googleMap.clear()
                    val latLng = LatLng(
                        viewModel.orderData.lat!!.toDouble(),
                        viewModel.orderData.lng!!.toDouble()
                    )
                    val option = MarkerOptions().position(latLng)
                    googleMap.addMarker(option)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
                }

                else -> {}
            }
        }
    }

    private fun configurationPayTabsPayment(totalAmount: Double) {
        val profileId = ""// PROFILE_ID
        val serverKey = ""
        val clientLey = ""

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
        val amount = totalAmount

        val tokeniseType = PaymentSdkTokenise.NONE // tokenise is off
        //or PaymentSdkTokenise.USER_OPTIONAL // tokenise is optional as per user approval
        //or PaymentSdkTokenise . USER_MANDATORY // tokenise is forced as per user approval
        //or PaymentSdkTokenise . MERCHANT_MANDATORY // tokenise is forced without user approval
        //or PaymentSdkTokenise . USER_OPTIONAL_DEFAULT_ON // tokenise is optional as per user approval default value true

        val transType = PaymentSdkTransactionType.SALE // or PaymentSdkTransactionType.AUTH
        // val transType = PaymentSdkTransactionType.AUTH


        val tokenFormat = PaymentSdkTokenFormat.Hex32Format()
//or PaymentSdkTokenFormat . NoneFormat ()
//or PaymentSdkTokenFormat . AlphaNum20Format ()
//or PaymentSdkTokenFormat . Digit22Format ()
//or PaymentSdkTokenFormat . Digit16Format ()
//or PaymentSdkTokenFormat . AlphaNum32Format ()

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

        // startCardPayment(context = requireActivity(), ptConfigData = configData, callback = this)
        // or
        // startSamsungPayment(requireActivity(), configData, "samsungpay token", callback = this)

        PaymentSdkActivity.startPaymentWithSavedCards(
            context = requireActivity(),
            ptConfigData = configData,
            support3DS = true,
            callback = this
        )
    }

    override fun onCancel() {
        Toast.makeText(requireActivity(), "Payment Cancelled", Toast.LENGTH_SHORT).show()
        Log.d("TAG_PAY_TABS", "onPaymentCancel: 111")
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

    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
        Log.d("TAG_PAY_TABS", "onPaymentFinish: $paymentSdkTransactionDetails")
        Toast.makeText(
            requireActivity(),
            "${paymentSdkTransactionDetails.paymentResult?.responseMessage}",
            Toast.LENGTH_SHORT
        ).show()
        viewModel.createOrder(context)
    }

    override fun onPaymentCancel() {
        Toast.makeText(requireActivity(), "onPaymentCancel", Toast.LENGTH_SHORT).show()
        Log.d("TAG_PAY_TABS", "onPaymentCancel:222")
    }
}