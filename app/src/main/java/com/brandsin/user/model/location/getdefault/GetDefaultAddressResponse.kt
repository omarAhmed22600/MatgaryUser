package com.brandsin.user.model.location.getdefault

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.brandsin.user.model.location.addresslist.AddressListItem

data class GetDefaultAddressResponse(

	@field:SerializedName("data")
	val defaultAddressItem: List<AddressListItem?>? = null,

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	val error: String? = null
)

data class DefaultAddressItem(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("lng")
	val lng: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("is_default")
	val isDefault: Int? = null,

	@field:SerializedName("street_name")
	val streetName: String? = null,

	@field:SerializedName("city_name")
	val cityName: String? = null,

	@field:SerializedName("state_name")
	val stateName: String? = null,

	@field:SerializedName("country_name")
	val countryName: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("state_id")
	val stateId: Int? = null,

	@field:SerializedName("type_label")
	val typeLabel: String? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("lat")
	val lat: String? = null,

	@field:SerializedName("country_id")
	val countryId: Int? = null,

	@field:SerializedName("city_id")
	val cityId: Int? = null,

	@field:SerializedName("status")
	var status: Int? = null,

	@field:SerializedName("landmark")
	var landmark: String? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		)

    override fun toString(): String {
		return "DefaultAddressItem(code=$code, lng=$lng, lastName=$lastName, type=$type, isDefault=$isDefault, streetName=$streetName, cityName=$cityName, stateName=$stateName, countryName=$countryName, phoneNumber=$phoneNumber, id=$id, stateId=$stateId, typeLabel=$typeLabel, firstName=$firstName, lat=$lat, countryId=$countryId, cityId=$cityId, status=$status)"
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(code)
		parcel.writeString(lng)
		parcel.writeString(lastName)
		parcel.writeString(type)
		parcel.writeValue(isDefault)
		parcel.writeString(streetName)
		parcel.writeString(cityName)
		parcel.writeString(stateName)
		parcel.writeString(countryName)
		parcel.writeString(phoneNumber)
		parcel.writeValue(id)
		parcel.writeValue(stateId)
		parcel.writeString(typeLabel)
		parcel.writeString(firstName)
		parcel.writeString(lat)
		parcel.writeValue(countryId)
		parcel.writeValue(cityId)
		parcel.writeValue(status)
		parcel.writeValue(landmark)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<DefaultAddressItem> {
		override fun createFromParcel(parcel: Parcel): DefaultAddressItem {
			return DefaultAddressItem(parcel)
		}

		override fun newArray(size: Int): Array<DefaultAddressItem?> {
			return arrayOfNulls(size)
		}
	}
}
