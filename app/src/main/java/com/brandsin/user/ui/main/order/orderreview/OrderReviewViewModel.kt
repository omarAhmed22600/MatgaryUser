package com.brandsin.user.ui.main.order.orderreview

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.confirmorder.createorder.CreateOrderRequest
import com.brandsin.user.model.order.confirmorder.createorder.CreateOrderResponse
import com.brandsin.user.model.order.confirmorder.createorder.ItemOption
import com.brandsin.user.model.order.confirmorder.createorder.OfferOrderItem
import com.brandsin.user.model.order.confirmorder.createorder.OrderData
import com.brandsin.user.model.order.confirmorder.createorder.OrderItemRequest
import com.brandsin.user.model.order.confirmorder.createorder.OrderRequestParcelableClass
import com.brandsin.user.model.order.confirmorder.createorder.ShippingItem
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderReviewViewModel : BaseViewModel() {
    var orderData = OrderRequestParcelableClass()
    private var userCart = UserCart()
    var orderItemsAdapter = OrderReviewItemsAdapter()
    private var createOrderRequest = CreateOrderRequest()

    var obsDeliveryTime = ObservableField<String>()
    var obsPaymentMethod = ObservableField<String>()
    var obsCurrentCreditWallet = ObservableField<Double>()
    var obsOrderAmount = ObservableField<String>()
    var obsPackagingPrice = ObservableField<String>()
    var isMapReady = MutableLiveData<Boolean>()
    var isVerified: Boolean = false
    var isTimeChanged: Boolean = false

//    fun onConfirmClicked() {
//        when {
//            !isVerified -> {
//                setValue(Codes.VERIFY_PHONE)
//            }
//            else -> {
//                createOrder()
//            }
//        }
//    }

    init {
        getCartData()
    }

    private fun getCartData() {
        when {
            PrefMethods.getUserCart() != null -> {
                userCart = PrefMethods.getUserCart()!!
            }
        }
    }

    fun createOrder(context: Context?) {
        obsIsVisible.set(true)
        requestCall<CreateOrderResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().createOrder(prepareOrderRequest(context))
            }
        })
        { res ->
            obsIsVisible.set(false)
            when (res!!.isSuccess) {
                true -> {
                    PrefMethods.deleteCartData()
                    apiResponseLiveData.value = ApiResponse.success(res)
                }

                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message)
                }
            }
        }
    }

    private fun prepareOrderRequest(context: Context?): CreateOrderRequest {
        createOrderRequest.lang = PrefMethods.getLanguage()
        when {
            !isTimeChanged -> {
                createOrderRequest.deliveryTime = null
            }

            else -> {
                createOrderRequest.deliveryTime = obsDeliveryTime.get()
            }
        }
        when {
            orderData.discountId != -1 -> {
                createOrderRequest.discountId
                createOrderRequest.discountType = orderData.discountType
                createOrderRequest.discountValue = orderData.discountValue
            }
        }
        createOrderRequest.deviceType = "Android"
        //createOrderRequest.paymentMethod = orderData.paymentMethod

        if (obsPaymentMethod.get()!!.trim() == context!!.getString(R.string.visa).trim() ||
            obsPaymentMethod.get()!!.trim() == context.getString(R.string.visa).trim()
        ) {
            createOrderRequest.paymentMethod = "paytaps" // "MyFatoorah"
        } else if (obsPaymentMethod.get()!!.trim() == context.getString(R.string.cash).trim()) {
            createOrderRequest.paymentMethod = "cash"
        } else if (obsPaymentMethod.get()!!.trim() == context.getString(R.string.my_wallet).trim()) {
            createOrderRequest.paymentMethod = "wallet"
        }

        createOrderRequest.deliveryTime = orderData.deliveryTime
        createOrderRequest.userNotes = orderData.userNotes
        createOrderRequest.extraFees = orderData.extraFees.toString()

        createOrderRequest.orderType = orderData.orderType
        createOrderRequest.gifterName = orderData.recipientName
        createOrderRequest.gifterMobile = orderData.recipientMobileNumber
        createOrderRequest.has_Packaging = orderData.hasPackaging

        val orderItems: ArrayList<OrderItemRequest> = ArrayList()
        val offerItems: ArrayList<OfferOrderItem> = ArrayList()

        orderData.orderItems!!.forEach {
            when (it.isOffer) {
                true -> {
                    offerItems.add(
                        OfferOrderItem(
                            it.productId!!,
                            it.productQuantity,
                            it.productTotalPrice,
                            it.productImage
                        )
                    )
                }

                else -> {
                    val item = OrderItemRequest(
                        it.productTotalPrice.toString(),
                        ItemOption(""),
                        it.addonsIds,
                        it.productQuantity.toString(),
                        it.skuCode.toString(),
                        it.userNotes
                    )
                    orderItems.add(item)
                }
            }
        }

        val offerIds: ArrayList<String> = ArrayList()
        offerIds.add(offerItems.toString())

        val data = OrderData()
        data.addressId = orderData.addressId!!.toInt().toString()
        data.amount = orderData.orderCost.toString()
        data.orderItems = orderItems

        // data.offers = Gson().toJson(arrayListOf(offerItems))

        data.offers = offerItems
        data.shipping = ShippingItem(
            userCart.totalItemsPrices.toString(),
            userCart.cartStoreData!!.storeName.toString()
        )
        data.store_id = userCart.cartStoreData?.storeId?.toInt().toString()
        data.user_id = PrefMethods.getUserData()?.id?.toInt()

        createOrderRequest.data = Gson().toJson(arrayListOf(data))

        Log.d("createOrderRequest", createOrderRequest.toString())
        return createOrderRequest
    }
}