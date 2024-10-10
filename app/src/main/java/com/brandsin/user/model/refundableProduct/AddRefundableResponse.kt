package com.brandsin.user.model.refundableProduct

import com.google.gson.annotations.SerializedName

data class AddRefundableResponse(

	@field:SerializedName("data")
	val orderNumber: String? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
