package com.brandsin.user.ui.menu.discover

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.menu.favourits.FavouritsResponse
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscoverViewModel : BaseViewModel()
{
    lateinit var storiesAdapter : StoriesAdapter
    lateinit var latitude : String
    lateinit var longitude : String
    private var isFirstTime = true


    fun setStoriesListner(onStoryClickedListner: StoriesAdapter.OnStoryClickedListner){
        storiesAdapter  = StoriesAdapter(onStoryClickedListner)
        storiesAdapter.setStoryViewType(2)
    }


//    fun onLoginClicked() {
//        setValue(Codes.LOGIN_CLICKED)
//    }

    init {
        when {
            isFirstTime -> {
                isFirstTime = false
                getLatLng()
                getDiscover()
            }
        }
    }

    private fun getLatLng() {
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

    fun getDiscover()
    {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<FavouritsResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getDiscover( latitude, longitude)
            }
        })
        { res ->

            obsIsLoading.set(false)
            when (res!!.success) {
                true -> {
                    res.let {
                        if(it.stories!!.isNotEmpty())
                        {
                            obsIsEmpty.set(false)
                            obsIsFull.set(true)
                            obsIsLoadingStores.set(false)
                            obsHideRecycler.set(true)
                            storiesAdapter.updateList(res.stories  as MutableList<ArrayList<StoriesItem>>)
                        }
                        else {
                            obsIsEmpty.set(true)
                            obsIsFull.set(false)
                        }
                    }
                }
                else -> {}
            }
        }
    }

}