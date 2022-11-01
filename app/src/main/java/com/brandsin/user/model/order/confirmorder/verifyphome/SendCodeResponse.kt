package com.brandsin.user.model.order.confirmorder.verifyphome

import com.google.gson.annotations.SerializedName

data class SendCodeResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	@field:SerializedName("error")
	val error: String? = null,
)
