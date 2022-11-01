package com.brandsin.user.model.location.addresstype

import com.google.gson.annotations.SerializedName

data class AddressTypesRequest(
    @SerializedName("code") var code: String? = null,
    @SerializedName("lang") var lang: String? = null
)