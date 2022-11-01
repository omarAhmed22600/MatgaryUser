package com.brandsin.user.ui.main.home.stores

import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.ShopsItem

class ItemStoreViewModel(var item: ShopsItem) : BaseViewModel()
{
    var obsMinPrice = ObservableField<String>()
    var obsDeliveryPrice = ObservableField<String>()
    var obsRating = ObservableField<Float>()

    private fun getMinPrice() = when (item.minPriceProduct) {
        null -> {
            obsMinPrice.set("""${getString(R.string.min_order)} : 0 ${getString(R.string.currency)}""")
        }
        else -> {
            obsMinPrice.set("""${getString(R.string.min_order)} : ${item.minOrderPrice} ${getString(R.string.currency)}""")
        }
    }

    private fun getDeliveryPrice() = when (item.deliveryPrice) {
        null -> {
            obsDeliveryPrice.set("""${getString(R.string.delivery_price)} : 0 ${getString(R.string.currency)}""")
        }
        else -> {
            obsDeliveryPrice.set("""${getString(R.string.delivery_price)} : ${item.deliveryPrice} ${getString(R.string.currency)}""")
        }
    }

    private fun getRating() = when (item.avgRating) {
        null -> {
            obsRating.set(0f)
        }
        else -> {
            obsRating.set(item.avgRating!!.toFloat())
        }
    }

    init {
        getDeliveryPrice()
        getMinPrice()
        getRating()
    }
}