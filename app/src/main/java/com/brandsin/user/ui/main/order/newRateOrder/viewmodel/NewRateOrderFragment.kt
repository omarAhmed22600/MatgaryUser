package com.brandsin.user.ui.main.order.newRateOrder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.MessageResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.ui.main.order.newRateOrder.model.RatingRequest
import com.brandsin.user.ui.main.order.newRateOrder.model.UploadMultiFilesResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class NewRateOrderViewModel : BaseViewModel() {

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _uploadMultiFilesResponse: MutableLiveData<ResponseHandler<UploadMultiFilesResponse?>> =
        MutableLiveData()
    val uploadMultiFilesResponse: LiveData<ResponseHandler<UploadMultiFilesResponse?>> =
        _uploadMultiFilesResponse.toSingleEvent()

    private val _addRateStoreOrProductOrDriverResponse: MutableLiveData<ResponseHandler<MessageResponse?>> =
        MutableLiveData()
    val addRateStoreOrProductOrDriverResponse: LiveData<ResponseHandler<MessageResponse?>> =
        _addRateStoreOrProductOrDriverResponse.toSingleEvent()

    fun addRateStoreOrProductOrDriver(
        ratingRequest: RatingRequest
    ) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.addRateStoreOrProductOrDriver(ratingRequest)
            }.collect {
                _addRateStoreOrProductOrDriverResponse.value = it
            }
        }
    }

    fun uploadMultiFiles(file: String) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.uploadMultiFiles(checkImagePath(file))
            }.collect {
                _uploadMultiFilesResponse.value = it
            }
        }
    }

    private fun checkImagePath(imagePath: String?): MultipartBody.Part? {
        return if (imagePath != null) {
            val picFile = File(imagePath)
            val picRequestBody =
                picFile.asRequestBody("image/*".toMediaTypeOrNull())

            picRequestBody.let {
                MultipartBody.Part.createFormData(
                    "files[]",
                    picFile.name,
                    it,
                )
            }
        } else {
            null
        }
    }
}