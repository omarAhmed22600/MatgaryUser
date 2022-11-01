package com.brandsin.user.ui.auth.authways

import com.brandsin.user.database.BaseRepository
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.auth.login.LoginRequest
import com.brandsin.user.model.auth.login.LoginResponse
import com.brandsin.user.model.auth.register.RegisterRequest
import com.brandsin.user.model.auth.register.RegisterResponse
import com.brandsin.user.model.auth.setting.countryId.CountryIdResponse
import com.brandsin.user.utils.PrefMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginWaysViewModel  : BaseViewModel() {

    var requestLogin = LoginRequest()
    var requestRegister = RegisterRequest()
    var countryId = ""
    var lang = ""
    var code =""


    fun apiLogin(phone_number: String, password: String, lang: String, provider_fb: String, name: String, email: String) {

        requestLogin.phone_number=phone_number
        requestLogin.password=password
        requestLogin.lang=lang
        requestLogin.provider_fb=provider_fb

        val baeRepo = BaseRepository()
        val responseCall: Call<LoginResponse?> = baeRepo.apiInterface.login(requestLogin)
        responseCall.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(call: Call<LoginResponse?>, response: Response<LoginResponse?>) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {
                        PrefMethods.saveUserData(response.body()!!.data)
                        PrefMethods.saveLoginState(true)
                        setValue(Codes.LOGIN_SUCCESS)
                    } else {
                        if (provider_fb == "Default") {
                            setValue(response.body()!!.error.toString())
                        } else {
                            if (provider_fb == "Facebook") {
                                apiRegister("",
                                        password,
                                        password,
                                        PrefMethods.getCountryId(),
                                        email,
                                        name,
                                        lang,
                                        "Facebook",
                                        phone_number)
                            } else if (provider_fb == "Gmail") {
                                apiRegister("",
                                        password,
                                        password,
                                        PrefMethods.getCountryId(),
                                        email,
                                        name,
                                        lang,
                                        "Gmail",
                                        phone_number)
                            }
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

    fun apiRegister(phone_number: String, password: String, password_confirmation: String, country_id: String?,
                    email: String, name: String, lang: String,provider_fb: String, provider_id: String) {
        requestRegister.phone_number = phone_number
        requestRegister.password = password
        requestRegister.password_confirmation = password_confirmation
        requestRegister.country_id = country_id
        requestRegister.email = email
        requestRegister.name = name
        requestRegister.lang = lang
        requestRegister.provider_fb = provider_fb
        requestRegister.provider_id = provider_id

        val baeRepo = BaseRepository()
        val responseCall: Call<RegisterResponse?> = baeRepo.apiInterface.register(requestRegister)
        responseCall.enqueue(object : Callback<RegisterResponse?> {
            override fun onResponse(call: Call<RegisterResponse?>, response: Response<RegisterResponse?>) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {
                        PrefMethods.saveUserData(response.body()!!.data)
                        PrefMethods.saveLoginState(true)
                        setValue(Codes.LOGIN_SUCCESS)
                    } else {
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
    fun apiCountryId(){
        countryId="default_country_id"
        lang= PrefMethods.getLanguage()
        val baeRepo = BaseRepository()
        val responseCall: Call<CountryIdResponse?> = baeRepo.apiInterface.getCountryId(countryId, lang)!!
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
}