package com.brandsin.user.model.auth.register

import com.google.gson.annotations.SerializedName
import com.brandsin.user.model.auth.UserModel

data class RegisterResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,
	@field:SerializedName("error")
	val error: String? = null,
	@field:SerializedName("data")
	val data: UserModel? = null,
	@field:SerializedName("code")
	val code: Int? = null

)
