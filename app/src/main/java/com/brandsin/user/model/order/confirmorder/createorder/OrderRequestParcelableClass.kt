package com.brandsin.user.model.order.confirmorder.createorder

import android.os.Parcelable
import com.brandsin.user.model.order.cart.CartItem
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class OrderRequestParcelableClass(
    var addressId: Int? = null,
    var addressType: String? = null,
    var streetName: String? = null,
    var lansmark: String? = null,
    var lat: Double? = null,
    var lng: Double? = null,
    var deliveryTime: String? = null,
    var paymentMethod: String? = null,
    var extraFees: Double? = null,
    var orderCost: Double? = null,
    var phoneNumber: String? = null,
    var discountId: Int? = null,
    var discountValue: Double? = null,
    var discountType: String? = null,
    var orderItems: List<CartItem>? = null,
    var userNotes: String? = null,
    var addressStatus: Int? = null,
    var storeId: Int? = null,
    var isTimeChanged: Boolean = false,

    var orderType: String? = null,
    var recipientName: String? = null,
    var recipientMobileNumber: String? = null,
    var hasPackaging: String? = null,
    var packagingPrice: Double? = null,

    var currentCreditWallet: Double? = null,

) : Parcelable

@Parcelize
data class OfferOrderItem(
    var id: Int? = null,
    var quantity: Int? = null,
    var amount: Double? = null,
    var image: String? = null,

): Parcelable
