package com.brandsin.user.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.brandsin.user.model.auth.UserModel
import com.brandsin.user.model.location.UserLocation
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.order.cart.UserCart
import java.util.*

object Const{
    var DEFAULT_LANG: String = Locale.getDefault().language
    const val APP_PREF_NAME = "PREF_HAGATY_USER"
    const val PREF_LANG = "PREF_LANG"
    const val PREF_USER_DATA = "PREF_USER_DATA"
    const val PREF_DEFAULT_ADDRESS = "PREF_DEFAULT_ADDRESS"
    const val PREF_USER_ADDRESS = "PREF_USER_ADDRESS"
    const val PREF_LOGIN_STATE = "PREF_LOGIN_STATE"
    const val PREF_COUNTRY_ID = "PREF_COUNTRY_ID"
    const val PREF_IS_PERMISSION_DENIED_FOR_EVER = "PREF_IS_PERMISSION_DENIED_FOR_EVER"
    const val PREF_IS_NOTIFICATIONS_ENABLED = "PREF_IS_NOTIFICATIONS_ENABLED"
    const val PREF_IS_ASKED_TO_LOGIN = "PREF_IS_ASKED_TO_LOGIN"
    const val PREF_USER_CART = "PREF_USER_CART"
    const val PREF_Welcome = "PREF_Welcome"
    const val PREF_HomePopup = "PREF_HomePopup"
}

public object  PrefMethods {

    private var PRIVATE_MODE = 0

    private fun getSharedPreference(): SharedPreferences {
        val appCtx = MyApp.getInstance().applicationContext
        return appCtx.getSharedPreferences(Const.APP_PREF_NAME, PRIVATE_MODE)
    }

    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(Const.APP_PREF_NAME, PRIVATE_MODE)
    }

    fun getLanguage(context: Context? = null): String {
        context?.let {
            return getSharedPreference(it).getString(Const.PREF_LANG,Const.DEFAULT_LANG)!!
        } ?: return getString(Const.PREF_LANG, Const.DEFAULT_LANG)!!
    }

    fun setLanguage( value: String, context: Context?=null) {
        context?.let {
            getSharedPreference(it).edit {
                putString(Const.PREF_LANG, value)
            }
        }?: setString(Const.PREF_LANG, value)
    }

    fun getIsAskedToLogin(context: Context? = null): Boolean {
        context?.let {
            return getSharedPreference(it).getBoolean(Const.PREF_IS_ASKED_TO_LOGIN,false)
        } ?: return getBoolean(Const.PREF_IS_ASKED_TO_LOGIN, false)!!
    }

    fun saveIsAskedToLogin( value: Boolean, context: Context? = null) {
        context?.let {
            getSharedPreference(it).edit {
                putBoolean(Const.PREF_IS_ASKED_TO_LOGIN, value)
            }
        }?: setBoolean(Const.PREF_IS_ASKED_TO_LOGIN, value)
    }

    fun getIsPermissionDeniedForEver(context: Context? = null): Boolean {
        context?.let {
            return getSharedPreference(it).getBoolean(Const.PREF_IS_PERMISSION_DENIED_FOR_EVER,false)
        } ?: return getBoolean(Const.PREF_IS_PERMISSION_DENIED_FOR_EVER, false)!!
    }

    fun saveIsPermissionDeniedForEver( value: Boolean, context: Context? = null) {
        context?.let {
            getSharedPreference(it).edit {
                putBoolean(Const.PREF_IS_PERMISSION_DENIED_FOR_EVER, value)
            }
        }?: setBoolean(Const.PREF_IS_PERMISSION_DENIED_FOR_EVER, value)
    }

    fun getCountryId(context: Context? = null): String {
        context?.let {
            return getSharedPreference(it).getString(Const.PREF_COUNTRY_ID,"195")!!
        } ?: return getString(Const.PREF_COUNTRY_ID, "195")!!
    }

    fun setCountryId( value: String, context: Context?=null) {
        context?.let {
            getSharedPreference(it).edit {
                putString(Const.PREF_COUNTRY_ID, value)
            }
        }?: setString(Const.PREF_COUNTRY_ID, value)
    }

    fun saveUserData(data: UserModel?) {
        data?.let {
            val gSon = Gson()
            setString(Const.PREF_USER_DATA, gSon.toJson(it))
        }
    }

    fun getUserData(): UserModel? {
        getString(Const.PREF_USER_DATA, null)?.let {
                val gSon = Gson()
                return gSon.fromJson(it, UserModel::class.java)

        } ?: return null
    }

    fun saveUserLocation(data: UserLocation?) {
        data?.let {
            val gSon = Gson()
            setString(Const.PREF_USER_ADDRESS, gSon.toJson(it))
        }
    }

    fun getUserLocation(): UserLocation? {
        getString(Const.PREF_USER_ADDRESS, null)?.let {
            val gSon = Gson()
            return gSon.fromJson(it, UserLocation::class.java)
        } ?: return null
    }

    fun saveDefaultAddress(data: AddressListItem?) {
        data?.let {
            val gSon = Gson()
            setString(Const.PREF_DEFAULT_ADDRESS, gSon.toJson(it))
        }
    }

    fun getDefaultAddress(): AddressListItem? {
        getString(Const.PREF_DEFAULT_ADDRESS, null)?.let {
            val gSon = Gson()
            return gSon.fromJson(it, AddressListItem::class.java)
        } ?: return null
    }

    fun getLoginState(context: Context? = null): Boolean {
        context?.let {
            return getSharedPreference(it).getBoolean(Const.PREF_LOGIN_STATE,false)
        } ?: return getBoolean(Const.PREF_LOGIN_STATE, false)!!
    }

    fun saveLoginState( value: Boolean, context: Context? = null) {
        context?.let {
            getSharedPreference(it).edit {
                putBoolean(Const.PREF_LOGIN_STATE, value)
            }
        }?: setBoolean(Const.PREF_LOGIN_STATE, value)
    }

    fun getIsNotificationsEnabled(context: Context? = null): Boolean? {
        context?.let {
            return getSharedPreference(it).getBoolean(Const.PREF_IS_NOTIFICATIONS_ENABLED,true)
        } ?: return getBoolean(Const.PREF_IS_NOTIFICATIONS_ENABLED, true)
    }

    fun setIsNotificationsEnabled(value: Boolean,context: Context? = null) {
        context?.let {
            getSharedPreference(it).edit {
                putBoolean(Const.PREF_IS_NOTIFICATIONS_ENABLED, value)
            }
        }?: setBoolean(Const.PREF_IS_NOTIFICATIONS_ENABLED, value)
    }


    /* ------ Deleting Cash --------  */
    fun deleteUserData()
    {
        remove(Const.PREF_USER_DATA)
    }

    fun deleteDefaultAddress()
    {
        remove(Const.PREF_DEFAULT_ADDRESS)
    }

    fun deleteCartData()
    {
        remove(Const.PREF_USER_CART)
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return getSharedPreference().getString(key, defaultValue)
    }

    private fun remove(key: String) {
        getSharedPreference().edit { remove(key) }
    }

    private fun setString(key: String, value: String) {
        getSharedPreference().edit { putString(key, value) }
    }

    private fun getBoolean(key: String, defaultValue: Boolean? = null): Boolean {
        return getSharedPreference().getBoolean(key, defaultValue!!)
    }

    private fun setBoolean(key: String, value: Boolean) {
        getSharedPreference().edit { putBoolean(key, value) }
    }

    fun saveUserCart( userCart: UserCart?) {
        userCart?.let {
            val gSon = Gson()
            setString(Const.PREF_USER_CART, gSon.toJson(it))
        }
    }

    fun getUserCart(): UserCart? {
        getString(Const.PREF_USER_CART, null)?.let {
            val gSon = Gson()
            return gSon.fromJson(it, UserCart::class.java)
        } ?: return null
    }

    fun getWelcome(context: Context? = null): Boolean {
        context?.let {
            return getSharedPreference(it).getBoolean(Const.PREF_Welcome,false)
        } ?: return getBoolean(Const.PREF_Welcome, false)!!
    }

    fun saveWelcome( value: Boolean, context: Context? = null) {
        context?.let {
            getSharedPreference(it).edit {
                putBoolean(Const.PREF_Welcome, value)
            }
        }?: setBoolean(Const.PREF_Welcome, value)
    }

    fun getHomePopup(context: Context? = null): Boolean {
        context?.let {
            return getSharedPreference(it).getBoolean(Const.PREF_HomePopup,true)
        } ?: return getBoolean(Const.PREF_HomePopup, true)
    }

    fun saveHomePopup( value: Boolean, context: Context? = null) {
        context?.let {
            getSharedPreference(it).edit {
                putBoolean(Const.PREF_HomePopup, value)
            }
        }?: setBoolean(Const.PREF_HomePopup, value)
    }
}