package com.brandsin.user.model.order.confirmorder.verifyphome

import com.google.gson.annotations.SerializedName

data class VerifyCodeResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	@field:SerializedName("error")
	val error: String? = null
)

data class Data(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("lng")
	val lng: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("is_default")
	val isDefault: Int? = null,

	@field:SerializedName("street_name")
	val streetName: String? = null,

	@field:SerializedName("city_name")
	val cityName: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("state_name")
	val stateName: String? = null,

	@field:SerializedName("country_name")
	val countryName: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Int? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("state_id")
	val stateId: Int? = null,

	@field:SerializedName("type_label")
	val typeLabel: String? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("lat")
	val lat: String? = null,

	@field:SerializedName("country_id")
	val countryId: Int? = null,

	@field:SerializedName("city_id")
	val cityId: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
