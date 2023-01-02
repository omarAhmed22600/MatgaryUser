package com.brandsin.user.model.order.cart

import java.io.Serializable

data class UserCart(
    var cartStoreData: CartStoreData? = null,
    var cartItems: ArrayList<CartItem>? = null,
    var totalCartPrices: Double = 0.0,
    var totalItemsPrices: Double = 0.0,
    var userNotes: String = "",
) : Serializable

data class CartStoreData(
    var storeId: Int? = null,
    var storeName: String? = null,
    var extraFees: Double = 0.0,
    var deliveryTime: String? = null,
    var minimumOrder: Double = 0.0,

    //var paymentMethod: String? = null,
) : Serializable

data class CartItem(
    var productId: Int? = null,
    var productName: String? = null,
    var productDescription: String? = null,
    var productImage: String? = null,
    var productQuantity: Int = 1,
    var productUnitPrice: Double = 0.0,  // price for selected sku
    var productItemPrice: Double = 0.0,  // unit price + addons
    var productTotalPrice: Double = 0.0, // item price * quantity
    var skuCode: String? = null,
    var skuName: String? = null,
    var userNotes: String = "",
    var addonsIds: ArrayList<String>? = null,
    var addonsNames: ArrayList<String>? = null,
    var addonsPrice: Double = 0.0,
    var type: String? = null,
    var isOffer: Boolean? = null,
) : Serializable {

    override fun toString(): String {
        return "CartItem(productName=$productName, productImage=$productImage, productQuantity=$productQuantity, productTotalPrice=$productTotalPrice, productUnitPrice=$productUnitPrice, skuCode=$skuCode,userNotes=$userNotes, addonsIds=$addonsIds, addonsPrice=$addonsPrice)"
    }
}
