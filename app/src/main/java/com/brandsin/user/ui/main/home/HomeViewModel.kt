package com.brandsin.user.ui.main.home

import android.content.Context
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.database.BaseRepository
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.auth.devicetoken.DeviceTokenRequest
import com.brandsin.user.model.auth.devicetoken.DeviceTokenResponse
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homepage.*
import com.brandsin.user.model.sliders.SlidersResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.home.category.CategoriesAdapter
import com.brandsin.user.ui.main.home.stores.StoresAdapter
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.main.home.subcategory.SubCategoriesAdapter
import com.brandsin.user.ui.main.homenew.moreslider.MoreSliderAdapter
import com.brandsin.user.ui.main.order.storedetails.banners.BannersAdapter
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : BaseViewModel() {

    var context: Context? = null
    var moreSliderAdapter = MoreSliderAdapter()
    var bannersAdapter = BannersAdapter()
    var homePageResponse = HomePageResponse()
    var slidersResponse = SingleLiveEvent<SlidersResponse>()
    var linearLayoutMng = ObservableField<Boolean>()
    var layoutManager = ObservableField<RecyclerView.LayoutManager>()
    var homeNewResponse = HomeNewResponse()
    var deviceTokenRequest = DeviceTokenRequest()
    var categoriesAdapter = CategoriesAdapter()
    var subCategoryAdapter = SubCategoriesAdapter()
    var storesAdapter = StoresAdapter()
    var obsCartCount = ObservableField<Int>()
    var obsAddress = ObservableField<String>()
    var latitude: String = Const.latitude.toString()
    var longitude: String = Const.longitude.toString()
    var categoriesList = mutableListOf<CategoriesItem>()
    var tagsList = mutableListOf<TagsItem>()
    var storesList = mutableListOf<ShopsItem>()
    var showStories = SingleLiveEvent<Boolean>()
    lateinit var storiesAdapter: StoriesAdapter
    var storiesList = ArrayList<ArrayList<StoriesItem>>()

    var categoryId = ""
    var categoryImg = ""
    var store_ids = ArrayList<String>()

    fun setStoriesListner(onStoryClickedListner: StoriesAdapter.OnStoryClickedListner) {
        storiesAdapter = StoriesAdapter(onStoryClickedListner)
    }

    fun setCurrentContext(context: Context) {
        this.context = context
    }

    fun onCartClicked() {
        setValue(Codes.CART_CLICKED)
    }

    fun onSearchClicked() {
        setValue(Codes.SEARCH_CLICKED)
    }

    fun onLinearLayoutClicked() {
        linearLayoutMng.set(true)
       // layoutManager.set(LinearLayoutManager(context))
        storesAdapter.setAdapterViewType(0)
    }

    fun onHorizontalLayoutClicked() {
        linearLayoutMng.set(false)
       // layoutManager.set(GridLayoutManager(context,2))
        storesAdapter.setAdapterViewType(1)
    }

    fun onLocationClicked() {
        setValue(Codes.LOCATION_CLICKED)
    }

    fun getUserStatus(categoryId: String) {
        when {
            PrefMethods.getUserData() != null -> {
                getUserAddress(categoryId)
                getCartCount()
            }
            else -> {
                getDefaultAddress(categoryId)
                obsCartCount.set(0)
            }
        }
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

    fun getUserAddress(categoryId: String) {
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
        getHomePage(categoryId)
    }

    fun getDefaultAddress(categoryId: String) {
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
        getHomePage(categoryId)
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

    fun getHomePage(categoryId: String) {
        subCategoryAdapter.selectedPosition = 0
        obsIsEmpty.set(false)
        requestCall<HomePageResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getHomePage(
                    latitude,
                    longitude,
                    "5",
                    PrefMethods.getLanguage(),
                    categoryId
                )
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    res.data.let {
                        getSlider("show-category")
                        homePageResponse = res
                        if (it!!.categories!!.isNotEmpty()) {
                            setShowProgress(false)
                            obsIsLoading.set(false)
                            obsIsFull.set(true)
                            obsIsLoadingStores.set(false)
                            obsHideRecycler.set(true)
                            // obsHidesTories.set(false)
                            /* Categories List */
                            categoriesList = res.data!!.categories as MutableList<CategoriesItem>
                            categoriesAdapter.updateList(categoriesList)

                            /* Tags List */
                            tagsList = res.data.categories!![0]!!.tags as MutableList<TagsItem>
                            tagsList.add(
                                0,
                                TagsItem(thumbnail = categoryImg, name = MyApp.getInstance().getString(R.string.all_tags), id = null)
                            )
                            subCategoryAdapter.updateList(tagsList)

                            /* Stores List */
                            storesList = res.data.shops as MutableList<ShopsItem>
                            storesAdapter.updateList(storesList)


                            /* Stories List */

                            if (res.data.stories!!.isNotEmpty()) {
                                /* More List */
                                showStories.value = true
                                storiesList = res.data.stories as ArrayList<ArrayList<StoriesItem>>
                                //var sotories: MutableList<ArrayList<StoriesItem>> = ArrayList()
                                //sotories.add(storiesList)
                                storiesAdapter.updateList(storiesList)
                            } else {
                                showStories.value = false
                            }
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

    fun filterStores(tagId: Int) {
        var filteredList = mutableListOf<ShopsItem>()
        when (tagId) {
            0 -> {
                filteredList = storesList
            }
            else -> {
                storesList.forEach {

                    when {
                        it.tags!!.isEmpty() -> {
                            filteredList.add(it)
                        }
                        else -> {
                            when (it.tags[0]!!.id) {
                                tagId -> {
                                    filteredList.add(it)
                                }
                            }
                        }
                    }
                }
            }
        }
        storesAdapter.updateList(filteredList)
        setShowProgress(false)
    }

    fun deviceToken() {

        deviceTokenRequest.user_id = PrefMethods.getUserData()!!.id.toString()
        deviceTokenRequest.type = "android_token"

        val baeRepo = BaseRepository()
        val responseCall: Call<DeviceTokenResponse?> =
            baeRepo.apiInterface.deviceToken(deviceTokenRequest)
        responseCall.enqueue(object : Callback<DeviceTokenResponse?> {
            override fun onResponse(
                call: Call<DeviceTokenResponse?>,
                response: Response<DeviceTokenResponse?>
            ) {
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