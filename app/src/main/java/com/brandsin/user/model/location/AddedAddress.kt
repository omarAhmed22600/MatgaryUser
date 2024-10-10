package com.brandsin.user.model.location

import android.os.Parcel
import android.os.Parcelable

data class AddedAddress(
    var addressId : Int? = null,
    var lat : String? = null,
    var lng : String? = null,
    var streetName : String? = null,
    var addressName : String? = null,
    var typeValue : String? = null,
    var typeLabel : String? = null,
    var phoneNumber : String? = null,
    var landmark : String? = null,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(addressId)
        parcel.writeString(lat)
        parcel.writeString(lng)
        parcel.writeString(streetName)
        parcel.writeString(addressName)
        parcel.writeString(typeValue)
        parcel.writeString(typeLabel)
        parcel.writeString(phoneNumber)
        parcel.writeString(landmark)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddedAddress> {
        override fun createFromParcel(parcel: Parcel): AddedAddress {
            return AddedAddress(parcel)
        }

        override fun newArray(size: Int): Array<AddedAddress?> {
            return arrayOfNulls(size)
        }
    }
}