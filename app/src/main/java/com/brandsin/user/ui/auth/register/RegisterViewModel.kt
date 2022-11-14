package com.brandsin.user.ui.auth.register

import com.brandsin.user.database.BaseRepository
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.auth.devicetoken.DeviceTokenRequest
import com.brandsin.user.model.auth.devicetoken.DeviceTokenResponse
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.auth.register.RegisterRequest
import com.brandsin.user.model.auth.register.RegisterResponse
import com.brandsin.user.model.auth.setting.countryId.CountryIdResponse
import com.brandsin.user.utils.PrefMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : BaseViewModel()
{
    var deviceTokenRequest = DeviceTokenRequest()
    var request = RegisterRequest()
    var countryId = ""
    var lang = ""
    var code =""
    var userID = ""

    fun onRegisterClicked()
    {
        setClickable()
        when {
            request.phone_number.isNullOrEmpty() || request.phone_number.isNullOrBlank() -> {
                setValue(Codes.EMPTY_PHONE)
            }
            request.phone_number!!.length < 10 -> {
                setValue(Codes.INVALID_PHONE)
            }
            request.password.isNullOrEmpty() || request.password.isNullOrBlank() -> {
                setValue(Codes.PASSWORD_EMPTY)
            }
            request.password!!.length < 6 -> {
                setValue(Codes.PASSWORD_SHORT)
            }
            request.password_confirmation.isNullOrEmpty() || request.password_confirmation.isNullOrBlank() -> {
                setValue(Codes.CONFIRM_PASS_EMPTY)
            }
            request.password_confirmation != request.password -> {
                setValue(Codes.PASSWORD_NOT_MATCH)
            }
            else -> {
                setShowProgress(true)
                apiRegister()
            }
        }
    }
    fun apiRegister(){
        request.country_id = PrefMethods.getCountryId()
        request.name = ""
        request.last_name = ""
        request.provider_fb = "Default"
        request.provider_id = ""
        request.lang = PrefMethods.getLanguage()

        val baeRepo = BaseRepository()
        val responseCall: Call<RegisterResponse?> = baeRepo.apiInterface.register(request)
        responseCall.enqueue(object : Callback<RegisterResponse?> {
            override fun onResponse(call: Call<RegisterResponse?>, response: Response<RegisterResponse?>) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {
                        PrefMethods.saveUserData(response.body()!!.data)
                        code=response.body()!!.code.toString()
                        userID=response.body()!!.data!!.id.toString()
                        deviceToken()
                        setValue(Codes.REGISTER_SUCCESS)
                    }else{
                        setValue(response.body()!!.error.toString())
                    }
                } else {
                    setValue(response.message())
                }
            }
            override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                setValue(t.message!!)
                setShowProgress(false)
            }
        })
    }
    fun onConditionsClicked()
    {
        setClickable()
        setValue(Codes.SHOW_CONDITIONS)
    }

    fun onSkipClicked()
    {
        PrefMethods.saveLoginState(false)
        setValue(Codes.SKIP_CLICKED)
    }

    fun apiCountryId(){
        countryId="default_country_id"
        lang=PrefMethods.getLanguage()
        val baeRepo = BaseRepository()
        val responseCall: Call<CountryIdResponse?> = baeRepo.apiInterface.getCountryId(countryId,lang)!!
        responseCall.enqueue(object : Callback<CountryIdResponse?> {
            override fun onResponse(
                call: Call<CountryIdResponse?>,
                response: Response<CountryIdResponse?>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {
                        PrefMethods.setCountryId(response.body()!!.data.toString())
                    }
                } else {
                    setValue(response.message())
                }
            }

            override fun onFailure(call: Call<CountryIdResponse?>, t: Throwable) {
                setValue(t.message!!)
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
}