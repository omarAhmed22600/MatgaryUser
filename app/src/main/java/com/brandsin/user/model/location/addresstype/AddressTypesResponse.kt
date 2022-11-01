package com.brandsin.user.model.location.addresstype

import com.google.gson.annotations.SerializedName

data class AddressTypesResponse(

	@field:SerializedName("data")
	val typesList: List<AddressTypeItem?>? = null,

	@field:SerializedName("success")
	val isSuccess: Boolean? = null
)

data class AddressTypeItem(

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)
