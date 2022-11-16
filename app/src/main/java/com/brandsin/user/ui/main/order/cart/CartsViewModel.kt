package com.brandsin.user.ui.main.order.cart

import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.utils.PrefMethods
import java.util.*
import kotlin.collections.ArrayList

class CartsViewModel : BaseViewModel()
{
    var userCartData = UserCart()
    var cartItemsList = ArrayList<CartItem>()
    var cartsAdapter  = CartsAdapter()

    var obsExtraFeesPrice = ObservableField(0.0)
    var obsItemsPrice = ObservableField(0.0)
    var obsTotalPrice = ObservableField(0.0)
    var obsNotes = ObservableField<String>()

    var obsAddress = ObservableField<String>()

    fun getUserStatus() {
        when {
            PrefMethods.getUserData() != null -> {
                obsIsLogin.set(true)
                getUserAddress()
                getCartData()
            }
            else -> {
                obsIsLogin.set(false)
            }
        }
    }

    private fun getUserAddress() {
        when {
            PrefMethods.getDefaultAddress() == null -> {
                when {
                    PrefMethods.getUserLocation() != null -> {
                        when {
                            PrefMethods.getUserLocation()!!.address != null || PrefMethods.getUserLocation()!!.address != "null" -> {
                                obsAddress.set(PrefMethods.getUserLocation()!!.address.toString())
                            }
                            else -> {
                                obsAddress.set("يجب تحديد موقك الجغرافي حتي نقوم بتوصيل كل ما تحتاجة بدقة اعلي")
                            }
                        }
                    }
                    else -> {
                        obsAddress.set("يجب تحديد موقك الجغرافي حتي نقوم بتوصيل كل ما تحتاجة بدقة اعلي")
                    }
                }
            }
            else -> {
                obsAddress.set(PrefMethods.getDefaultAddress()!!.streetName.toString())
            }
        }
    }

    private fun getCartData()
    {
        when {
            PrefMethods.getUserCart() == null -> {
                obsIsEmpty.set(true)
                obsIsFull.set(false)
            }
            else -> {
                obsIsEmpty.set(false)
                obsIsFull.set(true)
                userCartData = PrefMethods.getUserCart()!!
                getCartItems()
                obsExtraFeesPrice.set(userCartData.cartStoreData!!.extraFees)
            }
        }
    }

    private fun getCartItems()
    {
        cartItemsList = userCartData.cartItems as ArrayList<CartItem>
        cartsAdapter.updateList(cartItemsList)

        cartItemsList.forEach {
            var itemsPrice = obsItemsPrice.get()!!.toDouble() + it.productTotalPrice
            obsItemsPrice.set(String.format(Locale.ENGLISH, "%.2f", itemsPrice).toDouble())
         }

        var totalPrice = obsItemsPrice.get()!!.toDouble() + userCartData.cartStoreData!!.extraFees
        obsTotalPrice.set(String.format(Locale.ENGLISH, "%.2f", totalPrice).toDouble())
    }

    fun removeItemFromCart(item: CartItem)
    {

        cartItemsList.remove(item)
        cartsAdapter.updateList(cartItemsList)

        var itemsPrice = obsItemsPrice.get()!!.toDouble() - item.productTotalPrice
        obsItemsPrice.set(String.format(Locale.ENGLISH, "%.2f", itemsPrice).toDouble())

        var totalPrice = obsItemsPrice.get()!!.toDouble() + userCartData.cartStoreData!!.extraFees
        obsTotalPrice.set(String.format(Locale.ENGLISH, "%.2f", totalPrice).toDouble())


        when (cartItemsList.size) {
            0 -> {
                obsItemsPrice.set(0.0)
                obsTotalPrice.set(0.0)
                val cartStore = null
                val userCart = UserCart(cartStore, cartItemsList)
                PrefMethods.getUserCart()!!.cartStoreData = null
                PrefMethods.saveUserCart(userCart)
                PrefMethods.deleteCartData()
                obsIsFull.set(false)
                obsIsEmpty.set(true)
            }
            else -> {
                val cartStore = userCartData.cartStoreData
                val userCart = UserCart(cartStore, cartItemsList)
                PrefMethods.saveUserCart(userCart)
            }
        }
    }

    init {
        getUserStatus()
    }

    fun onMakeOrderClicked()
    {

        when {
            userCartData.cartStoreData!!.minimumOrder > obsItemsPrice.get()!! -> {
                setValue(Codes.LOWER_ORDER_COST)
            }
            else -> {
                userCartData.totalCartPrices = obsTotalPrice.get()!!
                userCartData.totalItemsPrices = obsItemsPrice.get()!!
                if (obsNotes.get() != null) {
                    userCartData.userNotes = obsNotes.get()!!
                }
                //userCartData.userNotes = obsNotes.get()!!
                setValue(Codes.MAKE_ORDER_CLICKED)
            }
        }
    }

    fun onAddMoreClicked() {
        setValue(Codes.ADD_MORE_CLICKED)
    }

    fun onLoginClicked() {
        setValue(Codes.LOGIN_CLICKED)
    }

    fun inCreaseCartCount(item: CartItem)
    {
        cartItemsList[cartItemsList.indexOf(item)].productQuantity = item.productQuantity + 1
        cartItemsList[cartItemsList.indexOf(item)].productTotalPrice = item.productItemPrice * cartItemsList[cartItemsList.indexOf(
            item)].productQuantity
        cartsAdapter.updateList(cartItemsList)

        var itemsPrice = obsItemsPrice.get()!!.toDouble() + item.productItemPrice
        obsItemsPrice.set(String.format(Locale.ENGLISH, "%.2f", itemsPrice).toDouble())

        var totalPrice = obsItemsPrice.get()!!.toDouble() + obsExtraFeesPrice.get()!!.toDouble()
        obsTotalPrice.set(String.format(Locale.ENGLISH, "%.2f", totalPrice).toDouble())

        updateCrtData()
    }

    fun deCreaseCartCount(item: CartItem)
    {
        cartItemsList[cartItemsList.indexOf(item)].productQuantity = item.productQuantity - 1
        cartItemsList[cartItemsList.indexOf(item)].productTotalPrice = item.productItemPrice * cartItemsList[cartItemsList.indexOf(
            item)].productQuantity
        cartsAdapter.updateList(cartItemsList)

        var itemsPrice = obsItemsPrice.get()!!.toDouble() - item.productItemPrice
        obsItemsPrice.set(String.format(Locale.ENGLISH, "%.2f", itemsPrice).toDouble())

        var totalPrice = obsItemsPrice.get()!!.toDouble() + obsExtraFeesPrice.get()!!.toDouble()
        obsTotalPrice.set(String.format(Locale.ENGLISH, "%.2f", totalPrice).toDouble())

        updateCrtData()
    }

    private fun updateCrtData()
    {
        val cartStore = userCartData.cartStoreData
        val userCart = UserCart(cartStore, cartItemsList)

        PrefMethods.saveUserCart(userCart)
    }
}