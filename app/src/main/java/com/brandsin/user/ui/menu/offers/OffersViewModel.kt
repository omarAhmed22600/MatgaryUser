package com.brandsin.user.ui.menu.offers

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.menu.favourits.FavouritsResponse
import com.brandsin.user.model.menu.offers.OffersItemDetails
import com.brandsin.user.model.menu.offers.OffersResponse
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.sliders.SlidersResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.main.homenew.moreslider.MoreSliderAdapter
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OffersViewModel : BaseViewModel()
{
    lateinit var storiesAdapter : StoriesAdapter
    var moreSliderAdapter = MoreSliderAdapter()
    var slidersResponse = SingleLiveEvent<SlidersResponse>()
    var offersAdapter  = OffersAdapter()
    lateinit var latitude : String
    lateinit var longitude : String
    private var isFirstTime = true

    fun onLoginClicked() {
        setValue(Codes.LOGIN_CLICKED)
    }

    fun setStoriesListner(onStoryClickedListner: StoriesAdapter.OnStoryClickedListner){
        storiesAdapter  = StoriesAdapter(onStoryClickedListner)

    }


    init {
        when {
            isFirstTime -> {
                isFirstTime = false
                getLatLng()

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


    fun getOffersStories()
    {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<FavouritsResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getOffersStories( )
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
                           // obsIsEmpty.set(true)
                            obsIsFull.set(false)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    fun getSlider(key: String) {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<SlidersResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getSlider(key, PrefMethods.getLanguage())
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    setShowProgress(false)
                    res.data.let {
                        slidersResponse.value = res
                        if (it!!.slides!!.isNotEmpty()) {
                            obsIsEmpty.set(false)
                            obsIsFull.set(true)
                            obsIsLoadingStores.set(false)
                            obsHideRecycler.set(true)
                            /*slider*/
                            moreSliderAdapter.updateList(it!!.slides)
                        }else {
                          //  obsIsEmpty.set(true)
                            obsIsFull.set(false)
                        }
                    }
                }
                else -> {
                    obsIsLoading.set(false)
                    //obsIsEmpty.set(true)
                    obsIsFull.set(false)
                }
            }
        }
    }

    fun getOffers()
    {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<OffersResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getOffers(10 , 0, PrefMethods.getLanguage(), latitude, longitude, "5")
            }
        })
        { res ->

            obsIsLoading.set(false)
            when (res!!.isSuccess) {
                    true -> {
                    res.let {
                        if(it.offersList!!.isNotEmpty())
                        {
                            obsIsEmpty.set(false)
                            obsIsFull.set(true)
                            obsIsLoadingStores.set(false)
                            obsHideRecycler.set(true)
                            offersAdapter.updateList(res.offersList as ArrayList<OffersItemDetails>)
                        }
                        else {
                            //obsIsEmpty.set(true)
                            obsIsFull.set(false)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}