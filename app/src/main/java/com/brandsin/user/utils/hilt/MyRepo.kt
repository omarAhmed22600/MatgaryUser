package com.brandsin.user.utils.hilt

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng

interface MyRepo {
    fun getLocation(): LiveData<LatLng>
}