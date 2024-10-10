package com.brandsin.user.ui.location.addaddress

import androidx.lifecycle.MutableLiveData
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.location.addaddress.AddAddressRequest
import com.brandsin.user.model.location.addaddress.AddAddressResponse
import com.brandsin.user.model.location.addaddress.NewAddressResponse
import com.brandsin.user.model.location.addresstype.AddressTypeItem
import com.brandsin.user.model.location.addresstype.AddressTypesResponse
import com.brandsin.user.model.location.getdefault.GetDefaultAddressResponse
import com.brandsin.user.model.location.setdefault.SetDefaultAddressResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddAddressViewModel : BaseViewModel() {
    var addAddressRequest = AddAddressRequest()
    var newAddressResponse = NewAddressResponse()
    var isMapReady = MutableLiveData<Boolean>()
    var typesList = mutableListOf<AddressTypeItem>()
    var typesSpinnerLst = mutableListOf<String>()

    fun onAddAddressClicked() {
        setClickable()
        when {
            addAddressRequest.firstName == null -> {
                setValue(Codes.EMPTY_FIRST_NAME)
            }

            addAddressRequest.lastName == null -> {
                setValue(Codes.EMPTY_LAST_NAME)
            }

            addAddressRequest.type == null -> {
                setValue(Codes.EMPTY_TYPE)
            }

            addAddressRequest.streetName == null -> {
                setValue(Codes.EMPTY_streetName)
            }

            /*addAddressRequest.addressName == null -> {
                setValue(Codes.EMPTY_addressName)
            }*/

            addAddressRequest.phoneNumber == null -> {
                setValue(Codes.EMPTY_PHONE)
            }

            addAddressRequest.phoneNumber!!.length < 10 -> {
                setValue(Codes.INVALID_PHONE)
            }

            else -> {
                typesList.forEach {
                    if (it.label == addAddressRequest.type) {
                        addAddressRequest.type = it.value
                    }
                }

                addAddressToApi()
            }
        }
    }

    fun changeLocationClicked() {
        setValue(Codes.CHANGE_LOCATION)
    }

    init {
        getAddressTypes()
    }

    private fun getAddressTypes() {
        requestCall<AddressTypesResponse?>({
            withContext(Dispatchers.IO) { // to return a result its like asyncTask() and await
                return@withContext getApiRepo().getAddressTypes()
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    when {
                        res.typesList!!.isNotEmpty() -> {
                            typesList = res.typesList as MutableList<AddressTypeItem>
                            typesList.forEach {
                                typesSpinnerLst.add(it.label.toString())
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun addAddressToApi() {
        collectAddressRequest()

        obsIsVisible.set(true)
        requestCall<AddAddressResponse?>({
            withContext(Dispatchers.IO) { // to return a result its like asyncTask() and await
                return@withContext getApiRepo().addNewAddress(addAddressRequest)
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    newAddressResponse = res.newAddressResponse!!
                    setDefaultAddress(newAddressResponse.id!!)
                }

                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message.toString())
                }
            }
        }
    }

    private fun collectAddressRequest() {
        addAddressRequest.run {
            userId = PrefMethods.getUserData()!!.id
            lang = PrefMethods.getLanguage()
            countryId = PrefMethods.getCountryId().toInt()
            cityId = 1
            stateId = 1
        }
    }

    private fun setDefaultAddress(addressId: Int) {
        obsIsVisible.set(true)
        requestCall<SetDefaultAddressResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().setDefaultAddress(
                    PrefMethods.getUserData()!!.id!!,
                    addressId,
                    PrefMethods.getLanguage()
                )
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    getDefaultAddressFromApi()
                }

                else -> {
                    obsIsVisible.set(false)
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message.toString())
                }
            }
        }
    }

    private fun getDefaultAddressFromApi() {
        requestCall<GetDefaultAddressResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getDefaultAddress(
                    PrefMethods.getUserData()!!.id!!,
                    PrefMethods.getLanguage()
                )
            }
        })
        { res ->
            obsIsVisible.set(false)
            when (res!!.isSuccess) {
                true -> {
                    obsIsVisible.set(false)
                    setValue(Codes.ADDRESS_SAVED)
                    // apiResponseLiveData.value = ApiResponse.success(res.defaultAddressItem!![0])
                }

                else -> {}
            }
        }
    }

    fun onBackPressed() {
        setValue(Codes.BACK_PRESSED)
    }
}