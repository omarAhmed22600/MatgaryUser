package com.brandsin.user.model.order.rate

import com.google.gson.annotations.SerializedName

data class RateOrderRequest (
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("order_id") var orderId: Int? = null,
    @SerializedName("comment") var comment: String? = null,
    @SerializedName("store") var store: StoreRateItem? = null,
    @SerializedName("products") var products: List<ProductRateItem>? = null,
) {
    override fun toString(): String {
        return "RateOrderRequest(userId=$userId, orderId=$orderId, comment=$comment, store=$store, products=$products)"
    }
}

data class StoreRateItem(
    @SerializedName("id") var storeId: Int? = null,
    @SerializedName("rate") var storeRate: Float? = null,
)

data class ProductRateItem(
    @SerializedName("id") var productId: Int? = null,
    @SerializedName("rate") var productRate: Float? = null,
)