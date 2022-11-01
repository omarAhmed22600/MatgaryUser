package com.brandsin.user.ui.main.order.cart

import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.utils.SingleLiveEvent
import java.util.*

class ItemCartViewModel(var item: CartItem) : BaseViewModel()
{
    val obsCount = ObservableField<Int>()
    val obsAddons = ObservableField<String>()
    val obsPrice = ObservableField<Double>()
    var increaseCountLiveData = SingleLiveEvent<CartItem>()
    var decreaseCountLiveData = SingleLiveEvent<CartItem>()
    var removeItemLiveData = SingleLiveEvent<CartItem>()

    init {
       var totalPrice = item.productTotalPrice
        obsPrice.set(String.format(Locale.ENGLISH, "%.2f", totalPrice).toDouble())
        item.productQuantity.let {
            obsCount.set(item.productQuantity)
        }

        when {
            item.type != "simple" -> {
                item.addonsNames.let {
                    when {
                        item.addonsNames!!.size != 0 -> {
                            obsAddons.set(item.addonsNames!!.joinToString { it })
                        }
                    }
                }
            }
        }

    }

    /*
    * Tis method called when click on item cart PLUS icon
    */
    fun onPlusClicked()
    {
        obsCount.set(obsCount.get()!! + 1)
      //  obsPrice.set(item.productTotalPrice + (item.productItemPrice * (obsCount.get()!! + 1)))
        increaseCountLiveData.value = item
    }

    /*
   * Tis method called when click on item cart MINUS icon
   */
    fun onMinusClicked() {
        when {
            obsCount.get()!!.toInt() != 1 -> {
                obsCount.set(obsCount.get()!! - 1)
                decreaseCountLiveData.value = item
            }
            else -> {
                removeItemLiveData.value = item
            }
        }
    }
}