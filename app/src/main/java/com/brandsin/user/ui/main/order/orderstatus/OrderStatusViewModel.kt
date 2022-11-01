package com.brandsin.user.ui.main.order.orderstatus

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.details.OrderDetailsResponse
import com.brandsin.user.model.order.details.OrderItem
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderStatusViewModel : BaseViewModel()
{
    var itemsAdapter  = OrderItemsAdapter()
    var orderDetails  = OrderDetailsResponse()

    fun getOrderDetails(orderId : Int) {
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<OrderDetailsResponse?>({
            withContext(Dispatchers.IO) { return@withContext getApiRepo().getOrderDetails(orderId, PrefMethods.getLanguage())
            } })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    when {
                        res.items!!.isNotEmpty() -> {
                            obsIsLoading.set(false)
                            obsIsFull.set(true)
                            orderDetails = res
                            itemsAdapter.updateList(res.items as ArrayList<OrderItem>)
                            apiResponseLiveData.value = ApiResponse.success(res)
                            notifyChange()
                        } }
                }
                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message)
                }
            }

        }
    }
}