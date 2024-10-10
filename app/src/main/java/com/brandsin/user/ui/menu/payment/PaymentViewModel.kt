package com.brandsin.user.ui.menu.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.MessageResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.launch

class PaymentViewModel : BaseViewModel() {

    var recipientName: String? = null
    var recipientMobileNumber: String? = null
    var numberOfPoints: String? = null

    init {
        obsIsLogin.set(true)
    }

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getWalletTransactionsResponse: MutableLiveData<ResponseHandler<WalletTransactionsResponse?>> =
        MutableLiveData()
    val getWalletTransactionsResponse: LiveData<ResponseHandler<WalletTransactionsResponse?>> =
        _getWalletTransactionsResponse.toSingleEvent()

    private val _transferPointsResponse: MutableLiveData<ResponseHandler<MessageResponse?>> =
        MutableLiveData()
    val transferPointsResponse: LiveData<ResponseHandler<MessageResponse?>> =
        _transferPointsResponse.toSingleEvent()

    private val _updateCreditIncreaseResponse: MutableLiveData<ResponseHandler<UpdateCreditResponse?>> =
        MutableLiveData()
    val updateCreditIncreaseResponse: LiveData<ResponseHandler<UpdateCreditResponse?>> =
        _updateCreditIncreaseResponse.toSingleEvent()

    private val _updateCreditDecreaseResponse: MutableLiveData<ResponseHandler<UpdateCreditResponse?>> =
        MutableLiveData()
    val updateCreditDecreaseResponse: LiveData<ResponseHandler<UpdateCreditResponse?>> =
        _updateCreditDecreaseResponse.toSingleEvent()

    fun getWalletTransactions() {
        obsIsLoading.set(true)
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getWalletTransactions(
                    PrefMethods.getUserData()!!.id!!
                )
            }.collect {
                _getWalletTransactionsResponse.value = it
            }
        }
    }

    fun transferPoints() {
        obsIsLoading.set(true)
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.transferPoints(
                    PrefMethods.getUserData()!!.id!!,
                    recipientMobileNumber.toString(),
                    numberOfPoints!!.toInt()
                )
            }.collect {
                _transferPointsResponse.value = it
            }
        }
    }

    fun updateCreditIncrease(shippingAmount: String?) {
        obsIsLoading.set(true)
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.updateCreditIncrease(
                    PrefMethods.getUserData()!!.id!!,
                    shippingAmount!!,
                )
            }.collect {
                _updateCreditIncreaseResponse.value = it
            }
        }
    }

    fun updateCreditDecrease(shippingAmount: Double?) {
        obsIsLoading.set(true)
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.updateCreditDecrease(
                    PrefMethods.getUserData()!!.id!!,
                    shippingAmount!!,
                )
            }.collect {
                _updateCreditDecreaseResponse.value = it
            }
        }
    }
}