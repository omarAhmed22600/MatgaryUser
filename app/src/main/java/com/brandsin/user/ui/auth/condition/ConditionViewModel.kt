package com.brandsin.user.ui.auth.condition

import com.brandsin.user.database.BaseRepository
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.auth.setting.countryId.CountryIdResponse
import com.brandsin.user.utils.PrefMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConditionViewModel : BaseViewModel()
{
    var code = ""
    var lang = ""
    var txt = ""

    fun apiCondition(){
        code="terms_and_policy"
        lang= PrefMethods.getLanguage()
        val baeRepo = BaseRepository()
        val responseCall: Call<CountryIdResponse?> = baeRepo.apiInterface.getCountryId(code,lang)!!
        responseCall.enqueue(object : Callback<CountryIdResponse?> {
            override fun onResponse(call: Call<CountryIdResponse?>, response: Response<CountryIdResponse?>) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {
                        txt=response.body()!!.data.toString()
                        setValue(Codes.SHOW_CONDITIONS)
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