package com.brandsin.user.ui.main.order.storedetails.addons.skus.adapter

import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.storedetails.StoreSkusItem

class ItemOrderSkusColorViewModel (var item: StoreSkusItem) : BaseViewModel() {
    var skuChildAdapter = OrderSkusColorAdapter()
    //var obsSkuPrice = ObservableField<String>()

//    private fun getSkuPrice() = when (item.price) {
//        null -> {
//            obsSkuPrice.set("0 ${getString(R.string.currency)}")
//        }
//        else -> {
//            obsSkuPrice.set("${item.price} ${getString(R.string.currency)}")
//        }
//    }
    init {
        if (item.values!=null) {
            skuChildAdapter.updateList(item.values)
        }
        //getSkuPrice()
    }

}