package com.brandsin.user.ui.main.order.confirmorder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentConfirmOrderBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.order.confirmorder.coupon.CouponResponseData
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.dialogs.timedialog.DialogOrderTimeFragment
import com.brandsin.user.ui.location.address.ListAddressesActivity
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
import timber.log.Timber


class ConfirmOrderFragment : BaseHomeFragment(), Observer<Any?>, OnMapReadyCallback {

    private lateinit var binding: HomeFragmentConfirmOrderBinding

    private lateinit var viewModel: ConfirmOrderViewModel
    private val paymentViewModel: PaymentViewModel by viewModels()

    private val cartArgs: ConfirmOrderFragmentArgs by navArgs()

    private var shippingType: String = "homeDelivery"// selfPickup

    // private var storeShippingValue: Int = 0

    private var packagingPriceValue: Int? = null

    private lateinit var paymentMethodAdapter: PaymentMethodsAdapter

    private val onPaymentMethodClickCallBack: (paymentMethod: PaymentMethodItem) -> Unit =
        { paymentMethod ->
            viewModel.obsDiscountValue.set(0.0)
            viewModel.discountValue = 0.0
            viewModel.couponResponseData = CouponResponseData()
            viewModel.obsCouponCode.set("")

            // set name of payment method selected
            viewModel.obsPaymentMethod.set(paymentMethod.name)

            /*// check if selected cash method add cod fees of cash
            if (paymentMethod.id == 2) {
                // viewModel.obsExtraFees.set(cartArgs.cartData.cartStoreData!!.extraFees)
                viewModel.obsExtraFees.set(0.0)
            } else {
                // viewModel.obsExtraFees.set(cartArgs.cartData.cartStoreData!!.extraFees)
                viewModel.obsExtraFees.set(0.0)
            }*/

            /*viewModel.obsTotalPrice.set(
                viewModel.obsItemsPrice.get()!!.toDouble() -
                        (viewModel.obsDiscountValue.get() ?: 0.0) +
                        viewModel.obsExtraFees.get()!!.toDouble() +
                        (viewModel.obsPackagingPrice.get() ?: 0.0)
            )*/
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.home_fragment_confirm_order, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ConfirmOrderViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.userCart = cartArgs.cartData

        viewModel.obsItemsPrice.set(cartArgs.cartData.totalItemsPrices)

        setBarName(getString(R.string.confirm_order))

        initView()
        setBtnListener()

        viewModel.storeLat.observe(viewLifecycleOwner) {
            if (it != 0.0) {
                val mapReadyCallback = object : OnMapReadyCallback {
                    override fun onMapReady(googleMap: GoogleMap) {
                        // Your logic when the map is ready
                        // For example, you can set a marker:
                        val location = LatLng(
                            viewModel.storeLat.value ?: -1.0,
                            viewModel.storeLng.value ?: -1.0
                        )
                        Timber.e("location is :$location")
                        googleMap.addMarker(MarkerOptions().position(location))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                    }
                }
                val storeMapFragment =
                    childFragmentManager.findFragmentById(R.id.my_address_map) as SupportMapFragment?
                storeMapFragment!!.getMapAsync(mapReadyCallback)
            }
        }
        // call api get shipping data (home_delivery, smart safe, and self_pickup)
        // viewModel.getShipping()

        // call function to get default address to user
        viewModel.getStoreLocation(cartArgs.cartData.cartStoreData?.storeId ?: -1)
        getDefaultAddress()

        // call api get cod fees if selected cash
        // viewModel.getCodCash()

        // call api get packaging price if selected sending order to some else
        viewModel.getPackagingPrice()

        // call api Wallet Transactions to get current credit of wallet
        paymentViewModel.getWalletTransactions()
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        subscribeData()
    }

    private fun subscribeData() {
        observe(viewModel.apiResponseLiveData) {
            if (it != null) {
                when (it.status) {
                    Status.ERROR_MESSAGE -> {
                        showToast(it.message.toString(), 1)
                    }

                    Status.SUCCESS_MESSAGE -> {
                        showToast(it.message.toString(), 2)
                    }

                    else -> {
                        Timber.e(it.message)
                    }
                }
            }
        }

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        /*viewModel.shippingResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    shippingResponseList = it.data!!

                    *//*it.data.data?.forEach { data ->
                        when (data?.code) {
                            "home_delivery" -> {
                                binding.homeDeliveryValue.text =
                                    data.value.toString() + getString(R.string.currency)
                            }

                            "self_pickup" -> {
                                if (data.value == 0) {
                                    binding.selfPickupValue.text = getString(R.string.free)
                                } else {
                                    binding.selfPickupValue.text =
                                        data.value.toString() + getString(R.string.currency)
                                }
                            }

                            "smart_safe" -> {
                                binding.smartSafeValue.text =
                                    data.value.toString() + getString(R.string.currency)

                            }
                        }
                    }*//*
                }

                is ResponseHandler.Error -> {
                    // show error message
                    viewModel.obsIsEmpty.set(true)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }

        viewModel.getCodFeesCashResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    viewModel.obsCodFeesValue.set(it.data?.data?.toDouble())
                    viewModel.obsExtraFees.set(cartArgs.cartData.cartStoreData!!.extraFees + viewModel.obsShippingValue.get()!! + viewModel.obsCodFeesValue.get()!!)
                }

                is ResponseHandler.Error -> {
                    // show error message
                    viewModel.obsIsEmpty.set(true)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }*/

        viewModel.getPackagingPriceResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    packagingPriceValue = it.data?.data
                    /*viewModel.obsExtraFees.set(
                        cartArgs.cartData.cartStoreData!!.extraFees
                                + viewModel.obsShippingValue.get()!! + viewModel.obsCodFeesValue.get()!!
                    )*/
                }

                is ResponseHandler.Error -> {
                    // show error message
                    viewModel.obsIsEmpty.set(true)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }

        paymentViewModel.getWalletTransactionsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    viewModel.obsCurrentCreditWallet.set(it.data?.currentCredit?.toDouble())
                    paymentMethodAdapter.updateList(getPaymentMethodList())
                }

                is ResponseHandler.Error -> {
                    // show error message
                    viewModel.obsIsEmpty.set(true)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }
    }

    private fun initView() {
        binding.rawPaymentCash.visibility = View.GONE
        binding.rawPaymentVisa.visibility = View.GONE

        viewModel.obsPaymentMethod.set(getString(R.string.cash))

        // Initialize payment method recycler
        binding.rvPaymentMethod.apply {
            paymentMethodAdapter = PaymentMethodsAdapter(onPaymentMethodClickCallBack)
            // paymentMethodAdapter.updateList(getPaymentMethodList())
            adapter = paymentMethodAdapter
        }

        if (viewModel.userCart.cartItems?.get(0)?.pickUpFromStore == 0) {
            binding.cvSelfPickup.visibility = View.GONE
        } else {
            binding.cvHomeDelivery.visibility = View.VISIBLE
        }

        if (viewModel.userCart.cartItems?.get(0)?.cashOnDelivery == 0) {
            binding.cvHomeDelivery.visibility = View.GONE
        } else {
            binding.cvHomeDelivery.visibility = View.VISIBLE
        }

        when (shippingType) {
            "selfPickup" -> { // selfPickup
                binding.cvSelfPickup.setBackgroundResource(R.drawable.back_btn_bg)
                binding.cvHomeDelivery.setBackgroundResource(R.drawable.item_payment_way_bg)
                binding.cvSmartSafe.setBackgroundResource(R.drawable.item_payment_way_bg)

                viewModel.obsExtraFees.set(0.0)
                // viewModel.obsExtraFees.set(cartArgs.cartData.cartStoreData!!.extraFees)
                viewModel.obsTotalPrice.set(
                    viewModel.obsItemsPrice.get()!!.toDouble() +
                            viewModel.obsExtraFees.get()!!.toDouble()
                )
            }

            "homeDelivery" -> {
                binding.cvHomeDelivery.setBackgroundResource(R.drawable.back_btn_bg)
                binding.cvSelfPickup.setBackgroundResource(R.drawable.item_payment_way_bg)
                binding.cvSmartSafe.setBackgroundResource(R.drawable.item_payment_way_bg)

                binding.shippingInfo.text =
                    getString(R.string.your_order_will_be_delivered_to_your_address)
            }

            "smartSafe" -> {
                binding.cvSmartSafe.setBackgroundResource(R.drawable.back_btn_bg)
                binding.cvSelfPickup.setBackgroundResource(R.drawable.item_payment_way_bg)
                binding.cvHomeDelivery.setBackgroundResource(R.drawable.item_payment_way_bg)
            }
        }

        when (viewModel.hasPackagingValue) {
            0 -> { // without packaging
                viewModel.obsExtraFees.set(0.0)
                // viewModel.obsExtraFees.set(cartArgs.cartData.cartStoreData!!.extraFees)
                viewModel.obsTotalPrice.set(
                    viewModel.obsItemsPrice.get()!!.toDouble() +
                            viewModel.obsExtraFees.get()!!.toDouble()
                )
            }
        }

        if (viewModel.obsDeliveryTime.get().isNullOrEmpty()) {
            viewModel.obsDeliveryTime.set(
                "${getString(R.string.during)} " +
                        "${cartArgs.cartData.cartStoreData!!.deliveryTime}" +
                        " ${
                            Utils.translateDeliveryType(
                                requireContext(),
                                viewModel.userCart.cartStoreData?.deliveryType.toString()
                            )
                        }"
            )
        }

        if (PrefMethods.getLanguage() == "ar") {
            binding.edtRecipientMobileNumber.gravity = Gravity.END
        }
    }

    private fun setBtnListener() {
        binding.cbSendingOrder.setOnCheckedChangeListener { _, isChecked -> // buttonView
            if (isChecked) {
                binding.sendOrderAsGiftToUserLayout.visibility = View.VISIBLE
                binding.txtItSentBy.visibility = View.VISIBLE
                binding.packagingLayout.visibility = View.VISIBLE
                binding.PackagingPriceLayout.visibility = View.VISIBLE

                viewModel.orderType = "gift"

                binding.radioBtnPackaging.isChecked = true
                binding.radioBtnWithoutPackaging.isChecked = false

                viewModel.obsPackagingPrice.set(packagingPriceValue?.toDouble())

                viewModel.hasPackagingValue = 1

                viewModel.obsTotalPrice.set(
                    viewModel.obsItemsPrice.get()!!.toDouble() -
                            (viewModel.obsDiscountValue.get() ?: 0.0) +
                            viewModel.obsExtraFees.get()!!.toDouble() +
                            (viewModel.obsPackagingPrice.get() ?: 0.0)
                )
            } else {
                binding.sendOrderAsGiftToUserLayout.visibility = View.GONE
                binding.txtItSentBy.visibility = View.GONE
                binding.packagingLayout.visibility = View.GONE
                binding.PackagingPriceLayout.visibility = View.GONE
                viewModel.hasPackagingValue = 0
                viewModel.obsPackagingPrice.set(0.0)
                viewModel.obsTotalPrice.set(
                    viewModel.obsItemsPrice.get()!!.toDouble() -
                            (viewModel.obsDiscountValue.get() ?: 0.0) +
                            viewModel.obsExtraFees.get()!!.toDouble() +
                            (viewModel.obsPackagingPrice.get() ?: 0.0)
                )
                viewModel.orderType = "normal"
            }
        }

        binding.radioBtnPackaging.setOnCheckedChangeListener { _, isChecked -> // buttonView
            if (isChecked) {
                binding.radioBtnPackaging.isChecked = true
                binding.radioBtnWithoutPackaging.isChecked = false

                viewModel.obsPackagingPrice.set(packagingPriceValue?.toDouble())

                viewModel.hasPackagingValue = 1

                viewModel.obsTotalPrice.set(
                    viewModel.obsItemsPrice.get()!!.toDouble() -
                            (viewModel.obsDiscountValue.get() ?: 0.0) +
                            viewModel.obsExtraFees.get()!!.toDouble() +
                            (viewModel.obsPackagingPrice.get() ?: 0.0)
                )
            }
        }

        binding.radioBtnWithoutPackaging.setOnCheckedChangeListener { _, isChecked -> // buttonView
            if (isChecked) {
                binding.radioBtnPackaging.isChecked = false
                binding.radioBtnWithoutPackaging.isChecked = true

                viewModel.hasPackagingValue = 0

                viewModel.obsPackagingPrice.set(0.0)
                viewModel.obsTotalPrice.set(
                    viewModel.obsItemsPrice.get()!!.toDouble() -
                            (viewModel.obsDiscountValue.get() ?: 0.0) +
                            viewModel.obsExtraFees.get()!!.toDouble() +
                            (viewModel.obsPackagingPrice.get() ?: 0.0)
                )
            }
        }

        binding.icOpenContact.setOnClickListener {
            openContactPicker()
        }

        binding.cvHomeDelivery.setOnClickListener {
            shippingType = "homeDelivery"
            viewModel.isPickFromStore.value = false
            binding.cvHomeDelivery.setBackgroundResource(R.drawable.back_btn_bg)
            binding.cvSelfPickup.setBackgroundResource(R.drawable.item_payment_way_bg)
            binding.cvSmartSafe.setBackgroundResource(R.drawable.item_payment_way_bg)

            binding.shippingInfo.text =
                getString(R.string.your_order_will_be_delivered_to_your_address)

            // storeShippingValue = cartArgs.cartData.cartStoreData!!.deliveryPrice ?: 0

            // viewModel.obsShippingValue.set(storeShippingValue.toDouble())
            viewModel.obsExtraFees.set(cartArgs.cartData.cartStoreData!!.extraFees)
            viewModel.obsTotalPrice.set(
                viewModel.obsItemsPrice.get()!!.toDouble() -
                        (viewModel.obsDiscountValue.get() ?: 0.0) +
                        viewModel.obsExtraFees.get()!!.toDouble() +
                        (viewModel.obsPackagingPrice.get() ?: 0.0)
            )
        }

        binding.cvSelfPickup.setOnClickListener {
            shippingType = "selfPickup"
            binding.cvSelfPickup.setBackgroundResource(R.drawable.back_btn_bg)
            binding.cvHomeDelivery.setBackgroundResource(R.drawable.item_payment_way_bg)
            binding.cvSmartSafe.setBackgroundResource(R.drawable.item_payment_way_bg)
            viewModel.isPickFromStore.value = true
            binding.shippingInfo.text =
                getString(R.string.your_order_will_be_picked_up_from_the_store)

            // storeShippingValue = 0.0
            // viewModel.obsShippingValue.set(0.0)

            viewModel.obsExtraFees.set(0.0)
            viewModel.obsTotalPrice.set(
                viewModel.obsItemsPrice.get()!!.toDouble() -
                        (viewModel.obsDiscountValue.get() ?: 0.0) +
                        viewModel.obsExtraFees.get()!!.toDouble() +
                        (viewModel.obsPackagingPrice.get() ?: 0.0)
            )
        }

        binding.cvSmartSafe.setOnClickListener {
            shippingType = "smartSafe"
            viewModel.isPickFromStore.value = false
            binding.cvSmartSafe.setBackgroundResource(R.drawable.back_btn_bg)
            binding.cvSelfPickup.setBackgroundResource(R.drawable.item_payment_way_bg)
            binding.cvHomeDelivery.setBackgroundResource(R.drawable.item_payment_way_bg)

            binding.shippingInfo.text =
                getString(R.string.your_order_will_be_received_from_the_nearest_redbox_warehouse)

            // storeShippingValue = cartArgs.cartData.cartStoreData!!.deliveryPrice ?: 0

            // viewModel.obsShippingValue.set(storeShippingValue.toDouble())
            viewModel.obsExtraFees.set(cartArgs.cartData.cartStoreData!!.extraFees)
            viewModel.obsTotalPrice.set(
                viewModel.obsItemsPrice.get()!!.toDouble() -
                        (viewModel.obsDiscountValue.get() ?: 0.0) +
                        viewModel.obsExtraFees.get()!!.toDouble() +
                        (viewModel.obsPackagingPrice.get() ?: 0.0)
            )
        }

        binding.btnConfirmOrder.setOnClickListener {
            if (viewModel.orderType == "gift") {
                if (validatePackagingPrice()) {
                    viewModel.onConfirmOrderClicked()
                }
            } else {
                viewModel.onConfirmOrderClicked()
            }
        }
    }

    private fun validatePackagingPrice(): Boolean {
        val isValid = true
        viewModel.recipientName = binding.edtRecipientName.text.toString().trim()
        viewModel.recipientMobileNumber = binding.edtRecipientMobileNumber.text.toString().trim()

        if (viewModel.recipientName.isNullOrEmpty()) {
            binding.edtRecipientName.error = getString(R.string.enter_recipient_name)
            showToast(getString(R.string.enter_recipient_name), 1)
            return false
        } else {
            binding.edtRecipientName.error = null
        }

        if (!isPhoneNumberStartingWith05(viewModel.recipientMobileNumber!!)) {
            binding.edtRecipientMobileNumber.error =
                getString(R.string.phone_number_must_begin_by_05)
            showToast(getString(R.string.phone_number_must_begin_by_05), 1)
            return false
        } else {
            binding.edtRecipientMobileNumber.error = null
        }

        if (viewModel.recipientMobileNumber!!.length < 10) {
            binding.edtRecipientMobileNumber.error =
                getString(R.string.phone_number_must_consist_of_10_number)
            showToast(getString(R.string.phone_number_must_consist_of_10_number), 1)
            return false
        } else {
            binding.edtRecipientMobileNumber.error = null
        }

        if (viewModel.recipientMobileNumber.isNullOrEmpty()) {
            binding.edtRecipientMobileNumber.error = getString(R.string.required)
            showToast(getString(R.string.required), 1)
            return false
        } else {
            binding.edtRecipientMobileNumber.error = null
        }
        return isValid
    }

    private fun isPhoneNumberStartingWith05(phoneNumber: String): Boolean {
        val pattern = "^05\\d{8}\$".toRegex()
        return pattern.matches(phoneNumber)
    }

    private val PICK_CONTACT_REQUEST = 1

    // Function to open the contact picker
    private fun openContactPicker() {
        val contactPickerIntent =
            Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, PICK_CONTACT_REQUEST)
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.EMPTY_TIME -> {
                showToast(getString(R.string.please_select_delivery_time), 1)
            }

            Codes.EMPTY_PAYMENT_METHOD -> {
                showToast(getString(R.string.please_select_payment_method), 1)
            }

            Codes.EMPTY_LOCATION -> {
                showToast(getString(R.string.please_select_your_location), 1)
            }

            Codes.CHANGE_LOCATION -> {
                startActivityForResult(
                    (Intent(requireActivity(), ListAddressesActivity::class.java))
                        .putExtra(Params.DELIVERY_ADDRESSES_FLAG, 2),
                    Codes.SHOW_DELIVERY_ADDRESSES_CODE
                )
            }

            Codes.NO_DEFAULT_LOCATION -> {
                startActivityForResult(
                    (Intent(requireActivity(), ListAddressesActivity::class.java))
                        .putExtra(Params.DELIVERY_ADDRESSES_FLAG, 2),
                    Codes.SHOW_DELIVERY_ADDRESSES_CODE
                )
            }

            Codes.SELECT_TIME_CLICKED -> {
                val bundle = Bundle()
                bundle.putSerializable(Params.STORE_TIMES_RESPONSE, viewModel.storeTimesResponse)
                Utils.startDialogActivity(
                    requireActivity(),
                    DialogOrderTimeFragment::class.java.name,
                    Codes.DIALOG_ORDER_TIME,
                    bundle
                )
            }

            Codes.EMPTY_COUPON -> {
                showToast(getString(R.string.please_enter_coupon_code), 1)
            }

            Codes.CONFIRM_ORDER -> {
                val action = ConfirmOrderFragmentDirections
                    .confirmOrderToReview(
                        viewModel.obsDeliveryTime.get().toString(),
                        viewModel.orderRequestParcelableClass
                    )
                findNavController().navigate(action)
                viewModel.obsCouponCode.set("")
                viewModel.obsDiscountValue.set(0.0)
            }

            /*is PaymentMethodItem -> {
                when (value.id) {
                    1 -> {
                        binding.rawPaymentCash.visibility = View.GONE
                        binding.rawPaymentVisa.visibility = View.GONE
                        viewModel.obsPaymentMethod.set(getString(R.string.visa))

                        // binding.tvPaymentDesc.text = requireActivity().getString(R.string.cash_desc)
                        // viewModel.userCart.cartStoreData!!.paymentMethod="1"
//                        when {
//                            PrefMethods.getLanguage() == "ar" -> {
//                                binding.rawPaymentVisa.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_visa_border)
//                            }
//                            else -> {
//                                binding.rawPaymentVisa.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_wallet_border)
//                            }
//                        }
                    }

                    2 -> {
                        binding.rawPaymentCash.visibility = View.GONE
                        binding.rawPaymentVisa.visibility = View.GONE
                        viewModel.obsPaymentMethod.set(getString(R.string.cash))

                        //binding.tvPaymentDesc.text = requireActivity().getString(R.string.qr_desc)
                        //viewModel.userCart.cartStoreData!!.paymentMethod="2"
//                        when {
//                            PrefMethods.getLanguage() == "ar" -> {
//                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_cash_border)
//                            }
//                            else -> {
//                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_qr_border)
//                            }
//                        }
                    }
//                    3 -> {
//                        binding.rawPaymentCash.visibility = View.VISIBLE
//                        binding.rawPaymentVisa.visibility = View.GONE
//                        binding.tvPaymentDesc.text = requireActivity().getString(R.string.qr_desc)
//                        viewModel.obsPaymentMethod.set("qr code")
//                        when {
//                            PrefMethods.getLanguage() == "ar" -> {
//                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_qr_border)
//                            }
//                            else -> {
//                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_cash_border)
//                            }
//                        }
//                    }
                    4 -> {
                        binding.rawPaymentCash.visibility = View.VISIBLE
                        binding.rawPaymentVisa.visibility = View.GONE
                        viewModel.obsPaymentMethod.set(getString(R.string.my_wallet))
                        *//*binding.tvPaymentDesc.text = requireActivity().getString(R.string.qr_desc)
                        when {
                            PrefMethods.getLanguage() == "ar" -> {
                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_wallet_border)
                            }
                            else -> {
                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_visa_border)
                            }
                        }*//*
                    }
                }
            }*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /* When This page opened navigate user to select his default address from delivery addresses list */
        when (requestCode) {
            Codes.SHOW_DELIVERY_ADDRESSES_CODE -> {
                getDefaultAddress()
                when (data) {
                    null -> {
//                        when {
//                            viewModel.userAddressItem.lat == null || viewModel.userAddressItem.lng == null -> {
//                                findNavController().navigateUp()
//                            }
//                        }
                    }

                    else -> {
                        when {
                            data.hasExtra(Params.ADDRESS_ITEM) -> {
                                val addressItem =
                                    data.getParcelableExtra<AddressListItem>(Params.ADDRESS_ITEM)
                                when {
                                    addressItem != null -> {
                                        viewModel.userAddressItem = addressItem
                                        viewModel.isMapReady.value = true
                                        viewModel.obsAddressTitle.set((addressItem.typeLabel))
                                        viewModel.obsAddressDesc.set("${addressItem.streetName} ${addressItem.landmark}")
                                        viewModel.obsPhoneNumber.set("""${getString(R.string.phone_number)}: ${addressItem.phoneNumber}""")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (requestCode == Codes.DIALOG_ORDER_TIME && data != null) {
            if (data.hasExtra(Params.DIALOG_CLICK_ACTION)) {
                when {
                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                        when {
                            data.hasExtra(Params.DELIVERY_TIME) -> {
                                viewModel.obsDeliveryTime.set(data.getStringExtra(Params.DELIVERY_TIME))
                                viewModel.orderRequestParcelableClass.deliveryTime =
                                    data.getStringExtra(Params.DELIVERY_TIME)
                                viewModel.isTimeChanged = true
                            }
                        }
                    }

                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 0 -> {
                        viewModel.obsDeliveryTime.set(
                            "${getString(R.string.during)} ${viewModel.userCart.cartStoreData!!.deliveryTime} ${
                                viewModel.userCart.cartStoreData!!.deliveryType
                            }"
                        )
                        viewModel.orderRequestParcelableClass.deliveryTime = null
                        viewModel.isTimeChanged = false
                    }
                }
            }
        }

        if (requestCode == PICK_CONTACT_REQUEST && data != null) {
            val contactUri: Uri = data.data!!

            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DATA1
            )

            // Query the contact data
            val cursor =
                requireActivity().contentResolver.query(contactUri, projection, null, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    // Retrieve the phone number
                    val phoneNumber =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    // Use the phoneNumber as needed
                    // Toast.makeText(this, "Selected phone number: $phoneNumber", Toast.LENGTH_SHORT).show()
                    binding.edtRecipientMobileNumber.setText(phoneNumber)
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
                        viewModel.userAddressItem.lat!!.toDouble(),
                        viewModel.userAddressItem.lng!!.toDouble()
                    )
                    val option = MarkerOptions().position(latLng)
                    googleMap.addMarker(option)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
                }

                else -> {}
            }
        }

        when {
            PrefMethods.getUserLocation()!!.lat != null && PrefMethods.getUserLocation()!!.lng != null -> {
                googleMap.clear()
                val latLng = LatLng(
                    PrefMethods.getUserLocation()!!.lat!!.toDouble(),
                    PrefMethods.getUserLocation()!!.lng!!.toDouble()
                )
                val option = MarkerOptions().position(latLng)
                googleMap.addMarker(option)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
            }
        }
    }

    fun getDefaultAddress() {
        when {
            PrefMethods.getDefaultAddress() == null -> {
                binding.addAddress.visibility = View.VISIBLE
                binding.locationLayout.visibility = View.GONE
            }

            else -> {
                if (PrefMethods.getDefaultAddress()!!.phoneNumber != null) {
                    binding.addAddress.visibility = View.GONE
                    binding.locationLayout.visibility = View.VISIBLE
                    viewModel.phoneNumber = PrefMethods.getDefaultAddress()?.phoneNumber.toString()
                    viewModel.obsPhoneNumber.set("""${getString(R.string.phone_number)}: ${viewModel.phoneNumber}""")
                    viewModel.obsAddressDesc.set(PrefMethods.getDefaultAddress()!!.streetName + " " + PrefMethods.getDefaultAddress()!!.landmark)
                    viewModel.obsAddressTitle.set(PrefMethods.getDefaultAddress()!!.type)
                    viewModel.isAddress.value = false
                    viewModel.userAddressItem.run {
                        id = PrefMethods.getDefaultAddress()?.id
                        lat = PrefMethods.getDefaultAddress()?.lat
                        lng = PrefMethods.getDefaultAddress()?.lng
                        type = PrefMethods.getDefaultAddress()?.type
                        typeLabel = PrefMethods.getDefaultAddress()?.typeLabel
                        phoneNumber = PrefMethods.getDefaultAddress()?.phoneNumber
                        streetName = PrefMethods.getDefaultAddress()?.streetName
                        cityId = PrefMethods.getDefaultAddress()?.cityId
                        countryId = PrefMethods.getDefaultAddress()?.countryId
                        countryName = PrefMethods.getDefaultAddress()?.countryName
                        code = PrefMethods.getDefaultAddress()?.code
                        lastName = PrefMethods.getDefaultAddress()?.lastName
                        isDefault = PrefMethods.getDefaultAddress()?.isDefault
                        status = PrefMethods.getDefaultAddress()?.status
                        landmark = PrefMethods.getDefaultAddress()?.landmark
                    }
                } else {
                    binding.addAddress.visibility = View.VISIBLE
                    binding.locationLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun getPaymentMethodList(): ArrayList<PaymentMethodItem> {
        val paymentMethodList = ArrayList<PaymentMethodItem>()
        paymentMethodList.add(PaymentMethodItem(2, R.drawable.ic_cash, getString(R.string.cash)))
        paymentMethodList.add(PaymentMethodItem(1, R.drawable.ic_visa, getString(R.string.visa)))
        paymentMethodList.add(
            PaymentMethodItem(
                4,
                R.drawable.ic_wallet,
                getString(R.string.my_wallet),
                viewModel.obsCurrentCreditWallet.get()
            )
        )

        return paymentMethodList
    }
}