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

class OrderDetailsViewModel : BaseViewModel() {

    var orderDetails = OrderDetailsResponse()

    var orderItemsAdapter = OrderContentAdapter()

    var obsPaymentMethod = ObservableField<String>()
    var isMapReady = MutableLiveData<Boolean>()
    var itemsAdapter = OrderItemsAdapter()

    fun getOrderDetails(orderId: Int) {
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

                    apiResponseLiveData.value = ApiResponse.success(res)

                    res.offers!!.forEach {
                        val item = OrderItem()
                        item.id = it!!.id
                        item.amount = it.amount
                        // item.product_description = it.offer!!.description
                        item.quantity = it.quantity ?: 0
                        item.skuCode = it.sku_code
                        item.productName = it.description ?: "" // it.offer?.name ?: ""
                        // item.userNotes = it.user_notes
                        item.type = it.type
                        item.image = it.image ?: "" // it.offer?.image ?: ""
                        // item.addons = null
                        item.storeId = null
                        res.items!!.add(item)
                    }

                    itemsAdapter.updateList(res.items as ArrayList<OrderItem>)
                    orderItemsAdapter.updateList(res.items)

                    val pay = res.order?.billing?.gateway
                    if (pay == "Paytaps") { // MyFatoorah
                        obsPaymentMethod.set(getString(R.string.visa))
                    } else if (pay == "Wallet") {
                        obsPaymentMethod.set(getString(R.string.hajaty_wallet))
                    } else {
                        obsPaymentMethod.set(getString(R.string.cash))
                    }
                    //obsPaymentMethod.set(res.order!!.billing!!.gateway)
                    isMapReady.value = true
                    notifyChange()
                }

                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message)
                }
            }
        }
    }

    fun onPhoneClicked() {
        setValue(Codes.PHONE_CLICKED)
    }

    fun onWhatsClicked() {
        setValue(Codes.WHATSAPP_CLICKED)
    }

}