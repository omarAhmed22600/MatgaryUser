package com.brandsin.user.model

import com.google.gson.annotations.SerializedName

data class FavoriteResponse(

	@field:SerializedName("data")
	val data: List<Any?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
