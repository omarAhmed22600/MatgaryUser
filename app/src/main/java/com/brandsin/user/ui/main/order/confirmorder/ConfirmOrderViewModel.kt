package com.brandsin.user.ui.main.order.confirmorder

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.menu.settings.SettingResponse
import com.brandsin.user.model.menu.settings.ShippingResponse
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.confirmorder.coupon.ApplyCouponResponse
import com.brandsin.user.model.order.confirmorder.coupon.CouponResponseData
import com.brandsin.user.model.order.confirmorder.createorder.OrderRequestParcelableClass
import com.brandsin.user.model.order.storedetails.StoreDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreTimesResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.requestCall
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ConfirmOrderViewModel : BaseViewModel() {
    var orderRequestParcelableClass = OrderRequestParcelableClass()

    var storeTimesResponse = StoreTimesResponse()

    var obsDeliveryTime = ObservableField<String>()
    var obsPaymentMethod = ObservableField<String>()
    var obsCurrentCreditWallet = ObservableField<Double>()

    var isTimeChanged: Boolean = false

    var userCart = UserCart()

    /* All Prices variables */
    var couponResponseData = CouponResponseData()

    // var obsShippingValue = ObservableField(0.0)
    // var obsCodFeesValue = ObservableField(0.0)
    var obsItemsPrice = ObservableField(0.0)
    var obsExtraFees = ObservableField(0.0)
    var obsPackagingPrice = ObservableField(0.0)
    var obsTotalPrice = ObservableField(0.0)
    var obsDiscountValue = ObservableField(0.0)
    val isPickFromStore = MutableLiveData(false)
    private var discountId: Int = -1
    var discountValue: Double = 0.0
    private var discountType: String = ""

    var orderType: String? = "normal"
    var recipientName: String? = null
    var recipientMobileNumber: String? = null
    var hasPackagingValue: Int = 0

    private val storeIds: ArrayList<Int> = ArrayList()
    private val productsIds: ArrayList<Int> = ArrayList()
    private val offersIds: ArrayList<Int> = ArrayList()

    var obsCouponCode = ObservableField<String>()
    var obsVisible = ObservableField<Boolean>()

    var userAddressItem = AddressListItem()
    var isMapReady = MutableLiveData<Boolean>()
    var isAddress = MutableLiveData<Boolean>()

    var obsPhoneNumber = ObservableField<String>()
    var obsAddressDesc = ObservableField<String>()
    var obsAddressTitle = ObservableField<String>()
    var obsShowOrderPrices = ObservableBoolean(true)

    var phoneNumber: String? = null

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getShippingResponse: MutableLiveData<ResponseHandler<ShippingResponse?>> =
        MutableLiveData()
    val shippingResponse: LiveData<ResponseHandler<ShippingResponse?>> =
        _getShippingResponse.toSingleEvent()

    private val _getSettingResponse: MutableLiveData<ResponseHandler<SettingResponse?>> =
        MutableLiveData()
    val getSettingResponse: LiveData<ResponseHandler<SettingResponse?>> =
        _getSettingResponse.toSingleEvent()

    private val _getPackagingPriceResponse: MutableLiveData<ResponseHandler<SettingResponse?>> =
        MutableLiveData()
    val getPackagingPriceResponse: LiveData<ResponseHandler<SettingResponse?>> =
        _getPackagingPriceResponse.toSingleEvent()

    val storeLat = MutableLiveData(0.0)
    val storeLng = MutableLiveData(0.0)

    init {
        getWorkingHours()
    }

    fun showHidePrices() {
        when {
            obsShowOrderPrices.get() -> {
                obsShowOrderPrices.set(false)
            }

            else -> {
                obsShowOrderPrices.set(true)
            }
        }
    }

    private fun getWorkingHours() {
        requestCall<StoreTimesResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext PrefMethods.getUserCart()?.cartStoreData?.storeId?.let {
                    getApiRepo().getStoreWorkingHours(it)
                }
            }
        })
        { res ->
            when (res?.isSuccess) {
                true -> {
                    res.let {
                        storeTimesResponse = res
                    }
                }

                else -> {}
            }
        }
    }

    fun onApplyCouponClicked() {
        setClickable()
        when {
            obsCouponCode.get() == null -> {
                setValue(Codes.EMPTY_COUPON)
            }

            else -> {
                applyCoupon()
            }
        }
    }

    fun onSelectTimeClicked() {
        if (storeTimesResponse.storeTimesList.isNullOrEmpty()) {
            getWorkingHours()
        } else {
            setClickable()
            setValue(Codes.SELECT_TIME_CLICKED)
        }
    }

    fun changeLocationClicked() {
        setClickable()
        setValue(Codes.CHANGE_LOCATION)
    }

    private fun applyCoupon() {
        obsIsVisible.set(true)
        userCart.cartItems!!.forEach {
            when (it.isOffer) {
                true -> {
                    offersIds.add(it.productId!!)
                }

                else -> {
                    productsIds.add(it.productId!!)
                }
            }
        }
        storeIds.add(userCart.cartStoreData!!.storeId!!)

        requestCall<ApplyCouponResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().applyCoupon(
                    PrefMethods.getUserData()!!.id!!,
                    PrefMethods.getLanguage(),
                    obsCouponCode.get().toString(),
                    storeIds.toString(),
                    productsIds.toString(),
                    productsIds.toString()
                )
            }
        })
        { res ->

            obsIsVisible.set(false)
            when (res!!.isSuccess) {
                true -> {
                    res.let {
                        apiResponseLiveData.value =
                            ApiResponse.successMessage(getString(R.string.coupon_applied_successfully))
                        couponResponseData = res.couponResponseData!!
                        discountId = couponResponseData.id!!
                        when (res.couponResponseData.type) {
                            "fixed" -> {
                                discountValue = res.couponResponseData.value!!.toDouble()
                                discountType = "fixed"
                                obsDiscountValue.set(discountValue)
                            }

                            "percentage" -> {
                                discountValue =
                                    obsItemsPrice.get()!!.toDouble() * (res.couponResponseData.value!!.toDouble() / 100)

                                discountType = "percentage"
                                obsDiscountValue.set(discountValue)
                            }
                        }
                        updatePricesAfterDiscount()
                    }
                }

                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message.toString())
                }
            }
        }
    }


    private fun updatePricesAfterDiscount() {
        when {
            couponResponseData.minCartTotal != null -> {
                when {
                    obsItemsPrice.get()!!.toDouble() >
                            couponResponseData.minCartTotal!!.toDouble() -> {
                        discountValue =
                            when {
                                /*discountValue > couponResponseData.maxDiscountValue!!.toDouble() -> {
                                    couponResponseData.maxDiscountValue!!.toDouble()
                                }*/

                                else -> {
                                    couponResponseData.value!!.toDouble()
                                }
                            }
                        // obsItemsPrice.set(obsItemsPrice.get()!!.toDouble() - discountValue)
                        // obsItemsPrice.set(obsItemsPrice.get()!!.toDouble() - obsDiscountValue.get()!!.toDouble())
                        // obsTotalPrice.set(obsItemsPrice.get()!!.toDouble() + obsExtraFees.get()!!.toDouble() + obsShippingValue.get()!!.toDouble() + obsCodFeesValue.get()!!.toDouble())
                        obsTotalPrice.set(
                            obsItemsPrice.get()!!.toDouble() + obsExtraFees.get()!!.toDouble()
                                    + (obsPackagingPrice.get() ?: 0.0) -
                                    (obsDiscountValue.get() ?: 0.0)
                        )
                        apiResponseLiveData.value =
                            ApiResponse.successMessage(getString(R.string.coupon_applied_successfully))
                    }

                    else -> {
                        discountId = -1
                        discountValue = 0.0
                        obsDiscountValue.set(discountValue)
                        apiResponseLiveData.value =
                            ApiResponse.errorMessage(getString(R.string.impossible_to_apply_coupon))
                    }
                }
            }

            else -> {
                // discountValue = couponResponseData.value!!.toDouble()
                obsDiscountValue.set(discountValue)
                // obsItemsPrice.set(obsItemsPrice.get()!!.toDouble() - discountValue)
                obsTotalPrice.set(
                    obsItemsPrice.get()!!.toDouble() + obsExtraFees.get()!!.toDouble() +
                            (obsPackagingPrice.get() ?: 0.0) - (obsDiscountValue.get() ?: 0.0)
                )
                apiResponseLiveData.value = ApiResponse.success(getString(R.string.coupon_applied_successfully))
            }
        }
    }

    fun onConfirmOrderClicked() {
        when {
            obsPaymentMethod.get() == null -> {
                setValue(Codes.EMPTY_PAYMENT_METHOD)
            }

            userAddressItem.lat == null -> {
                setValue(Codes.EMPTY_LOCATION)
            }

            else -> {
                orderRequestParcelableClass.addressId = userAddressItem.id
                orderRequestParcelableClass.lat = userAddressItem.lat!!.toDouble()
                orderRequestParcelableClass.lng = userAddressItem.lng!!.toDouble()
                orderRequestParcelableClass.addressType = userAddressItem.typeLabel
                orderRequestParcelableClass.streetName = userAddressItem.streetName
                orderRequestParcelableClass.lansmark = userAddressItem.landmark
                orderRequestParcelableClass.deliveryTime = obsDeliveryTime.get()
                orderRequestParcelableClass.isTimeChanged = isTimeChanged
                orderRequestParcelableClass.orderCost = obsTotalPrice.get()
                orderRequestParcelableClass.extraFees = obsExtraFees.get()
                orderRequestParcelableClass.orderItems = userCart.cartItems
                orderRequestParcelableClass.phoneNumber = userAddressItem.phoneNumber
                orderRequestParcelableClass.paymentMethod = obsPaymentMethod.get()
                orderRequestParcelableClass.discountId = discountId
                orderRequestParcelableClass.discountType = discountType
                orderRequestParcelableClass.discountValue = discountValue
                orderRequestParcelableClass.addressStatus = userAddressItem.status

                orderRequestParcelableClass.orderType = orderType

                orderRequestParcelableClass.recipientName = recipientName.toString()
                orderRequestParcelableClass.recipientMobileNumber = recipientMobileNumber.toString()
                orderRequestParcelableClass.hasPackaging = hasPackagingValue.toString()
                orderRequestParcelableClass.packagingPrice = obsPackagingPrice.get()

                orderRequestParcelableClass.currentCreditWallet = obsCurrentCreditWallet.get()

                setValue(Codes.CONFIRM_ORDER)
            }
        }
    }

    fun onAddAddressClicked() {
        setValue(Codes.NO_DEFAULT_LOCATION)
    }

    /*fun getShipping() {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getShipping(
                    "Home_Delivery",
                    "Smart_Safe",
                    "Self_Pickup",
                    PrefMethods.getLanguage()
                )
            }.collect {
                _getShippingResponse.value = it
            }
        }
    }*/

    fun getCodCash() {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getCodFeesCash(
                    "COD_Fees",
                    PrefMethods.getLanguage()
                )
            }.collect {
                _getSettingResponse.value = it
            }
        }
    }

    fun getPackagingPrice() {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getPackagingPrice(
                    "Packaging_price",
                    PrefMethods.getLanguage()
                )
            }.collect {
                _getPackagingPriceResponse.value = it
            }
        }
    }
    fun getStoreLocation(storeId:Int) {
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<StoreDetailsResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getStoreDetails(
                    storeId,
                )
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    obsIsLoading.set(false)
                    obsIsFull.set(true)
                    storeLat.value = res.storeDetailsData?.lat?.toDouble()
                    storeLng.value = res.storeDetailsData?.lng?.toDouble()
                    Timber.e("store location : ${storeLat.value},${storeLng.value}")
                    // storeData = res.storeDetailsData!!
                }

                else -> {}
            }
        }
    }
}
