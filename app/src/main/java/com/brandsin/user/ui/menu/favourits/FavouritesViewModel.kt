package com.brandsin.user.ui.menu.favourits

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.menu.favourits.FavoriteStoriesResponse
import com.brandsin.user.model.menu.favourits.FavouritesResponse
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.refundableProduct.RefundableProductResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.requestCall
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.ui.main.home.addedstories.StoryAdapter
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.menu.favourits.adapter.FavoriteStoriesAdapter
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class FavouritesViewModel : BaseViewModel() {

    // lateinit var storiesAdapter: StoriesAdapter

    var latitude: String = Const.latitude.toString()
    var longitude: String = Const.longitude.toString()
    private var isFirstTime = true

    var obsCartCount = ObservableField<Int>()
    var obsAddress = ObservableField<String>()

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _getFavouritesListResponse: MutableLiveData<ResponseHandler<FavoriteStoriesResponse?>> =
        MutableLiveData()
    val getFavouritesListResponse: LiveData<ResponseHandler<FavoriteStoriesResponse?>> =
        _getFavouritesListResponse.toSingleEvent()

    /*fun onLoginClicked() {
        setValue(Codes.LOGIN_CLICKED)
    }*/

    /*init {
        when {
            isFirstTime -> {
                isFirstTime = false
                // getLatLng()
                getFavourites(isSeen = 0)
            }
        }
    }*/

    /*private fun getLatLng() {
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
    }*/

    fun getFavourites(isSeen: Int) {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        obsIsLoading.set(true)
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getFavourites(
                    PrefMethods.getUserData()!!.id, isSeen
                )
            }.collect {
                Timber.e("vm response is $it")
                _getFavouritesListResponse.value = it
            }
        }
    }

    /*fun getFavourites(isSeen: Int) {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<FavoriteStoriesResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getFavourites(
                    PrefMethods.getUserData()!!.id, isSeen
                )
            }
        })
        { res ->
            obsIsLoading.set(false)
            when (res!!.success) {
                true -> {
                    res.let {
                        if (it.stories?.isNotEmpty() == true) {
                            obsIsFull.set(true)
                            obsIsLoadingStores.set(false)
                            obsHideRecycler.set(true)
                            favoriteStoriesAdapter.updateList(it.stories)
                        } else {
                            obsIsEmpty.set(true)
                            obsIsFull.set(false)
                        }
                    }
                }

                else -> {}
            }
        }
    }*/

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

    fun onCartClicked() {
        setValue(Codes.CART_CLICKED)
    }

    fun onSearchClicked() {
        setValue(Codes.SEARCH_CLICKED)
    }
}