package com.brandsin.user.ui.profile.updateprofile

import android.util.Patterns
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseRepository
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.profile.updateprofile.UpdateProfileRequest
import com.brandsin.user.model.profile.updateprofile.UpdateProfileResponse
import com.brandsin.user.utils.PrefMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileViewModel : BaseViewModel() {
    var request = UpdateProfileRequest()
    var isMale = ObservableBoolean(true)
    var birthDay = ObservableField<String>()

    init {
        if (PrefMethods.getUserData()!!.name != null) {
            request.name = PrefMethods.getUserData()!!.name
        }
        if (PrefMethods.getUserData()!!.last_name != null) {
            request.last_name = PrefMethods.getUserData()!!.last_name
        }
        if (PrefMethods.getUserData()!!.phoneNumber != null) {
            request.phone_number = PrefMethods.getUserData()!!.phoneNumber
        }
        if (PrefMethods.getUserData()!!.email != null) {
            request.email = PrefMethods.getUserData()!!.email
        }
        if (PrefMethods.getUserData()!!.birthdate != null) {
            request.birthdate = PrefMethods.getUserData()!!.birthdate
            birthDay.set(request.birthdate)
        }
        if (PrefMethods.getUserData()!!.gender != null) {
            request.gender = PrefMethods.getUserData()!!.gender
            if (request.gender.equals("male")) {
                isMale.set(true)
            } else {
                isMale.set(false)
            }
        }
    }

    fun onSaveClicked() {
        setClickable()
        when {
            request.name.isNullOrEmpty() || request.name.isNullOrBlank() -> {
                setValue(Codes.EMPTY_FIRST_NAME)
            }
            request.last_name.isNullOrEmpty() || request.last_name.isNullOrBlank() -> {
                setValue(Codes.EMPTY_LAST_NAME)
            }
            request.phone_number.isNullOrEmpty() || request.phone_number.isNullOrBlank() -> {
                setValue(Codes.EMPTY_PHONE)
            }
            request.phone_number!!.length < 10 -> {
                setValue(Codes.INVALID_PHONE)
            }
            request.email.isNullOrEmpty() || request.email.isNullOrBlank() -> {
                setValue(Codes.EMAIL_EMPTY)
            }
            !Patterns.EMAIL_ADDRESS.matcher(request.email).matches() -> {
                setValue(Codes.EMAIL_INVALID)
            }
            else -> {
                setShowProgress(true)
                apiupdateProfile()
            }
        }
    }

    fun onMaleClicked() {
        isMale.set(true)
    }

    fun onFeMaleClicked() {
        isMale.set(false)
    }

    fun selectBirthDay() {
        setValue(Codes.SELECT_BIRTHDAY)
    }

    fun apiupdateProfile() {
        request.user_id = PrefMethods.getUserData()!!.id.toString()
        request.lang = PrefMethods.getLanguage()
        request.gender = if (isMale.get()) "male" else "female"
        val baeRepo = BaseRepository()
        val responseCall: Call<UpdateProfileResponse?> = baeRepo.apiInterface.updateProfile(request)
        responseCall.enqueue(object : Callback<UpdateProfileResponse?> {
            override fun onResponse(
                call: Call<UpdateProfileResponse?>,
                response: Response<UpdateProfileResponse?>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {
                        PrefMethods.saveUserData(response.body()!!.data)
                        setValue(Codes.EDIT_PROFILE)
                    } else {
                        setValue(response.body()!!.error.toString())
                    }
                } else {
                    setValue(response.message())
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse?>, t: Throwable) {
                setValue(t.message!!)
                setShowProgress(false)
            }
        })
    }
}