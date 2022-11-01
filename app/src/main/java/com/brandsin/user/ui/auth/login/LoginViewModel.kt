package com.brandsin.user.ui.auth.login

import com.brandsin.user.database.BaseRepository
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.auth.devicetoken.DeviceTokenRequest
import com.brandsin.user.model.auth.devicetoken.DeviceTokenResponse
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.auth.login.LoginRequest
import com.brandsin.user.model.auth.login.LoginResponse
import com.brandsin.user.utils.PrefMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : BaseViewModel()
{
    var deviceTokenRequest = DeviceTokenRequest()
    var request = LoginRequest()
    var code = ""
    var userID = ""

    fun onSkipClicked()
    {
        PrefMethods.saveLoginState(false)
        PrefMethods.deleteUserData()
        setValue(Codes.SKIP_CLICKED)
    }

    fun onLoginClicked()
    {
        setClickable()

        when {
            request.phone_number.isNullOrEmpty() || request.phone_number.isNullOrBlank() -> {
                setValue(Codes.EMPTY_PHONE)
            }
            request.phone_number!!.length < 9 -> {
                setValue(Codes.INVALID_PHONE)
            }
            request.password.isNullOrEmpty() || request.password.isNullOrBlank() -> {
                setValue(Codes.PASSWORD_EMPTY)
            }
            request.password!!.length < 6 -> {
                setValue(Codes.PASSWORD_SHORT)
            }
            else -> {
                setShowProgress(true)
                apiLogin()
            }
        }
    }
    fun apiLogin(){
        request.provider_fb="Default"
        request.lang=PrefMethods.getLanguage()
        val baeRepo = BaseRepository()
        val responseCall: Call<LoginResponse?> = baeRepo.apiInterface.login(request)
        responseCall.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(call: Call<LoginResponse?>, response: Response<LoginResponse?>) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {
                        PrefMethods.saveUserData(response.body()!!.data)
                        PrefMethods.saveLoginState(true)
                        deviceToken()
                        setValue(Codes.LOGIN_SUCCESS)
                    }else{
                        if (response.body()!!.register==1){
                            code=response.body()!!.code.toString()
                            userID=response.body()!!.data!!.id.toString()
                            setValue("not activate")
                        }else{
                            setValue(response.body()!!.error.toString())
                        }
                    }
                } else {
                    setValue(response.message())
                }
            }
            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                setValue(t.message!!)
                setShowProgress(false)
            }
        })
    }
    fun deviceToken(){

        deviceTokenRequest.user_id = PrefMethods.getUserData()!!.id.toString()
        deviceTokenRequest.type = "android_token"

        val baeRepo = BaseRepository()
        val responseCall: Call<DeviceTokenResponse?> = baeRepo.apiInterface.deviceToken(deviceTokenRequest)
        responseCall.enqueue(object : Callback<DeviceTokenResponse?> {
            override fun onResponse(call: Call<DeviceTokenResponse?>, response: Response<DeviceTokenResponse?>) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {

                    }
                } else {
                    setValue(response.message())
                }
            }
            override fun onFailure(call: Call<DeviceTokenResponse?>, t: Throwable) {
                setValue(t.message!!)
                setShowProgress(false)
            }
        })
    }

    fun onForgotClicked()
    {
        setValue(Codes.FORGOT_INTENT)
    }

    fun onRegisterClicked()
    {
        setValue(Codes.REGISTER_INTENT)
    }
}