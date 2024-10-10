package com.brandsin.user.ui.main.search.stores

import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.search.SearchStoresOrProducts
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.Utils


class ItemStoreViewModel(var item: SearchStoresOrProducts) : BaseViewModel() {

    var obsDeliveryPriceTime = ObservableField<String>()
    var obsCategories = ObservableField<String>()

    // var categoriesName = ArrayList<String>()

    init {
        // getCategories()
        getDeliveryPrice()
    }

    /*private fun getCategories() {
        when (item.categories) {
            null -> {

            }

            else -> {
                for (item in item.categories!!) {
                    // body of loop
                    categoriesName.add(item!!.name.toString())
                }

                obsCategories.set(categoriesName.joinToString { it -> "$it" })
            }
        }
    }*/

    private fun getDeliveryPrice() {
        when (item.deliveryPrice) {
            null -> {
                when (item.deliveryTime) {
                    null -> {
                        obsDeliveryPriceTime.set(
                            """${getString(R.string.during)} : 0 ${
                                Utils.translateDeliveryType(
                                    MyApp.context,
                                    item.deliveryType.toString()
                                )
                            } . ${
                                getString(
                                    R.string.delivery_price
                                )
                            } : 0 ${getString(R.string.currency)}""".trimMargin()
                        )
                    }

                    else -> {
                        obsDeliveryPriceTime.set(
                            """${getString(R.string.during)} : ${item.deliveryTime} ${
                                Utils.translateDeliveryType(
                                    MyApp.context,
                                    item.deliveryType.toString()
                                )
                            } . ${
                                getString(
                                    R.string.delivery_price
                                )
                            } : 0 ${getString(R.string.currency)}""".trimMargin()
                        )
                    }
                }
            }

            else -> {
                when (item.deliveryTime) {
                    null -> {
                        obsDeliveryPriceTime.set(
                            """${getString(R.string.during)} : 0 ${
                                Utils.translateDeliveryType(
                                    MyApp.context,
                                    item.deliveryType.toString()
                                )
                            } . ${
                                getString(
                                    R.string.delivery_price
                                )
                            } : ${item.deliveryPrice} ${getString(R.string.currency)}""".trimMargin()
                        )
                    }

                    else -> {
                        obsDeliveryPriceTime.set(
                            """${getString(R.string.during)} : ${item.deliveryTime} ${
                                Utils.translateDeliveryType(
                                    MyApp.context,
                                    item.deliveryType.toString()
                                )
                            } . ${
                                getString(
                                    R.string.delivery_price
                                )
                            } : ${item.deliveryPrice} ${getString(R.string.currency)}""".trimMargin()
                        )
                    }
                }
            }
        }
    }

    fun onStoreClicked() {
        setValue("hi")
    }
}