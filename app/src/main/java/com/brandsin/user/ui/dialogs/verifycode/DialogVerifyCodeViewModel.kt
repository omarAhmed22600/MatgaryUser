package com.brandsin.user.ui.dialogs.verifycode

import android.os.CountDownTimer
import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeRequest
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeResponse
import com.brandsin.user.model.order.confirmorder.verifyphome.VerifyCodeRequest
import com.brandsin.user.model.order.confirmorder.verifyphome.VerifyCodeResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DialogVerifyCodeViewModel : BaseViewModel()
{
    var request = VerifyCodeRequest()
    var obsTimer = ObservableField<String>()
    var obsCode = ObservableField<String>()
    var obsClickable = ObservableField<Boolean>()
    var obsPhoneNumber = ObservableField<String>()

    fun onChangePhoneClicked() {
        setValue(Codes.CHANGE_PHONE)
    }

    fun onCancelClicked() {
        setClickable()
        setValue(Codes.CANCEL_CLICKED)
    }

    fun onVerifyClicked() {
        setClickable()
        when {
            obsCode.get() == null -> {
                setClickable()
                setValue(Codes.CODE_EMPTY)
            }
            obsCode.get()!!.length < 4 -> {
                setClickable()
                setValue(Codes.CODE_SHORT)
            }
            else -> {
                request.lang = PrefMethods.getLanguage()
                verifyCode()
            }
        }
    }

    private fun verifyCode()
    {
        setClickable()
        request.lang = PrefMethods.getLanguage()
        request.code = obsCode.get()
        obsIsVisible.set(true)

        requestCall<VerifyCodeResponse?>({ withContext(Dispatchers.IO) { return@withContext getApiRepo().verifyCode(request) } })
        { res ->
            obsIsVisible.set(false)
            when (res!!.isSuccess) {
                true -> {
                    apiResponseLiveData.value = ApiResponse.success(res)
                }
                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.error)
                }
            }
        }
    }

    private fun startTimer() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                obsTimer.set("${getString(R.string.resend_in)}  ${millisUntilFinished / 1000}")
                obsClickable.set(false)
            }
            override fun onFinish() {
                obsTimer.set(MyApp.context.getString(R.string.zero_time) )
                obsClickable.set(true)
            }
        }.start()
    }

    fun onResendClicked() {
        setClickable()
        val sendCodeRequest = SendCodeRequest()
        sendCodeRequest.lang = PrefMethods.getLanguage()
        sendCodeRequest.addressId = request.addressId
        obsIsVisible.set(true)
        requestCall<SendCodeResponse?>({
            withContext(Dispatchers.IO) { return@withContext getApiRepo().sendCode(sendCodeRequest) } })
        { res ->
            obsIsVisible.set(false)
            obsClickable.set(true)
            when (res!!.isSuccess) {
                true -> {
                    startTimer()
                    apiResponseLiveData.value = ApiResponse.successMessage(res.code.toString())
                }
                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.error)
                }
            }
        }
    }

    init {
        obsClickable.set(false)
        startTimer()
    }
}