package com.brandsin.user.ui.main.homenew.moresub

import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homenew.StoresDataItem
import com.brandsin.user.utils.MyApp.Companion.context
import com.brandsin.user.utils.Utils


class ItemMoreSubViewModel(var itemMoreSub: StoresDataItem) : BaseViewModel() {

    var obsDeliveryTime = ObservableField<String>()
    var obsDeliveryPrice = ObservableField<String>()

    private fun getDeliveryTime() = when (itemMoreSub.deliveryTime) {
        null -> {
            obsDeliveryTime.set(
                """ 0 ${
                    Utils.translateDeliveryType(
                        context,
                        itemMoreSub.deliveryType.toString()
                    )
                }"""
            )
        }

        else -> {
            obsDeliveryTime.set(
                """ ${itemMoreSub.deliveryTime} ${
                    Utils.translateDeliveryType(
                        context,
                        itemMoreSub.deliveryType.toString()
                    )
                }"""
            )
        }
    }

    private fun getDeliveryPrice() = when (itemMoreSub.deliveryPrice) {
        null -> {
            obsDeliveryPrice.set(""" 0 ${getString(R.string.currency)}""")
        }

        else -> {
            obsDeliveryPrice.set(
                """ ${itemMoreSub.deliveryPrice} ${
                    getString(
                        R.string.currency
                    )
                }"""
            )
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