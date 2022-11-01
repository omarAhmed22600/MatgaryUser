package com.brandsin.user.model.auth.login

import com.google.gson.annotations.SerializedName
import com.brandsin.user.model.auth.UserModel

data class LoginResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,
	@field:SerializedName("error")
	val error: String? = null,
	@field:SerializedName("register")
	val register: Int? = null,
	@field:SerializedName("code")
	val code: Int? = null,
	@field:SerializedName("data")
	val data: UserModel? = null

)
