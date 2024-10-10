package com.brandsin.user.ui.main.homenew.moresub.brand.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.FavoriteResponse
import com.brandsin.user.model.order.homenew.ItemsSectionResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.ui.profile.favoriteProduct.model.FavoriteProductResponse
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.launch

class BrandViewModel: BaseViewModel() {

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getBrandsItemsResponse: MutableLiveData<ResponseHandler<ItemsSectionResponse?>> =
        MutableLiveData()
    val getBrandsItemsResponse: LiveData<ResponseHandler<ItemsSectionResponse?>> =
        _getBrandsItemsResponse.toSingleEvent()

    fun getItemsOfSectionId(sectionId: Int) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getItemsOfSectionId(sectionId)
            }.collect {
                _getBrandsItemsResponse.value = it
            }
        }
    }

}