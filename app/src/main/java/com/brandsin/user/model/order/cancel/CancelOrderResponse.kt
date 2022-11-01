package com.brandsin.user.model.order.cancel

import com.google.gson.annotations.SerializedName

data class CancelOrderResponse(

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,
)
