package com.brandsin.user.model.auth

import com.google.gson.annotations.SerializedName

data class UserModel(

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("android_token")
    val androidToken: String? = null,

    @field:SerializedName("picture")
    val picture: String? = null,

    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("ios_token")
    val iosToken: Any? = null,

    @field:SerializedName("role_id")
    val roleId: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("last_name")
    val last_name: String? = null,

    @field:SerializedName("full_name")
    val full_name: String? = null,

    @field:SerializedName("provider_id")
    val providerId: Any? = null,

    @field:SerializedName("phone_number")
    val phoneNumber: String? = null,

    @field:SerializedName("provider_fb")
    val providerFb: String? = null,

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("birthdate")
    val birthdate: String? = null,
    @field:SerializedName("gender")
    val gender: String? = null,
    @field:SerializedName("country_id")
    val countryId: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("city_id")
    val cityId: Int? = null
)