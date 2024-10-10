package com.brandsin.user.model.discover

import com.brandsin.user.model.order.homepage.StoriesItem
import com.google.gson.annotations.SerializedName

data class DiscoverResponse(

	@field:SerializedName("data")
	val stories: List<List<StoriesItem?>?> = listOf(),

	@field:SerializedName("success")
	val success: Boolean? = null
)

