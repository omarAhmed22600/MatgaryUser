package com.brandsin.user.ui.menu.offers.offersdetails

import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.menu.offers.OffersItemDetails
import com.brandsin.user.model.menu.offers.ProductsItem
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartStoreData
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.utils.PrefMethods

class OfferDetailsViewModel : BaseViewModel()
{
    var offerData = OffersItemDetails()
    var offerProductAdapter  = OfferProductAdapter()
    var obsPrice = ObservableField<String>()
    var cartData : UserCart? = null
    var cartItemsList : ArrayList<CartItem>? = null
    var storeData : CartStoreData? = null

    fun setOfferDetails(offerSetails : OffersItemDetails)
    {
        offerData = offerSetails
        offerProductAdapter.updateList(offerSetails.productsList as ArrayList<ProductsItem>)
        when {
            offerData.price != null -> {
                when {
                    offerData.priceTo != null -> {
                        obsPrice.set("${offerData.price} ${getString(R.string.currency)} : ${offerData.priceTo} ${getString(
                            R.string.currency)}")
                    }
                    else -> {
                        obsPrice.set("${offerData.price} ${getString(R.string.currency)}")
                    }
                }
            }
            offerData.price == null -> {
                when {
                    offerData.priceTo != null -> {
                        obsPrice.set("0 ${getString(R.string.currency)} : ${offerData.priceTo} ${getString(
                            R.string.currency)}")
                    }
                    else -> {
                        obsPrice.set("0 ${getString(R.string.currency)}")
                    }
                }
            }
        }
        notifyChange()
    }


    fun onBuyOfferClicked() {
        when {
            PrefMethods.getUserData() != null -> {
                if (PrefMethods.getUserCart() != null ) {

                    cartData = UserCart()
                    cartItemsList = ArrayList<CartItem>()
                    storeData = CartStoreData()

                    cartData = PrefMethods.getUserCart()!!
                    cartItemsList = cartData!!.cartItems!!
                    storeData = cartData!!.cartStoreData!!
                    when {
                        storeData!!.storeId != null -> {
                            when {
                                // Cart is Not empty But the saved products NOT has the same store Id >> Clear the cart before adding new products
                                storeData!!.storeId != offerData.storeId -> {
                                    setValue(Codes.CLEAR_CART)
                                }
                                else -> {
                                    // Cart is Not empty and the all products has the sam Id
                                    addOfferToCart()
                                }
                            }
                        }
                    }
                }
                else {
                    cartData = UserCart()
                    cartItemsList = ArrayList<CartItem>()
                    storeData = CartStoreData()

                    addOfferToCart()
                }
            }
            else -> {
                setValue(Codes.NOT_LOGIN)
            }
        }
    }


    fun addOfferToCart() {

        when {
            // Cart is empty
            cartItemsList!!.isEmpty() -> {
                cartItemsList!!.add(getItemCart())

                storeData?.run {
                    storeId = offerData.storeId
                    storeName = offerData.store!!.name
                    extraFees = offerData.store!!.deliveryPrice!!.toDouble()
                }

                cartData?.run {
                    cartItems = cartItemsList
                    cartStoreData = storeData
                }
                PrefMethods.saveUserCart(cartData)
                setValue(Codes.ADDED_TO_CART)
            }
            else -> {
                var i = 0
                while (i < cartItemsList!!.size) {
                    when {
                        // This item is exist in cart >> update quantity
                        cartItemsList!![i].productId == offerData.id -> {
                            cartItemsList!![i].productQuantity = cartItemsList!![i].productQuantity + 1 // update quantity
                            cartItemsList!![i].productTotalPrice = cartItemsList!![i].productTotalPrice + offerData.price!! // update price
                            val sharedCart = PrefMethods.getUserCart()
                            sharedCart?.cartItems?.clear()
                            sharedCart?.cartItems?.addAll(cartItemsList!!)
                            PrefMethods.saveUserCart(sharedCart)
                            setValue(Codes.ADDED_TO_CART)
                            return
                        }
                        i == cartItemsList!!.size - 1 -> {
                            // This item not exist in cart
                            cartItemsList!!.add(getItemCart())
                            storeData?.run {
                                storeId = offerData.storeId
                                storeName = offerData.store!!.name
                                extraFees = offerData.store!!.deliveryPrice!!.toDouble()
                            }

                            cartData?.run {
                                cartItems = cartItemsList
                                cartStoreData = storeData
                            }
                            PrefMethods.saveUserCart(cartData)
                            setValue(Codes.ADDED_TO_CART)
                            return
                        }
                        else -> i++
                    }
                }
            }
        }
    }

    fun getItemCart() : CartItem
    {
        val cartItem = CartItem()
        cartItem.productId = offerData.id
        cartItem.productName = offerData.name
        cartItem.productDescription = offerData.description
        cartItem.productImage = offerData.image
        cartItem.productQuantity = 1
        cartItem.productUnitPrice = offerData.price!!.toDouble()
        cartItem.productItemPrice = offerData.price!!.toDouble()
        cartItem.productTotalPrice = offerData.price!!.toDouble()
        cartItem.skuCode = ""
        cartItem.userNotes = ""
        cartItem.addonsIds = arrayListOf()
        cartItem.addonsNames = arrayListOf()
        cartItem.addonsPrice = 0.0
        cartItem.type = "offer"
        cartItem.isOffer = true

        return cartItem
    }
}