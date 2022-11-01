package com.brandsin.user.ui.menu.offers

import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.menu.offers.OffersItemDetails

class ItemOffersViewModel(var item: OffersItemDetails) : BaseViewModel()
{
    var obsPrice = ObservableField<String>()

    init {
        when {
            item.price != null -> {
                when {
                    item.priceTo != null && item.priceTo!=0-> {
                        obsPrice.set("${item.price} ${getString(R.string.currency)} : ${item.priceTo} ${getString(R.string.currency)}")
                    }
                    item.priceTo != null -> {
                        obsPrice.set("${item.price} ${getString(R.string.currency)} ")
                    }
                    else -> {
                        obsPrice.set("${item.price} ${getString(R.string.currency)}")
                    }
                }
            }
            item.price == null -> {
                when {
                    item.priceTo != null&& item.priceTo!=0 -> {
                        obsPrice.set("${item.priceTo} ${getString(R.string.currency)}")
                    }
                    else  -> {
                        obsPrice.set("0 ${getString(R.string.currency)}")
                    }
                }
            }
        }
    }
}