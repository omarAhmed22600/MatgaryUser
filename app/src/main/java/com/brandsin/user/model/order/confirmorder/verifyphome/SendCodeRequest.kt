package com.brandsin.user.model.order.confirmorder.verifyphome

import com.google.gson.annotations.SerializedName

data class SendCodeRequest(
    @SerializedName("address_id") var addressId: Int? = null,
    @SerializedName("lang") var lang: String? = null
)