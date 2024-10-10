package com.brandsin.user.model.order.cart

import android.os.Parcelable
import com.brandsin.user.model.order.storedetails.StoreProductItem
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class CartParcelableClass(
    var cartItem: CartItem? = null,
    var storeProductItem: StoreProductItem? = null,
) : Parcelable
