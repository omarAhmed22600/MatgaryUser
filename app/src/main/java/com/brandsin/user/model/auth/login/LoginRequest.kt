package com.brandsin.user.model.auth.login

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("phone_number") var phone_number: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("lang") var lang: String? = null,
    @SerializedName("provider_fb") var provider_fb: String? = null
)