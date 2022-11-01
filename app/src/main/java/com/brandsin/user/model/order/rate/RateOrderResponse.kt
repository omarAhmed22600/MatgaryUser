package com.brandsin.user.model.order.rate

import com.google.gson.annotations.SerializedName

data class RateOrderResponse(

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
