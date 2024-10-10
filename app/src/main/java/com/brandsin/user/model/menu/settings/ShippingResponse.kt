package com.brandsin.user.model.menu.settings

import com.google.gson.annotations.SerializedName

data class ShippingResponse(

	@field:SerializedName("data")
	val data: List<ShippingItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class ShippingItem(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("value")
	val value: Int? = null,

	var drawableResId: Int? = null // New property for drawable resource ID

)
