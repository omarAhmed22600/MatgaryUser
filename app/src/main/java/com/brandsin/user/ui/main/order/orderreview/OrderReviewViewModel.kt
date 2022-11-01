package com.brandsin.user.ui.main.order.orderreview

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.confirmorder.createorder.*
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderReviewViewModel : BaseViewModel()
{
    var orderData = OrderRequestParcelableClass()
    var userCart = UserCart()
    var orderItemsAdapter  = OrderReviewItemsAdapter()
    var createOrderRequest  = CreateOrderRequest()
    var obsDeliveryTime = ObservableField<String>()
    var obsPaymentMethod = ObservableField<String>()
    var obsOrderAmount = ObservableField<String>()
    var isMapReady = MutableLiveData<Boolean>()
    var isVerified : Boolean = false
    var isTimeChanged : Boolean = false

    fun onConfirmClicked() {
        when {
            !isVerified -> {
                setValue(Codes.VERIFY_PHONE)
            }
            else -> {
                createOrder()
            }
        }
    }

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

    fun createOrder()
    {
        obsIsVisible.set(true)
        requestCall<CreateOrderResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().createOrder(preprareOrderRequest())
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

    private fun preprareOrderRequest() : CreateOrderRequest
    {
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
        createOrderRequest.paymentMethod = orderData.paymentMethod
        createOrderRequest.deliveryTime = orderData.deliveryTime
        createOrderRequest.userNotes = orderData.userNotes

        val orderItems: ArrayList<OrderItemRequest> = ArrayList()
        val offerItems: ArrayList<OfferOrderItem> = ArrayList()

        orderData.orderItems!!.forEach {
            when (it.isOffer) {
                true -> {
                    offerItems.add(OfferOrderItem(it.productId!!,it.productQuantity, it.productTotalPrice ))
                }
                else -> {
                    val item = OrderItemRequest(it.productTotalPrice.toString(), ItemOption(""),
                        it.addonsIds, it.productQuantity.toString(), it.skuCode.toString(),it.userNotes)
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
        data.shipping = ShippingItem(userCart.totalItemsPrices.toString(), userCart.cartStoreData!!.storeName.toString())
        data.store_id = userCart.cartStoreData!!.storeId!!.toInt().toString()
        data.user_id = PrefMethods.getUserData()!!.id!!.toInt()

        createOrderRequest.data = Gson().toJson(arrayListOf(data))

        return createOrderRequest
    }
}