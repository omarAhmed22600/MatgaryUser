package com.brandsin.user.model.order.confirmorder.coupon

import com.google.gson.annotations.SerializedName

data class ApplyCouponResponse(

	@field:SerializedName("data")
	val couponResponseData: CouponResponseData? = null,

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class CouponResponseData(

	@field:SerializedName("count_used")
	val countUsed: Int? = null,

	@field:SerializedName("min_cart_total")
	val minCartTotal: String? = null,

	@field:SerializedName("start")
	val start: String? = null,

	@field:SerializedName("uses")
	val uses: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("expiry")
	val expiry: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("value")
	val value: String? = null,

	@field:SerializedName("max_discount_value")
	val maxDiscountValue: String? = null
) {
	override fun toString(): String {
		return "CouponResponseData(countUsed=$countUsed, minCartTotal=$minCartTotal, start=$start, uses=$uses, id=$id, expiry=$expiry, type=$type, value=$value, maxDiscountValue=$maxDiscountValue)"
	}
}
