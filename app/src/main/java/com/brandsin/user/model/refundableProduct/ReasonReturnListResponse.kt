package com.brandsin.user.model.refundableProduct

import com.google.gson.annotations.SerializedName

data class ReasonReturnListResponse(

	@field:SerializedName("data")
	val reasonReturnItem: List<ReasonReturnItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class ReasonReturnItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
