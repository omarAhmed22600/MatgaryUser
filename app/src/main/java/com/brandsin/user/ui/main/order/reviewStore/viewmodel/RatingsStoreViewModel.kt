package com.brandsin.user.ui.main.order.reviewStore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.ui.main.order.reviewStore.model.RatingsResponse
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.launch

class RatingsStoreViewModel : BaseViewModel() {
    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getRatingsStoreResponse: MutableLiveData<ResponseHandler<RatingsResponse?>> =
        MutableLiveData()
    val getRatingsStoreResponse: LiveData<ResponseHandler<RatingsResponse?>> =
        _getRatingsStoreResponse.toSingleEvent()

    fun getRatingsStoreList(storeId: Int?, hasMedia: Int) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getRatingsStoreList(
                    "store",
                    PrefMethods.getUserData()?.id ?: 0,
                    storeId,
                    hasMedia
                )
            }.collect {
                _getRatingsStoreResponse.value = it
            }
        }
    }
}