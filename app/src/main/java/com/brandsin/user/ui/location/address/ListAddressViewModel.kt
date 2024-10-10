package com.brandsin.user.ui.location.address

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.location.addresslist.ListAddressesResponse
import com.brandsin.user.model.location.deleteaddress.DeleteAddressResponse
import com.brandsin.user.model.location.setdefault.SetDefaultAddressResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("UNCHECKED_CAST")
class ListAddressViewModel : BaseViewModel() {

    var addressAdapter = AddressAdapter()

    private var addressesList = mutableListOf<AddressListItem>()

    fun getUserStatus() {
        when {
            PrefMethods.getUserData() != null -> {
                obsIsLogin.set(true)
                obsIsLoading.set(true)
                getDeliveryAddresses()
            }

            else -> {
                obsIsLogin.set(false)
            }
        }
    }

    init {
        getUserStatus()
    }

    fun onAddAddressClicked() {
        setValue(Codes.ADD_ADDRESS_CLICKED)
    }

    fun onAddClicked() {
        setValue(Codes.ADD_ADDRESS_CLICKED)
    }

    fun onLoginClicked() {
        setValue(Codes.LOGIN_CLICKED)
    }

    fun getDeliveryAddresses() {
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
//                            getDefaultAddressFromApi()
                            PrefMethods.saveDefaultAddress(res.addressesList[0])
                            addressAdapter.updateList(res.addressesList as List<AddressListItem>)
                            addressesList.addAll(res.addressesList)
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

    fun deleteDeliveryAddress(item: AddressListItem) {
        obsIsVisible.set(true)
        requestCall<DeleteAddressResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().deleteDeliveryAddress(
                    item.id!!,
                    PrefMethods.getLanguage()
                )
            }
        })
        { res ->
            obsIsVisible.set(false)
            when (res!!.isSuccess) {
                true -> {
                    addressAdapter.removeItem(item)
                    apiResponseLiveData.value = ApiResponse.successMessage(res.message.toString())
                    addressesList.remove(item)
                    when (addressesList.size) {
                        0 -> {
                            obsIsFull.set(false)
                            obsIsEmpty.set(true)
                            PrefMethods.deleteDefaultAddress()
                        }
                    }
                }

                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message.toString())
                }
            }
        }
    }

    fun setDefaultAddress(item: AddressListItem) {
        obsIsVisible.set(true)
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
//                    getDefaultAddressFromApi()
                }

                else -> {
                    obsIsVisible.set(false)
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message.toString())
                }
            }
        }
    }

//    private fun getDefaultAddressFromApi() {
//
//        requestCall<GetDefaultAddressResponse?>({ withContext(Dispatchers.IO) {
//            return@withContext getApiRepo().getDefaultAddress(PrefMethods.getUserData()!!.id!! , PrefMethods.getLanguage())
//        } })
//        { res ->
//            obsIsVisible.set(false)
//            when (res!!.isSuccess) {
//                true -> {
//                    PrefMethods.saveDefaultAddress(res.defaultAddressItem!![0])
//                     apiResponseLiveData.value = ApiResponse.success(res.defaultAddressItem[0])
//                }
//            }
//        }
//    }

    fun onBackPressed() {
        setValue(Codes.BACK_PRESSED)
    }
}