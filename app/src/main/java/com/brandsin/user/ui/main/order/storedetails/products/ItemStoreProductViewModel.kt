package com.brandsin.user.ui.main.order.storedetails.products

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.brandsin.user.R
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.utils.MyApp

class ItemStoreProductViewModel(var item: StoreProductItem) : ViewModel()
{
    var obsProductPrice = ObservableField<String>()
    var obsCount = ObservableField<Int>()

    init {
        getProductPrice()
    }

    private fun getProductPrice() = when (item.skus!![0]!!.salePrice) {
        null -> {
            obsProductPrice.set("${item.skus!![0]!!.regularPrice} ${MyApp.getInstance().getString(R.string.currency)}")
        }
        else -> {
            obsProductPrice.set("${item.skus!![0]!!.salePrice} ${MyApp.getInstance().getString(R.string.currency)}")
        }
    }
}