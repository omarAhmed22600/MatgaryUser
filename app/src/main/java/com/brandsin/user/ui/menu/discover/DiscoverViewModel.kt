package com.brandsin.user.ui.menu.discover

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.discover.DiscoverResponse
import com.brandsin.user.model.menu.favourits.FavouritesResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.requestCall
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverViewModel : BaseViewModel() {

    // lateinit var storiesAdapter: StoriesAdapter

    lateinit var latitude: String
    lateinit var longitude: String
    var isFirstTime = true

    var obsCartCount = ObservableField<Int>()
    var obsAddress = ObservableField<String>()

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getStoriesListResponse: MutableLiveData<ResponseHandler<DiscoverResponse?>> =
        MutableLiveData()
    val getStoriesListResponse: LiveData<ResponseHandler<DiscoverResponse?>> =
        _getStoriesListResponse.toSingleEvent()

    /* init {
        when {
            isFirstTime -> {
                isFirstTime = false
                getLatLng()
                // getDiscover()
                getStoriesList(isSeen = 0)
            }
        }
    } */

    /*fun setStoriesListener(onStoryClickedListener: StoriesAdapter.OnStoryClickedListener) {
        storiesAdapter = StoriesAdapter(onStoryClickedListener)
        storiesAdapter.setStoryViewType(2)
    }*/

    fun getStoriesList(isSeen: Int) {
        obsIsLoading.set(true)
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getStoriesListInDiscover( // user_id=1&lat=46.6858242&lng=24.6809393&is_seen=1
                    PrefMethods.getUserData()!!.id!!,
                    latitude,
                    longitude,
                    isSeen
                )
            }.collect {
                obsIsLoading.set(false)
                _getStoriesListResponse.value = it
            }
        }
    }

//    fun onLoginClicked() {
//        setValue(Codes.LOGIN_CLICKED)
//    }

    fun getLatLng() {
        when {
            PrefMethods.getDefaultAddress() == null -> {
                when {
                    PrefMethods.getUserLocation() != null -> {
                        when {
                            PrefMethods.getUserLocation()!!.address != null || PrefMethods.getUserLocation()!!.address != "null" -> {
                                latitude = PrefMethods.getUserLocation()!!.lat.toString()
                                longitude = PrefMethods.getUserLocation()!!.lng.toString()
                            }

                            else -> {
                                latitude = Const.latitude.toString()
                                longitude = Const.longitude.toString()
                            }
                        }
                    }

                    else -> {
                        latitude = Const.latitude.toString()
                        longitude = Const.longitude.toString()
                    }
                }
            }

            else -> {
                latitude = PrefMethods.getDefaultAddress()!!.lat.toString()
                longitude = PrefMethods.getDefaultAddress()!!.lng.toString()
            }
        }
    }

    fun getDiscover() {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<FavouritesResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getDiscover(latitude, longitude)
            }
        })
        { res ->

            obsIsLoading.set(false)
            when (res!!.success) {
                true -> {
                    res.let {
                        if (it.stories!!.isNotEmpty()) {
                            obsIsEmpty.set(false)
                            obsIsFull.set(true)
                            obsIsLoadingStores.set(false)
                            obsHideRecycler.set(true)
                            // storiesAdapter.updateList(res.stories as MutableList<ArrayList<StoriesItem>>)
                        } else {
                            obsIsEmpty.set(true)
                            obsIsFull.set(false)
                        }
                    }
                }

                else -> {}
            }
        }
    }

    fun getUserStatus() {
        when {
            PrefMethods.getUserData() != null -> {
                // getDefaultAddressFromApi()
                getUserAddress()
                getCartCount()
            }

            else -> {
                getDefaultAddress()
                obsCartCount.set(0)
            }
        }
    }

    fun getDefaultAddress() {
        when {
            PrefMethods.getUserLocation() != null -> {
                when {
                    PrefMethods.getUserLocation()!!.address != null || PrefMethods.getUserLocation()!!.address != "null" -> {
                        obsAddress.set(PrefMethods.getUserLocation()!!.address.toString())
                        latitude = PrefMethods.getUserLocation()!!.lat.toString()
                        longitude = PrefMethods.getUserLocation()!!.lng.toString()
                    }

                    else -> {
                        obsAddress.set("يجب تحديد موقك الجغرافي حتي نقوم بتوصيل كل ما تحتاجة بدقة اعلي")
                    }
                }
            }

            else -> {
                obsAddress.set("يجب تحديد موقك الجغرافي حتي نقوم بتوصيل كل ما تحتاجة بدقة اعلي")
            }
        }

        obsIsFull.set(false)
        obsIsLoading.set(true)
    }

    private fun getUserAddress() {
        when {
            PrefMethods.getDefaultAddress() == null -> {
                when {
                    PrefMethods.getUserLocation() != null -> {
                        when {
                            PrefMethods.getUserLocation()!!.address != null || PrefMethods.getUserLocation()!!.address != "null" -> {
                                obsAddress.set(PrefMethods.getUserLocation()!!.address.toString())
                                latitude = PrefMethods.getUserLocation()!!.lat.toString()
                                longitude = PrefMethods.getUserLocation()!!.lng.toString()
                            }

                            else -> {
                                obsAddress.set("يجب تحديد موقك الجغرافي حتي نقوم بتوصيل كل ما تحتاجة بدقة اعلي")
                            }
                        }
                    }

                    else -> {
                        obsAddress.set("يجب تحديد موقك الجغرافي حتي نقوم بتوصيل كل ما تحتاجة بدقة اعلي")
                    }
                }
            }

            else -> {
                obsAddress.set(PrefMethods.getDefaultAddress()!!.streetName.toString())
                latitude = PrefMethods.getDefaultAddress()!!.lat.toString()
                longitude = PrefMethods.getDefaultAddress()!!.lng.toString()
            }
        }

        obsIsFull.set(false)
        obsIsLoading.set(true)
    }

    private fun getCartCount() {
        when {
            PrefMethods.getUserCart() == null -> {
                obsCartCount.set(0)
            }

            else -> {
                val userCartData = PrefMethods.getUserCart()!!
                when {
                    userCartData.cartItems!!.isNotEmpty() -> {
                        obsCartCount.set(userCartData.cartItems!!.size)
                    }

                    else -> {
                        obsCartCount.set(0)
                    }
                }
            }
        }
    }
}