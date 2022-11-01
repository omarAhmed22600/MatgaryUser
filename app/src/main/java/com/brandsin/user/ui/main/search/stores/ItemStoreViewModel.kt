package com.brandsin.user.ui.main.search.stores

import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.ShopsItem


class ItemStoreViewModel(var item: ShopsItem) : BaseViewModel()
{

    var obsDeliveryPriceTime = ObservableField<String>()
    var obsCategorios= ObservableField<String>()
    var categoriosName= ArrayList<String>()

    private fun getCategorios() {
        when (item.categories){
            null -> {

            }
            else -> {
                for (item in item.categories!!) {
                    // body of loop
                    categoriosName.add(item!!.name.toString())
                }

                obsCategorios.set(categoriosName.joinToString { it -> "$it" })
            }
        }
    }
    private fun getDeliveryPrice() {
        when (item.deliveryPrice){
            null -> {
                when (item.deliveryTime){
                    null -> {
                        obsDeliveryPriceTime.set(
                            """${getString(R.string.during)} : 0 ${getString(R.string.minute)} . ${getString(R.string.delivery_price)} : 0 ${getString(R.string.currency)}""".trimMargin())
                    }
                    else -> {
                        obsDeliveryPriceTime.set(
                            """${getString(R.string.during)} : ${item.deliveryTime} ${getString(R.string.minute)} . ${getString(R.string.delivery_price)} : 0 ${getString(R.string.currency)}""".trimMargin())
                    }
                }
            }
            else -> {
                when (item.deliveryTime){
                    null -> {
                        obsDeliveryPriceTime.set(
                            """${getString(R.string.during)} : 0 ${getString(R.string.minute)} . ${getString(R.string.delivery_price)} : ${item.deliveryPrice} ${getString(R.string.currency)}""".trimMargin())
                    }
                    else -> {
                        obsDeliveryPriceTime.set(
                            """${getString(R.string.during)} : ${item.deliveryTime} ${getString(R.string.minute)} . ${getString(R.string.delivery_price)} : ${item.deliveryPrice} ${getString(R.string.currency)}""".trimMargin())
                    }
                }
            }
        }


    }
    fun onStoreClicked() {
        setValue("hi")
    }
    init {
        getCategorios()
        getDeliveryPrice()
    }
}