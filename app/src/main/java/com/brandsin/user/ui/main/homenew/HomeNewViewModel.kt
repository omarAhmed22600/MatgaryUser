package com.brandsin.user.ui.main.homenew

import android.util.Log
import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseRepository
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.auth.devicetoken.DeviceTokenRequest
import com.brandsin.user.model.auth.devicetoken.DeviceTokenResponse
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.location.getdefault.GetDefaultAddressResponse
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homenew.PopupsItem
import com.brandsin.user.model.order.homenew.SectionsItem
import com.brandsin.user.model.order.homepage.HomePageResponse
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.sliders.SlidersResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.main.homenew.category.CategoriesAdapter
import com.brandsin.user.ui.main.homenew.ads.AdsAdapter
import com.brandsin.user.ui.main.homenew.more.MoreAdapter
import com.brandsin.user.ui.main.homenew.moreslider.MoreSliderAdapter
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeNewViewModel : BaseViewModel()
{


    var homePageResponse = HomePageResponse()
    var homeNewResponse = HomeNewResponse()
    var deviceTokenRequest = DeviceTokenRequest()
    var categoriesAdapter  = CategoriesAdapter()
    lateinit var storiesAdapter :StoriesAdapter
    var adsAdapter  = AdsAdapter()
    var moreAdapter  = MoreAdapter()
    var obsCartCount = ObservableField<Int>()
    var obsAddress = ObservableField<String>()
    var latitude : String = Const.latitude.toString()
    var longitude : String = Const.longitude.toString()

    var categoriesList  = mutableListOf<CategoriesItem>()
    var moreList  = mutableListOf<SectionsItem>()

    var popupList  = mutableListOf<PopupsItem>()
    var storiesList  = mutableListOf<ArrayList<StoriesItem>>()

    var slidersResponse = SingleLiveEvent<SlidersResponse>()
    var moreSliderAdapter = MoreSliderAdapter()

    fun onCartClicked() {
        setValue(Codes.CART_CLICKED)
    }

    fun onSearchClicked() {
        setValue(Codes.SEARCH_CLICKED)
    }

    fun setStoriesListner(onStoryClickedListner: StoriesAdapter.OnStoryClickedListner){
         storiesAdapter  = StoriesAdapter(onStoryClickedListner)
    }

    fun onLocationClicked() {
        setValue(Codes.LOCATION_CLICKED)
    }

    fun getUserStatus() {
        when {
            PrefMethods.getUserData() != null -> {
//                getDefaultAddressFromApi()
                getUserAddress()
                getCartCount()
            }
            else -> {
                getDefaultAddress()
                obsCartCount.set(0)
            }
        }
    }
    private fun getCartCount()
    {
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

    fun getUserAddress() {
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
        getHomePage()
    }
    private fun getDefaultAddressFromApi() {

        requestCall<GetDefaultAddressResponse?>({ withContext(Dispatchers.IO) {
            return@withContext getApiRepo().getDefaultAddress(PrefMethods.getUserData()!!.id!! , PrefMethods.getLanguage())
        } })
        { res ->
            obsIsVisible.set(false)
            when (res!!.isSuccess) {
                true -> {
                    PrefMethods.saveDefaultAddress(res.defaultAddressItem!![0] )
                    apiResponseLiveData.value = ApiResponse.success(res.defaultAddressItem[0])
                }
                else -> {}
            }
        }
    }
    fun getDefaultAddress()
    {
        when
        {
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
        getHomePage()
    }



    fun getSlider(key: String) {
        requestCall<SlidersResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getSlider(key, PrefMethods.getLanguage())
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    setShowProgress(false)
                    obsIsLoading.set(false)
                    obsIsFull.set(true)
                    obsIsLoadingStores.set(false)
                    obsHideRecycler.set(true)
                    res.data.let {
                        slidersResponse.value = res
                        if (it!!.slides!!.isNotEmpty()) {

                            /*slider*/
                            moreSliderAdapter.updateList(it!!.slides)


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


    fun getHomePage() {
        obsIsEmpty.set(false)
        requestCall<HomeNewResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getHomeNew(latitude , longitude, "5" , PrefMethods.getLanguage())
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    homeNewResponse = res

                    obsIsLoading.set(false)
                    obsIsFull.set(true)
                    obsIsLoadingStores.set(false)
                    obsHideRecycler.set(true)

                    if(res.popups!!.isNotEmpty()) {
                        /* popups List */
                        popupList = res.popups as MutableList<PopupsItem>
                        setValue(Codes.SHOW_POPUP)
                    }

                    if(res.categories!!.isNotEmpty()) {
                        /* Categories List */
                        categoriesList = res.categories as MutableList<CategoriesItem>
                        categoriesAdapter.updateList(categoriesList)
                    }

                    /* Ads List */
//                    adsAdapter.updateList()

                    if(res.sections!!.isNotEmpty()) {
                        /* More List */
                        moreList = res.sections as MutableList<SectionsItem>
                        moreAdapter.updateList(moreList)
                    }

                    //stories list
                    if(res.stories!!.isNotEmpty()) {
                        /* More List */
                        storiesList = res.stories as MutableList<ArrayList<StoriesItem>>
                        storiesAdapter .updateList(storiesList)
                    }else{
                        obsHideRecycler.set(false)
                    }

                    Log.d("storiesList", "getHomePage: "+storiesList.size)
                }
                else -> {
                    obsIsLoading.set(false)
                    obsIsEmpty.set(true)
                    obsIsFull.set(false)
                }
            }
        }
    }



    fun deviceToken(){

        deviceTokenRequest.user_id = PrefMethods.getUserData()!!.id.toString()
        deviceTokenRequest.type = "android_token"

        val baeRepo = BaseRepository()
        val responseCall: Call<DeviceTokenResponse?> = baeRepo.apiInterface.deviceToken(deviceTokenRequest)
        responseCall.enqueue(object : Callback<DeviceTokenResponse?> {
            override fun onResponse(call: Call<DeviceTokenResponse?>, response: Response<DeviceTokenResponse?>) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {

                    }
                } else {
                    setValue(response.message())
                }
            }
            override fun onFailure(call: Call<DeviceTokenResponse?>, t: Throwable) {
                setValue(t.message!!)
                setShowProgress(false)
            }
        })
    }
}