package com.brandsin.user.model.order.cancel

import com.google.gson.annotations.SerializedName

data class CancelOrderRequest(

    @SerializedName("user_id")
    var userId: Int? = null,

    @SerializedName("order_id")
    var orderId: Int? = null,

    @SerializedName("lang")
    var lang: String? = null,
)