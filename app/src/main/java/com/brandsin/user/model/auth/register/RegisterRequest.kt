package com.brandsin.user.model.auth.register

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("phone_number") var phone_number: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("password_confirmation") var password_confirmation: String? = null,
    @SerializedName("country_id") var country_id: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("last_name") var last_name: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("lang") var lang: String? = null,
    @SerializedName("provider_fb") var provider_fb: String? = null,
    @SerializedName("provider_id") var provider_id: String? = null
)