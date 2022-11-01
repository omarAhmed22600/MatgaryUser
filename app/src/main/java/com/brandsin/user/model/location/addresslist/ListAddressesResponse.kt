package com.brandsin.user.model.location.addresslist

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ListAddressesResponse(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val addressesList: List<AddressListItem?>? = null,

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("error")
	val errorMessage: String? = null,
)

data class AddressListItem(

	@field:SerializedName("code")
	var code: String? = null,

	@field:SerializedName("lng")
	var lng: String? = null,

	@field:SerializedName("last_name")
	var lastName: String? = null,

	@field:SerializedName("type")
	var type: String? = null,

	@field:SerializedName("is_default")
	var isDefault: Int? = null,

	@field:SerializedName("street_name")
	var streetName: String? = null,

	@field:SerializedName("city_name")
	var cityName: String? = null,

	@field:SerializedName("state_name")
	var stateName: String? = null,

	@field:SerializedName("flat")
	var flat: String? = null,

	@field:SerializedName("country_name")
	var countryName: String? = null,

	@field:SerializedName("phone_number")
	var phoneNumber: String? = null,

	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("state_id")
	var stateId: Int? = null,

	@field:SerializedName("type_label")
	var typeLabel: String? = null,

	@field:SerializedName("floor")
	var floor: String? = null,

	@field:SerializedName("first_name")
	var firstName: String? = null,

	@field:SerializedName("lat")
	var lat: String? = null,

	@field:SerializedName("country_id")
	var countryId: Int? = null,

	@field:SerializedName("city_id")
	var cityId: Int? = null,

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
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(code)
		parcel.writeString(lng)
		parcel.writeString(lastName)
		parcel.writeString(type)
		parcel.writeValue(isDefault)
		parcel.writeString(streetName)
		parcel.writeString(cityName)
		parcel.writeString(stateName)
		parcel.writeString(flat)
		parcel.writeString(countryName)
		parcel.writeString(phoneNumber)
		parcel.writeValue(id)
		parcel.writeValue(stateId)
		parcel.writeString(typeLabel)
		parcel.writeString(floor)
		parcel.writeString(firstName)
		parcel.writeString(lat)
		parcel.writeValue(countryId)
		parcel.writeValue(cityId)
		parcel.writeValue(status)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<AddressListItem> {
		override fun createFromParcel(parcel: Parcel): AddressListItem {
			return AddressListItem(parcel)
		}

		override fun newArray(size: Int): Array<AddressListItem?> {
			return arrayOfNulls(size)
		}
	}
}
