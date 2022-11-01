package com.brandsin.user.model.location.setdefault

import com.google.gson.annotations.SerializedName

data class SetDefaultAddressResponse(

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
