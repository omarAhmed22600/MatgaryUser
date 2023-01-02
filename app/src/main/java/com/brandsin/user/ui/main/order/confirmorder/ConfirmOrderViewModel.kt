package com.brandsin.user.ui.main.order.confirmorder

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.confirmorder.coupon.ApplyCouponResponse
import com.brandsin.user.model.order.confirmorder.coupon.CouponResponseData
import com.brandsin.user.model.order.confirmorder.createorder.*
import com.brandsin.user.model.order.storedetails.*
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.MyApp.Companion.context
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfirmOrderViewModel : BaseViewModel()
{
    var orderRequestParcelableClass = OrderRequestParcelableClass()

    var storeTimesResponse = StoreTimesResponse()
    var obsDeliveryTime = ObservableField<String>()
    var obsPaymentMethod = ObservableField<String>()

    var isTimeChanged : Boolean = false

    var userCart = UserCart()

    /* All Prices variables */
    var couponResponseData = CouponResponseData()
    var obsItemsPrice = ObservableField(0.0)
    var obsExtraFees = ObservableField(0.0)
    var obsTotalPrice = ObservableField(0.0)

    var discountId : Int = -1
    var discountValue : Double = 0.0
    var discountType : String = ""

    private val storeIds: ArrayList<Int> = ArrayList()
    private val productsIds: ArrayList<Int> = ArrayList()
    private val offersIds: ArrayList<Int> = ArrayList()

    var obsCouponCode = ObservableField<String>()

    var paymentWaysAdapter = PaymentWaysAdapter()
    var obsVisible = ObservableField<Boolean>()
    var userAddressItem = AddressListItem()
    var isMapReady = MutableLiveData<Boolean>()
    var isAddress = MutableLiveData<Boolean>()
    var obsPhoneNumber = ObservableField<String>()
    var obsAddressDesc = ObservableField<String>()
    var obsAddressTitle = ObservableField<String>()
    var phoneNumber : String? = null
    var obsShowOrderPrices = ObservableBoolean(true)

    private fun getPaymentWays() {
        val paymentWay1 = PaymentWayItem(1, getString(R.string.visa))
        val paymentWay2 = PaymentWayItem(2, getString(R.string.cash))
        val paymentWay3 = PaymentWayItem(3, "Qr code")
        val paymentWay4 = PaymentWayItem(4, "محفظتي")

        val paymentWaysList: MutableList<PaymentWayItem> = mutableListOf()


        paymentWaysList.add(paymentWay1)
        paymentWaysList.add(paymentWay2)

//        paymentWaysList.add(paymentWay3)
//        paymentWaysList.add(paymentWay4)

//        if (userCart.cartStoreData!!.paymentMethod != null && userCart.cartStoreData!!.paymentMethod == "0") {
//
//            obsPaymentMethod.set(getString(R.string.cash))
//        } else
//        if (userCart.cartStoreData!!.paymentMethod != null && userCart.cartStoreData!!.paymentMethod == "1") {
//
//            obsPaymentMethod.set("visa")
//        } else if (userCart.cartStoreData!!.paymentMethod != null && userCart.cartStoreData!!.paymentMethod == "2") {
//
//            //  paymentWaysList.add(paymentWay1)
//            obsPaymentMethod.set(getString(R.string.cash))
//        }

        paymentWaysAdapter.updateList(paymentWaysList)
        paymentWaysAdapter.notifyDataSetChanged()

        obsPaymentMethod.set(getString(R.string.cash))
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

    init {
        getPaymentWays()
        getWorkingHours()
    }

    private fun getWorkingHours()
    {
        requestCall<StoreTimesResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext PrefMethods.getUserCart()!!.cartStoreData!!.storeId?.let {
                    getApiRepo().getStoreWorkingHours(it)
                }
            }
        })
        { res ->
            when (res!!.isSuccess) {
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
            else ->
            {
                applyCoupon()
            }
        }
    }

    fun onSelectTimeClicked() {
        if (storeTimesResponse.storeTimesList.isNullOrEmpty()){
            getWorkingHours()
        }else{
            setClickable()
            setValue(Codes.SELECT_TIME_CLICKED)
        }
    }

    fun changeLocationClicked() {
        setClickable()
        setValue(Codes.CHANGE_LOCATION)
    }

    private fun applyCoupon()
    {
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
                return@withContext getApiRepo().applyCoupon(PrefMethods.getUserData()!!.id!!, PrefMethods.getLanguage() , obsCouponCode.get().toString(),
                       storeIds.toString() , productsIds.toString(), productsIds.toString())
            }
        })
        { res ->

            obsIsVisible.set(false)
            when (res!!.isSuccess) {
                true -> {
                    res.let {
                        couponResponseData = res.couponResponseData!!
                        discountId = couponResponseData.id!!
                        when(res.couponResponseData.type) {
                            "fixed" -> {
                                discountValue = res.couponResponseData.value!!.toDouble()
                                discountType = "fixed"
                            }
                            "percentage" -> {
                                discountValue = (res.couponResponseData.value!!.toDouble() / 100 ) * obsItemsPrice.get()!!.toDouble()
                                discountType = "percentage"
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
                    obsItemsPrice.get()!!.toDouble() > couponResponseData.minCartTotal!!.toDouble() -> {
                        discountValue =
                            when {
                                discountValue > couponResponseData.maxDiscountValue!!.toDouble() -> {
                                    couponResponseData.maxDiscountValue!!.toDouble()
                                }
                                else -> {
                                    couponResponseData.value!!.toDouble()
                                }
                            }
                        obsItemsPrice.set(obsItemsPrice.get()!!.toDouble() - discountValue)
                        obsTotalPrice.set(obsItemsPrice.get()!!.toDouble() + obsExtraFees.get()!!.toDouble())
                        apiResponseLiveData.value = ApiResponse.success(getString(R.string.coupon_applied_successfully))
                    }
                    else -> {
                        discountId = -1
                        discountValue = 0.0
                        apiResponseLiveData.value = ApiResponse.errorMessage(getString(R.string.impossible_to_apply_coupon))
                    }
                }
            }
            else -> {
                discountValue = couponResponseData.value!!.toDouble()
                obsItemsPrice.set(obsItemsPrice.get()!!.toDouble() - discountValue)
                obsTotalPrice.set(obsItemsPrice.get()!!.toDouble() + obsExtraFees.get()!!.toDouble())
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
//                orderRequestParcelableClass.deliveryTime = obsDeliveryTime.get()
                orderRequestParcelableClass.isTimeChanged = isTimeChanged
                orderRequestParcelableClass.orderCost = obsTotalPrice.get()
                orderRequestParcelableClass.orderItems = userCart.cartItems
                orderRequestParcelableClass.phoneNumber = userAddressItem.phoneNumber
                orderRequestParcelableClass.paymentMethod = obsPaymentMethod.get()
                orderRequestParcelableClass.discountId = discountId
                orderRequestParcelableClass.discountType = discountType
                orderRequestParcelableClass.discountValue = discountValue
                orderRequestParcelableClass.addressStatus = userAddressItem.status
                setValue(Codes.CONFIRM_ORDER)
            }
        }
    }
    fun onAddAddressClicked() {
        setValue(Codes.NO_DEFAULT_LOCATION)
    }
}
