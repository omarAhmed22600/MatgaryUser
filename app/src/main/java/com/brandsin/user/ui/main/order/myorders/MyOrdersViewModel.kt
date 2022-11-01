package com.brandsin.user.ui.main.order.myorders

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.cancel.CancelOrderRequest
import com.brandsin.user.model.order.cancel.CancelOrderResponse
import com.brandsin.user.model.order.myorders.MyOrderItem
import com.brandsin.user.model.order.myorders.MyOrdersResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("UNCHECKED_CAST")
class MyOrdersViewModel : BaseViewModel()
{
    var ordersAdapter  = MyOrdersAdapter()
    var cancelRequest = CancelOrderRequest()

    fun getUserStatus() {
        when {
            PrefMethods.getUserData() != null -> {
                obsIsLogin.set(true)
                obsIsLoading.set(true)
                getMyOrders()
            }
            else -> {
                obsIsLogin.set(false)
            }
        }
    }

    init {
       getUserStatus()
    }

    fun getMyOrders()
    {
        requestCall<MyOrdersResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getMyOrders(PrefMethods.getLanguage(), PrefMethods.getUserData()!!.id!! , 55)
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    obsIsLoading.set(false)
                    when {
                        res.myOrdersList!!.isNotEmpty() -> {
                            ordersAdapter.updateList(res.myOrdersList as ArrayList<MyOrderItem>)
                            obsIsFull.set(true)
                            obsIsEmpty.set(false)
                        }
                        else -> {
                            obsIsFull.set(false)
                            obsIsEmpty.set(true)
                        }
                    }
                }
                else -> {
                    obsIsLoading.set(false)
                    obsIsEmpty.set(true)
                    obsIsFull.set(false)
                }
            }
        }
    }

    fun cancelOrder(item : MyOrderItem)
    {
        cancelRequest.lang = PrefMethods.getLanguage()
        cancelRequest.userId = PrefMethods.getUserData()!!.id
        cancelRequest.orderId = item.id
        obsIsVisible.set(true)
        requestCall<CancelOrderResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().cancelOrder(cancelRequest)
            }
        })
        { res ->
            obsIsVisible.set(false)

            when (res!!.isSuccess) {
                true -> {
                    apiResponseLiveData.value = ApiResponse.success(res)
                }
                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message)
                }
            }
        }
    }

    fun onBackPressed() {
        setValue(Codes.BACK_PRESSED)
    }

    fun onLoginClicked() {
        setValue(Codes.LOGIN_CLICKED)
    }
}