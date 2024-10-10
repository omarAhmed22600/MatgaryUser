package com.brandsin.user.ui.menu.refundableProducts.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.refundableProduct.AddRefundableResponse
import com.brandsin.user.model.refundableProduct.ReasonReturnListResponse
import com.brandsin.user.model.refundableProduct.RefundableProductItem
import com.brandsin.user.model.refundableProduct.RefundableProductResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.ui.main.order.newRateOrder.model.UploadMultiFilesResponse
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class RefundableProductViewModel : BaseViewModel() {

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getRefundableProductResponse: MutableLiveData<ResponseHandler<RefundableProductResponse?>> =
        MutableLiveData()
    val getRefundableProductResponse: LiveData<ResponseHandler<RefundableProductResponse?>> =
        _getRefundableProductResponse.toSingleEvent()

    private val _getReasonsReturnResponse: MutableLiveData<ResponseHandler<ReasonReturnListResponse?>> =
        MutableLiveData()
    val getReasonsReturnResponse: LiveData<ResponseHandler<ReasonReturnListResponse?>> =
        _getReasonsReturnResponse.toSingleEvent()

    private val _addRefundableProductResponse: MutableLiveData<ResponseHandler<AddRefundableResponse?>> =
        MutableLiveData()
    val addRefundableProductResponse: LiveData<ResponseHandler<AddRefundableResponse?>> =
        _addRefundableProductResponse.toSingleEvent()

    private val _uploadMultiFilesResponse: MutableLiveData<ResponseHandler<UploadMultiFilesResponse?>> =
        MutableLiveData()
    val uploadMultiFilesResponse: LiveData<ResponseHandler<UploadMultiFilesResponse?>> =
        _uploadMultiFilesResponse.toSingleEvent()

    fun getAllRefundableProducts() {
        obsIsLoading.set(true)
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getAllRefundableProducts(
                    PrefMethods.getUserData()!!.id!!
                )
            }.collect {
                _getRefundableProductResponse.value = it
            }
        }
    }

    fun getAllReasonsReturnList() {
        obsIsLoading.set(true)
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getAllReasonsReturnList(
                    PrefMethods.getLanguage()
                )
            }.collect {
                _getReasonsReturnResponse.value = it
            }
        }
    }

    fun addRefundableProduct(
        refundableProductItem: RefundableProductItem?,
        pickImage: String?,
        notes: String?,
        selectedReasonReturnId: Int
    ) {
        obsIsLoading.set(true)
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.addRefundableProduct(
                    PrefMethods.getUserData()?.id ?: 0,
                    refundableProductItem?.skus?.get(0)?.productId ?: 0,
                    selectedReasonReturnId,
                    refundableProductItem?.orderItemId ?: 0,
                    notes,
                    PrefMethods.getLanguage(),
                    checkImagePath(pickImage)
                )
            }.collect {
                _addRefundableProductResponse.value = it
            }
        }
    }

    /*fun uploadMultiFiles(file: String) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.uploadMultiFiles(checkImagePath(file))
            }.collect {
                _uploadMultiFilesResponse.value = it
            }
        }
    }*/

    private fun File.toMultiPartV(key: String): MultipartBody.Part {
        val reqFile = asRequestBody("*/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            key,
            name, // filename, this is optional
            reqFile
        )
    }

    private fun checkImagePath(imagePath: String?): MultipartBody.Part? {
        Log.d("checkImagePath", "imagePath == $imagePath")
        return if (imagePath?.endsWith(".mp4") == true) {
            checkMediaPath(imagePath, "video/*")
        } else {
            checkMediaPath(imagePath, "image/*")
        }
    }

    private fun checkMediaPath(mediaPath: String?, mediaType: String): MultipartBody.Part? {
        return if (mediaPath != null) {
            val mediaFile = File(mediaPath)
            val mediaRequestBody =
                mediaFile.asRequestBody(mediaType.toMediaTypeOrNull())

            mediaRequestBody.let {
                MultipartBody.Part.createFormData(
                    "media",
                    mediaFile.name,
                    it
                )
            }
        } else {
            null
        }
    }
}
