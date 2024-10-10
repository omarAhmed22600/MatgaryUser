package com.brandsin.user.ui.activity.home

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods

class MainViewModel : BaseViewModel() {
    val obsShowToolbar = ObservableBoolean()
    val obsTitle = ObservableField<String>()
    val obsUserName = ObservableField<String>()
    val obsBtnLogout = ObservableField<String>()

    init {
        setUpUserData()
    }

    fun setUpUserData() {
        if (PrefMethods.getUserData() == null) {
            obsIsLogin.set(false)
            obsUserName.set(MyApp.context.getString(R.string.welcome))
            obsBtnLogout.set(MyApp.context.getString(R.string.login))
        } else {
            obsIsLogin.set(true)
            obsBtnLogout.set(MyApp.context.getString(R.string.account))
            if (PrefMethods.getUserData()!!.full_name.toString() != "null") {
                // obsUserName.set(getString(R.string.welcome) + " " + PrefMethods.getUserData()!!.full_name.toString())
                obsUserName.set(getString(R.string.welcome) + " " + PrefMethods.getUserData()!!.name.toString())
            } else {
                if (PrefMethods.getUserData()!!.phoneNumber.toString() != "null") {
                    obsUserName.set(getString(R.string.welcome) + " " + PrefMethods.getUserData()!!.phoneNumber.toString())
                } else {
                    obsUserName.set(MyApp.context.getString(R.string.welcome))
                }
            }
        }
    }

    fun onLogoutClicked() {
        PrefMethods.saveLoginState(false)
        PrefMethods.deleteUserData()
        PrefMethods.deleteCartData()
        setValue(Codes.LOGOUT_CLICK)
    }

    fun onEditClicked() {
        if (PrefMethods.getUserData() != null) {
            setValue(Codes.EDIT_CLICKED)
        } else {
            setValue(Codes.BUTTON_LOGIN_CLICKED)
        }
    }

    fun onClickOffers() {
        setValue(Codes.BUTTON_OFFER_CLICKED)
    }

    fun onClickNotification() {
        setValue(Codes.BUTTON_NOTIFICATION_CLICKED)
    }

    fun onClickMyOrder() {
        setValue(Codes.BUTTON_MYORDER_CLICKED)
    }

    fun payment() {
        setValue(Codes.BUTTON_PAYMENT_CLICKED)
    }

    fun help() {
        setValue(Codes.BUTTON_HELP_CLICKED)
    }

    fun about() {
        setValue(Codes.BUTTON_ABOUT_CLICKED)
    }

    fun contact() {
        setValue(Codes.BUTTON_CONTACT_CLICKED)
    }
}