package com.brandsin.user.ui.menu.payment

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class UpdateCreditResponse(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("user")
    val user: User? = null
) : Parcelable

@Parcelize
data class Properties(

    @field:SerializedName("about")
    val about: @RawValue Any? = null
) : Parcelable

@Parcelize
data class User(

    @field:SerializedName("national_id")
    val nationalId: @RawValue List<Any?>? = null,

    @field:SerializedName("integration_id")
    val integrationId: @RawValue Any? = null,

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("birthdate")
    val birthdate: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("confirmation_code")
    val confirmationCode: @RawValue Any? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("android_token")
    val androidToken: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("ios_token")
    val iosToken: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("card_last_four")
    val cardLastFour: @RawValue Any? = null,

    @field:SerializedName("card_brand")
    val cardBrand: @RawValue Any? = null,

    @field:SerializedName("notification_preferences")
    val notificationPreferences: @RawValue Any? = null,

    @field:SerializedName("provider_fb")
    val providerFb: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("confirmed_at")
    val confirmedAt: @RawValue Any? = null,

    @field:SerializedName("job_title")
    val jobTitle: @RawValue Any? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("address")
    val address: @RawValue Any? = null,

    @field:SerializedName("phone_country_code")
    val phoneCountryCode: @RawValue Any? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: @RawValue Any? = null,

    @field:SerializedName("reward_points")
    val rewardPoints: @RawValue Any? = null,

    @field:SerializedName("picture")
    val picture: String? = null,

    @field:SerializedName("picture_thumb")
    val pictureThumb: String? = null,

    @field:SerializedName("wallet_credit")
    val walletCredit: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("provider_id")
    val providerId: @RawValue Any? = null,

    @field:SerializedName("online")
    val online: Int? = null,

    @field:SerializedName("phone_number")
    val phoneNumber: String? = null,

    @field:SerializedName("trial_ends_at")
    val trialEndsAt: @RawValue Any? = null,

    @field:SerializedName("payment_method_token")
    val paymentMethodToken: @RawValue Any? = null,

    @field:SerializedName("gateway")
    val gateway: @RawValue Any? = null,

    @field:SerializedName("properties")
    val properties: @RawValue Properties? = null,

    @field:SerializedName("country_id")
    val countryId: Int? = null,

    @field:SerializedName("city_id")
    val cityId: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null
) : Parcelable
