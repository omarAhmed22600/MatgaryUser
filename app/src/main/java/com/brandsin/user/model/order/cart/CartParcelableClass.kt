package com.brandsin.user.model.order.cart

import com.brandsin.user.model.order.storedetails.StoreProductItem
import java.io.Serializable

data class CartParcelableClass(
    var cartItem: CartItem? = null,
    var storeProductItem: StoreProductItem? = null,
) : Serializable
