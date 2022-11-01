package com.brandsin.user.model.order.storedetails

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoreTimesResponse(

	@field:SerializedName("data")
	val storeTimesList: List<StoreTimeItem?>? = null,

	@field:SerializedName("success")
	val isSuccess: Boolean? = null
) :Serializable

data class StoreTimeItem(

	@field:SerializedName("date")
	val storeDate: String? = null,

	@field:SerializedName("time")
	val storeTime: List<String?>? = null,

	@field:SerializedName("day")
	val storeDay: String? = null,

	var isSelected : Boolean = false
) :Serializable