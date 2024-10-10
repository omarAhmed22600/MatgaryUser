package com.brandsin.user.model.menu.settings

import com.google.gson.annotations.SerializedName

data class SettingResponse(

	@field:SerializedName("data")
	val data: Int? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)
