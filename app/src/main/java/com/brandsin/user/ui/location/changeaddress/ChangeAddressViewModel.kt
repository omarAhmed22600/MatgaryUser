package com.brandsin.user.ui.location.changeaddress

import androidx.lifecycle.MutableLiveData
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.location.addresstype.AddressTypeItem
import com.brandsin.user.model.location.addresstype.AddressTypesResponse
import com.brandsin.user.model.location.changeaddress.ChangeAddressRequest
import com.brandsin.user.model.location.changeaddress.ChangeAddressResponse
import com.brandsin.user.model.location.changeaddress.ChangeAddressResponseData
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class ChangeAddressViewModel : BaseViewModel()
{
    var changeAddressRequest = ChangeAddressRequest()
    var changeAddressResponse = ChangeAddressResponseData()
    var isMapReady = MutableLiveData<Boolean>()
    var typesList  = mutableListOf<AddressTypeItem>()
    var typesSpinnerLst  = mutableListOf<String>()

    fun onAddAddressClicked() {
        setClickable()

        when {
            changeAddressRequest.firstName == null -> {
                setValue(Codes.EMPTY_FIRST_NAME)
            }
            changeAddressRequest.lastName == null -> {
                setValue(Codes.EMPTY_LAST_NAME)
            }
            changeAddressRequest.type == null -> {
                setValue(Codes.EMPTY_TYPE)
            }
            changeAddressRequest.streetName == null -> {
                setValue(Codes.EMPTY_streetName)
            }
            /*changeAddressRequest.addressName == null -> {
                setValue(Codes.EMPTY_addressName)
            }*/
            changeAddressRequest.phoneNumber == null -> {
                setValue(Codes.EMPTY_PHONE)
            }
            changeAddressRequest.phoneNumber!!.length < 10 -> {
                setValue(Codes.INVALID_PHONE)
            }
            else -> {
                typesList.forEach {
                    if (it.label == changeAddressRequest.type)
                    {
                        changeAddressRequest.type = it.value
                    }
                }
                updateAddress()
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
        obsIsVisible.set(true)

        requestCall<AddressTypesResponse?>({ withContext(Dispatchers.IO) { return@withContext getApiRepo().getAddressTypes() } })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    obsIsVisible.set(false)
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

    private fun updateAddress()
    {
        obsIsVisible.set(true)
        requestCall<ChangeAddressResponse?>({ withContext(Dispatchers.IO) { return@withContext getApiRepo().changeAddress(collectAddressRequest()) } })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    changeAddressResponse = res.changeAddressResponseData!!

                    Timber.e("LaaandMark : " + changeAddressResponse.landmark)

                    obsIsVisible.set(false)
                     setValue(Codes.ADDRESS_CHANGED)
                }
                else -> {
                    apiResponseLiveData.value = ApiResponse.errorMessage(res.message.toString())
                }
            }
        }
    }

    private fun collectAddressRequest() : ChangeAddressRequest{
        changeAddressRequest.run {
            userId = PrefMethods.getUserData()!!.id
            lang = PrefMethods.getLanguage()
            countryId = PrefMethods.getCountryId().toInt()
            cityId = changeAddressRequest.cityId
            stateId = changeAddressRequest.cityId
            landmark = changeAddressRequest.landmark
        }

        return changeAddressRequest
    }
}