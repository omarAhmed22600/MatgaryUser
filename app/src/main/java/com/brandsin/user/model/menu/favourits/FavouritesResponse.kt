package com.brandsin.user.model.menu.favourits

import com.brandsin.user.model.order.homepage.StoriesItem
import com.google.gson.annotations.SerializedName

data class FavouritesResponse(
    @field:SerializedName("data")
    val stories: List<List<StoriesItem?>>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,
)