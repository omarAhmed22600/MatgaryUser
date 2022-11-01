package com.brandsin.user.ui.main.order.rateorder

import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.details.OrderDetailsResponse
import com.brandsin.user.model.order.details.OrderItem
import com.brandsin.user.model.order.rate.ProductRateItem
import com.brandsin.user.model.order.rate.RateOrderRequest
import com.brandsin.user.model.order.rate.RateOrderResponse
import com.brandsin.user.model.order.rate.StoreRateItem
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RateOrderViewModel : BaseViewModel()
{
    val obsStoreRate = ObservableField(5.0f)
    var rateAdapter = RateOrderAdapter()
    var orderDetails = OrderDetailsResponse()
    var rateOrderRequest = RateOrderRequest()

    var productsRate :ArrayList<ProductRateItem> = ArrayList()
    var storeRate  = StoreRateItem()

    fun onRateClicked()
    {
        when {
            obsStoreRate.get() == 0f -> {
                setValue(Codes.STORE_RATING_EMPTY)
            }
            else -> {
                storeRate  = StoreRateItem()
                storeRate.storeId = orderDetails.order!!.storeId
                storeRate.storeRate = obsStoreRate.get()

                productsRate = ArrayList<ProductRateItem>()
                rateAdapter.itemsList.forEach {
                    val productRateItem = ProductRateItem(it.productId, it.obsRate)
                    productsRate.add(productRateItem)
                }
                rateOrder()
            }
        }
    }

    fun getOrderDetails(orderId : Int)
    {
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<OrderDetailsResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getOrderDetails(orderId, PrefMethods.getLanguage())
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    when {
                        res.items!!.isNotEmpty() -> {
                            obsIsLoading.set(false)
                            obsIsFull.set(true)
                            orderDetails = res
                            rateAdapter.updateList(res.items as ArrayList<OrderItem>)
                            obsStoreRate.set(5.0f)
                            notifyChange()
                        }
                    }
                }
                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message)
                }
            }
        }
    }

    private fun rateOrder()
    {
        obsIsVisible.set(true)
        rateOrderRequest.orderId = orderDetails.order!!.id
        rateOrderRequest.userId = PrefMethods.getUserData()!!.id
        rateOrderRequest.products = productsRate
        rateOrderRequest.store = storeRate

        requestCall<RateOrderResponse?>({ withContext(Dispatchers.IO) { return@withContext getApiRepo().rateOrder(rateOrderRequest) } })
        { res ->
            obsIsVisible.set(false)
            when (res!!.isSuccess) {
                true -> {
                    apiResponseLiveData.value = ApiResponse.success(res)
                    rateOrderRequest = RateOrderRequest()
                }
                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message)
                    rateOrderRequest = RateOrderRequest()
                }
            }
        }
    }
}