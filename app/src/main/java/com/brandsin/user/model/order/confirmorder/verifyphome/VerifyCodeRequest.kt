package com.brandsin.user.model.order.confirmorder.verifyphome

import com.google.gson.annotations.SerializedName

data class VerifyCodeRequest(
    @SerializedName("address_id") var addressId: Int? = null,
    @SerializedName("code") var code: String? = null,
    @SerializedName("lang") var lang: String? = null
)