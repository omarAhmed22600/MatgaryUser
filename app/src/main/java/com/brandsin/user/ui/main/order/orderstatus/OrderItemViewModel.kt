package com.brandsin.user.ui.main.order.orderstatus

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.details.OrderItem
import timber.log.Timber

class OrderItemViewModel(var item: OrderItem) : BaseViewModel(){

    init {
        Timber.e("Mou3aaaaaz : " + item.toString())
    }
}
