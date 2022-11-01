package com.brandsin.user.utils.hilt

interface SharedPreferenceDataSource {
    fun getLocationLatitude(): Double
    fun setLocationLatitude(latitude: Double)
    fun getLocationLongitude(): Double
    fun setLocationLongitude(longitude: Double)
}