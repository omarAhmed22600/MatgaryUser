package com.brandsin.user.ui.dialogs.sendcode

import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeRequest
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DialogSendCodeViewModel : BaseViewModel()
{
    var request = SendCodeRequest()
    var obsPhoneNumber = ObservableField<String>()

    fun onCancelClicked()
    {
        setClickable()
        setValue(Codes.CANCEL_CLICKED)
    }

    fun onSendCodeClicked()
    {
        setClickable()
        request.lang = PrefMethods.getLanguage()
        obsIsVisible.set(true)
        requestCall<SendCodeResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().sendCode(request)
            }
        })
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
}