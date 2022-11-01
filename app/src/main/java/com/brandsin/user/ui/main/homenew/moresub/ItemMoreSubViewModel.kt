package com.brandsin.user.ui.main.homenew.moresub

import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homenew.StoresDataItem


class ItemMoreSubViewModel(var itemMoreSub: StoresDataItem) : BaseViewModel() {

    var obsDeliveryTime = ObservableField<String>()
    var obsDeliveryPrice = ObservableField<String>()

    private fun getDeliveryTime() = when (itemMoreSub.deliveryTime) {
        null -> {
            obsDeliveryTime.set(""" 0 ${getString(R.string.minute)}""")
        }
        else -> {
            obsDeliveryTime.set(""" ${itemMoreSub.deliveryTime} ${getString(
                R.string.minute)}""")
        }
    }

    private fun getDeliveryPrice() = when (itemMoreSub.deliveryPrice) {
        null -> {
            obsDeliveryPrice.set(""" 0 ${getString(R.string.currency)}""")
        }
        else -> {
            obsDeliveryPrice.set(""" ${itemMoreSub.deliveryPrice} ${getString(
                R.string.currency)}""")
        }
    }
    fun onMoreSubClicked() {
        setValue("hi")
    }
    init {
        getDeliveryTime()
        getDeliveryPrice()
    }
}