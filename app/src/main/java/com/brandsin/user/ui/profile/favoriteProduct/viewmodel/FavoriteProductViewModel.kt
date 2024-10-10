package com.brandsin.user.ui.profile.favoriteProduct.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.FavoriteResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.ui.profile.favoriteProduct.model.FavoriteProductResponse
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.launch

class FavoriteProductViewModel: BaseViewModel() {

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getFavoriteProductResponse: MutableLiveData<ResponseHandler<FavoriteProductResponse?>> =
        MutableLiveData()
    val getFavoriteProductResponse: LiveData<ResponseHandler<FavoriteProductResponse?>> =
        _getFavoriteProductResponse.toSingleEvent()

    private val _addAndRemoveFavoriteResponse: MutableLiveData<ResponseHandler<FavoriteResponse?>> =
        MutableLiveData()
    val addAndRemoveFavoriteResponse: LiveData<ResponseHandler<FavoriteResponse?>> =
        _addAndRemoveFavoriteResponse.toSingleEvent()

    fun getAllFavoriteProduct() {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getAllFavoriteProduct(
                    PrefMethods.getUserData()!!.id!!,
                    "product"
                )
            }.collect {
                _getFavoriteProductResponse.value = it
            }
        }
    }

    fun addAndRemoveFavorite(productId: Int?) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.addAndRemoveFavorite(
                    PrefMethods.getUserData()!!.id!!,
                    "product",
                    productId.toString(),
                    PrefMethods.getLanguage()
                )
            }.collect {
                _addAndRemoveFavoriteResponse.value = it
            }
        }
    }

}
