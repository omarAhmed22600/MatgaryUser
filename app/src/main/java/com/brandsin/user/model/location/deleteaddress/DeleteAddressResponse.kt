package com.brandsin.user.model.location.deleteaddress

import com.google.gson.annotations.SerializedName

data class DeleteAddressResponse(

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: String? = null
)
