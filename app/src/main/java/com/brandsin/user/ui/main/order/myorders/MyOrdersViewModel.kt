package com.brandsin.user.ui.main.order.myorders

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.cancel.CancelOrderRequest
import com.brandsin.user.model.order.cancel.CancelOrderResponse
import com.brandsin.user.model.order.myorders.MyOrderItem
import com.brandsin.user.model.order.myorders.MyOrderListResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyOrdersViewModel : BaseViewModel() {

    var ordersAdapter = MyOrdersAdapter()

    private var cancelRequest = CancelOrderRequest()

    init {
        getUserStatus()
    }

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

    fun getMyOrders() {
        requestCall<MyOrderListResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getMyOrders(
                    PrefMethods.getLanguage(),
                    PrefMethods.getUserData()!!.id!!,
                    55
                )
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    obsIsLoading.set(false)
                    when {
                        res.data!!.isNotEmpty() -> {
                            ordersAdapter.updateList(res.data as ArrayList<MyOrderItem>)
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

    fun cancelOrder(item: MyOrderItem) {
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