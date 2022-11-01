package com.brandsin.user.model.menu.favourits

import com.google.gson.annotations.SerializedName
import com.brandsin.user.model.order.homepage.StoriesItem

data class FavouritsResponse(
    @field:SerializedName("data")
    val stories: List<List<StoriesItem?>>? = null,
    @field:SerializedName("success")
    val success: Boolean? = null,
)