package com.brandsin.user.ui.main.order.orderdetails

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.details.OrderDetailsResponse
import com.brandsin.user.model.order.details.OrderItem
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.order.orderstatus.OrderItemsAdapter
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailsViewModel : BaseViewModel()
{
    var orderDetails = OrderDetailsResponse()
    var orderItemsAdapter  = OrderContentAdapter()
    var obsPaymentMethod = ObservableField<String>()
    var isMapReady = MutableLiveData<Boolean>()
    var itemsAdapter  = OrderItemsAdapter()
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
                    Timber.e("Mou3aaaz true")
                    obsIsLoading.set(false)
                    obsIsFull.set(true)
                    orderDetails = res
                    itemsAdapter.updateList(res.items as ArrayList<OrderItem>)
                    orderItemsAdapter.updateList(res.items as ArrayList<OrderItem>)
                    obsPaymentMethod.set(getString(R.string.cash))
                    isMapReady.value = true
                    notifyChange()
                }
                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message)
                }
            }
        }
    }

    fun onPhoneClicked(){
        setValue(Codes.PHONE_CLICKED)
    }

    fun onWhatsClicked(){
        setValue(Codes.WHATSAPP_CLICKED)
    }

}