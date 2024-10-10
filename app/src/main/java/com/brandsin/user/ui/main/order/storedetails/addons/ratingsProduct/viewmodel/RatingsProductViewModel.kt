package com.brandsin.user.ui.main.order.storedetails.addons.ratingsProduct.viewmodel

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

class RatingsProductViewModel : BaseViewModel() {

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getRatingsProductResponse: MutableLiveData<ResponseHandler<RatingsResponse?>> =
        MutableLiveData()
    val getRatingsProductResponse: LiveData<ResponseHandler<RatingsResponse?>> =
        _getRatingsProductResponse.toSingleEvent()

    fun getRatingsProductList(productId: Int?) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getRatingsStoreList(
                    "product",
                    PrefMethods.getUserData()?.id ?: 0,
                    productId,
                    null
                )
            }.collect {
                _getRatingsProductResponse.value = it
            }
        }
    }

}