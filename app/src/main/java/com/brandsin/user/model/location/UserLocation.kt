package com.brandsin.user.model.location

import android.os.Parcel
import android.os.Parcelable
import com.brandsin.user.R
import com.brandsin.user.model.constants.Const
import com.brandsin.user.utils.MyApp

data class UserLocation(
    var lat: String? = Const.latitude.toString(),
    var lng: String? = Const.longitude.toString(),
    var address: String? = MyApp.getInstance().getString(R.string.no_saved_addresses),
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lat)
        parcel.writeString(lng)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "UserLocation(lat=$lat, lng=$lng, address=$address)"
    }

    companion object CREATOR : Parcelable.Creator<UserLocation> {
        override fun createFromParcel(parcel: Parcel): UserLocation {
            return UserLocation(parcel)
        }

        override fun newArray(size: Int): Array<UserLocation?> {
            return arrayOfNulls(size)
        }
    }
}