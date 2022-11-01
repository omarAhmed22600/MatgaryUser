package com.brandsin.user.ui.main.order.confirmorder

import com.google.gson.annotations.SerializedName

data class PaymentWayItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,
)
