package com.brandsin.user.ui.auth.forgotpass

import com.brandsin.user.database.BaseRepository
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.auth.forgot.ForgotPassRequest
import com.brandsin.user.model.auth.forgot.ForgotPassResponse
import com.brandsin.user.utils.PrefMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassViewModel : BaseViewModel()
{
    var request = ForgotPassRequest()
    var code=""
    var userId=""

    fun onNextClicked() {
        setClickable()
        when {
            request.phone_email.isNullOrEmpty() || request.phone_email.isNullOrBlank() -> {
                setValue(Codes.EMPTY_PHONE)
            }
            request.phone_email!!.length < 10 -> {
                setValue(Codes.INVALID_PHONE)
            }
            else -> {
                setShowProgress(true)
                apiResendCode()
            }
        }
    }
    fun apiResendCode(){
        request.lang= PrefMethods.getLanguage()
        val baeRepo = BaseRepository()
        val responseCall: Call<ForgotPassResponse?> = baeRepo.apiInterface.forgotPass(request)
        responseCall.enqueue(object : Callback<ForgotPassResponse?> {
            override fun onResponse(call: Call<ForgotPassResponse?>, response: Response<ForgotPassResponse?>) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {
                        code= response.body()!!.code.toString()
                        userId= response.body()!!.user_id.toString()
                        setValue(Codes.FORGOT_SUCCESS)
                    }else{
                        setValue(response.body()!!.error.toString())
                    }
                } else {
                    setValue(response.message())
                }
            }
            override fun onFailure(call: Call<ForgotPassResponse?>, t: Throwable) {
                setValue(t.message!!)
                setShowProgress(false)
            }
        })
    }
}