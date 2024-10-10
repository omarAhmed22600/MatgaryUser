package com.brandsin.user.model.location.addaddress

import com.google.gson.annotations.SerializedName

data class AddAddressRequest(
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("street_name") var streetName: String? = null,
    @SerializedName("address_name") var addressName: String? = null,
    @SerializedName("country_id") var countryId: Int? = null,
    @SerializedName("city_id") var cityId: Int? = null,
    @SerializedName("state_id") var stateId: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("phone_number") var phoneNumber: String? = null,
    @SerializedName("lang") var lang: String? = null,
    @SerializedName("lat") var lat: String? = null,
    @SerializedName("lng") var lng: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("landmark") var landmark: String? = null,
) {
    override fun toString(): String {
        return "AddAddressRequest(firstName=$firstName, lastName=$lastName, streetName=$streetName, addressName=$addressName, countryId=$countryId, " +
                "cityId=$cityId, stateId=$stateId, " + "userId=$userId, phoneNumber=$phoneNumber, lang=$lang, lat=$lat, lng=$lng, type=$type, langmark=$landmark)"
    }
}