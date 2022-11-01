package com.brandsin.user.model.auth.verifycode

import com.google.gson.annotations.SerializedName
import com.brandsin.user.model.auth.UserModel

data class VerifyCodeResponse(

		@field:SerializedName("success")
		val success: Boolean? = null,
		@field:SerializedName("error")
		val error: String? = null,
		@field:SerializedName("data")
		val data: UserModel? = null
)

