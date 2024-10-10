package com.brandsin.user.ui.main.order.newRateOrder.model

import com.google.gson.annotations.SerializedName

data class UploadMultiFilesResponse(

	@field:SerializedName("data")
	val data: List<String?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)
