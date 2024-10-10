package com.brandsin.user.ui.main.order.newRateOrder.model

import com.google.gson.annotations.SerializedName

data class RatingRequest(
    @field:SerializedName("items")
    val items: List<RatingItem>
)

data class RatingItem(
    @field:SerializedName("item_type")
    val itemType: String? = null,

    @field:SerializedName("item_id")
    val itemId: Int? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("rate")
    val rate: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("body")
    val body: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("video")
    val video: String? = null
)

