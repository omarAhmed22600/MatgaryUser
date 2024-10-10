package com.brandsin.user.ui.dialogs.chooselocation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.ApiInterface
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.location.addresslist.ListAddressesResponse
import com.brandsin.user.model.location.setdefault.SetDefaultAddressResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.BaseApiResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.requestCall
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseAddressViewModel : BaseViewModel() {

     val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getListAddressResponse: MutableLiveData<ResponseHandler<ListAddressesResponse?>> =
        MutableLiveData()
    val getListAddressResponse: LiveData<ResponseHandler<ListAddressesResponse?>> =
        _getListAddressResponse.toSingleEvent()

    fun getAllAddresses() {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getAllAddresses(
                    PrefMethods.getUserData()!!.id!!,
                    PrefMethods.getLanguage(),
                    1
                )
            }.collect {
                _getListAddressResponse.value = it
            }
        }
    }

    fun getListAddresses() {
        requestCall<ListAddressesResponse?>({
            withContext(Dispatchers.IO) { // to return a result its like asyncTask() and await
                return@withContext getApiRepo().getListAddresses(
                    PrefMethods.getUserData()!!.id!!,
                    PrefMethods.getLanguage(),
                    1
                )
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    obsIsLoading.set(false)
                    when {
                        res.addressesList!!.isNotEmpty() -> {
                            // addressAdapter.updateList(res.addressesList as List<AddressListItem>)
                            // addressesList.addAll(res.addressesList)
                            obsIsFull.set(true)
                            obsIsEmpty.set(false)
                        }

                        else -> {
                            obsIsFull.set(false)
                            obsIsEmpty.set(true)
                        }
                    }
                }

                else -> {
                    obsIsLoading.set(false)
                    obsIsEmpty.set(true)
                    obsIsFull.set(false)
                }
            }
        }
    }

    fun setDefaultAddress(item: AddressListItem) {
        // obsIsVisible.set(true)
        requestCall<SetDefaultAddressResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().setDefaultAddress(
                    PrefMethods.getUserData()!!.id!!,
                    item.id!!,
                    PrefMethods.getLanguage()
                )
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    // getDefaultAddressFromApi()
                }

                else -> {
                    /*obsIsVisible.set(false)
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message.toString())*/
                }
            }
        }
    }
}