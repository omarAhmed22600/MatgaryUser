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
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartStoreData
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homenew.PopupsItem
import com.brandsin.user.model.order.homenew.SectionsItem
import com.brandsin.user.model.order.homepage.HomePageResponse
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.productdetails.ProductDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.model.order.storedetails.StoreDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.model.sliders.SlidersResponse
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.main.homenew.ads.AdsAdapter
import com.brandsin.user.ui.main.homenew.category.CategoriesAdapter
import com.brandsin.user.ui.main.homenew.more.MoreAdapter
import com.brandsin.user.ui.main.homenew.moreslider.MoreSliderAdapter
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeNewViewModel : BaseViewModel() {

    var homePageResponse = HomePageResponse()
    var homeNewResponse = HomeNewResponse()

    var homeNewResponse2 = SingleLiveEvent<HomeNewResponse>()
    var deviceTokenRequest = DeviceTokenRequest()

    var categoriesAdapter = CategoriesAdapter()
    lateinit var storiesAdapter: StoriesAdapter
    var adsAdapter = AdsAdapter()
    var moreAdapter = MoreAdapter()

    var obsCartCount = ObservableField<Int>()
    var obsAddress = ObservableField<String>()
    var latitude: String = Const.latitude.toString()
    var longitude: String = Const.longitude.toString()

    var categoriesList = mutableListOf<CategoriesItem>()
    private var moreList = mutableListOf<SectionsItem>()

    private var popupList = mutableListOf<PopupsItem>()
    var storiesList = mutableListOf<ArrayList<StoriesItem>>()

    var slidersResponse = SingleLiveEvent<SlidersResponse>()
    var moreSliderAdapter = MoreSliderAdapter(){}

    var cartStore: CartStoreData? = null

    fun onCartClicked() {
        setValue(Codes.CART_CLICKED)
    }

    fun onSearchClicked() {
        setValue(Codes.SEARCH_CLICKED)
    }

    fun setStoriesListener(onStoryClickedListner: StoriesAdapter.OnStoryClickedListener) {
        storiesAdapter = StoriesAdapter(onStoryClickedListner)
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

    suspend fun getProductStatus(id: Int): String {
        var result = ""
        withContext(Dispatchers.IO) {
            val response = getApiRepo().getProductDetails(
                id,
                PrefMethods.getUserData()!!.id!!,
                PrefMethods.getLanguage()
            )

            response?.let { res ->
                if (res.success == true) {
                    result = res.data?.type.orEmpty()
                }
            }
        }
        return result
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
                    PrefMethods.saveDefaultAddress(res.defaultAddressItem!![0])
                    apiResponseLiveData.value = ApiResponse.success(res.defaultAddressItem[0])
                }

                else -> {}
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
                        if (it!!.slides.isNotEmpty()) {
                            /*slider*/
                            moreSliderAdapter.updateList(it.slides)
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
                return@withContext getApiRepo().getHomeNew(
                    latitude,
                    longitude,
                    "5",
                    PrefMethods.getLanguage(),
                    PrefMethods.getUserData()?.id ?: 0
                )
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
                    obsIsHide.set(true)

                    if (res.popups!!.isNotEmpty()) {
                        /* popups List */
                        popupList = res.popups as MutableList<PopupsItem>
                        // setValue(Codes.SHOW_POPUP) // hide dialog pop up in home
                    }

                    if (res.categories!!.isNotEmpty()) {
                        /* Categories List */
                        categoriesList = res.categories as MutableList<CategoriesItem>
                        categoriesAdapter.updateList(categoriesList)
                    }

                    /* Ads List */
//                    adsAdapter.updateList()

                    if (res.sections!!.isNotEmpty()) {
                        /* More List */
                        moreList = res.sections as MutableList<SectionsItem>
                        moreAdapter.updateList(moreList)
                    }

                    // Stories list
                    if (res.stories!!.isNotEmpty()) {
                        /* More List */
                        storiesList = res.stories as MutableList<ArrayList<StoriesItem>>
                        storiesAdapter.updateList(storiesList)
                    } else {
                        ////// storess///
                        obsIsHide.set(false)
                    }

                    Log.d("storiesList", "getHomePage: " + storiesList.size)
                }

                else -> {
                    obsIsLoading.set(false)
                    obsIsEmpty.set(true)
                    obsIsFull.set(false)
                }
            }
        }
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

    // var storeData = StoreDetailsData()
    var cartItemsList: ArrayList<CartItem>? = null
    var userCart: UserCart? = null

    fun addProductToCart(item: CartItem, storeProductItem: StoreProductItem) {
        obsIsVisible.set(true)
        /*
        cartStore?.storeId 791
storeProductItem?.storeId 51
storeProductItem StoreProductItem(store=StoreDetailsData(hasDelivery=null, hasTodayStories=false, isFollowed=false, isClosed=null, isBusy=null, minOrderPrice=null, minPriceProduct=180.0, thumbnail=https://dev.brandsin.net/media/user_v1oz1Yz27j/4308/conversions/133_350x350_0.jpg, followersCount=3, storeCategoriesList=null, address=null, avgRating=0.0, workingHours12=null, paymentMethod=null, name=NOOR, id=51, deliveryTime=24, deliveryType=hour, pickUpFromStore=1, cashOnDelivery=1, deliveryPrice=20, covers=[CoversItem(id=4309, url=https://dev.brandsin.net/media/user_v1oz1Yz27j/4309/20.png, content=null), CoversItem(id=4310, url=https://dev.brandsin.net/media/user_v1oz1Yz27j/4310/21.png, content=null)], storeProductList=null, stories=null), nameEn=Grandel, descriptionEn=<p>Color Black</p>, images=[https://dev.brandsin.net/media/user_v1oz1Yz27j/4339/jXoFid1662363184.png], imagesIds=[ImagesIdsItem(id=4339, url=https://dev.brandsin.net/media/user_v1oz1Yz27j/4339/jXoFid1662363184.png)], code=null, flag=0.0, skus=[StoreSkusItem(allowedQuantity=10, name=null, code=1865a2d4c7a6965261e78ba1cb4c0f76ba27b0a7, freeShipping=0, inventory=infinite, salePrice=350.00, freeRefunding=0, regularPrice=350.00, inventoryValue=null, shipping=null, price=350.00, productId=791, id=1523, secured=0, status=active, isSelected=false, key=null, media=[], unitId=null, values=null)], addons=[], description=اللون اسود, productStatus=null, caption=اللون اسود, todayOffers=0.0, createdAt=2022-09-05 11:33:04, discount=0.0, video=null, media=[MediaItem(manipulations=[], orderColumn=2918, fileName=jXoFid1662363184.png, modelType=Corals\Modules\Marketplace\Models\Product, createdAt=2022-09-05 11:33:04, modelId=791, customProperties=CustomProperties(featured=true, root=user_v1oz1Yz27j), disk=media, size=645509, updatedAt=2022-09-05 11:33:04, mimeType=image/png, name=jXoFid1662363184, id=4339, collectionName=marketplace-product-gallery)], type=simple, metaKeywords=null, relatedProducts=null, externalUrl=null, videoUrl=null, shipping=Shipping(width=null, length=null, weight=null, enabled=0, height=null), updatedAt=2022-09-20 22:55:27, adminStatus=approved, id=791, productCategoriesList=[CategoriesItem(cover=null, image=https://dev.brandsin.net/assets/corals/images/default_product_image.png, imageEn=null, mobileImage=null, thumbnail=https://dev.brandsin.net/assets/corals/images/default_product_image.png, bannerMobileImage=null, sidebar=null, categoryName=رسمية, categoryId=174, itemOrder=null, status=active, tags=null)], slugAr=gr-ndyl, slug=gr-ndyl, deepLink=null, views=0, whatsappNumber=null, storeId=51, image=https://dev.brandsin.net/media/user_v1oz1Yz27j/4339/conversions/jXoFid1662363184_300x530_0.png, address=null, metaTitle=null, deepLinkAr=null, createdBy=1, deletedAt=null, brandId=null, isUsed=0, metaDescription=null, userId=null, name=جرانديل, updatedBy=1, files=null, saleByPhone=null, countryId=null, ratings=[RatingItem(reviewRateableType=Corals\Modules\Marketplace\Models\Product, image=null, reviewRateableId=791, author=null, criteria=null, rating=5, hasMedia=false, createdAt=2023-03-07 02:38:16, video=null, title=Thank you, body=Thank you, authorType=Corals\User\Models\User, createdBy=0, videoImage=null, updatedAt=2023-03-07 02:38:16, updatedBy=0, id=22, authorId=798, status=approved)], isFeatured=false, isFavorite=false, status=active, cityId=null, isShareable=1.0, isRefund=0.0, refundDays=0.0, skuId=null, isRefundable=false, avgRating=5.0, isSelected=false)

         */

        when {
            // Cart is empty
            cartItemsList!!.isEmpty() -> {
                cartItemsList!!.add(item)
                cartStore?.run {
                    storeId = storeProductItem.id
                    storeName = storeProductItem.name
                    extraFees = storeProductItem.store?.deliveryPrice?.toDouble() ?: 0.0
                    deliveryTime = storeProductItem.store?.deliveryTime?.toString() ?: ""
                    deliveryType = storeProductItem.store?.deliveryType ?: ""
                    deliveryPrice = storeProductItem.store?.deliveryPrice ?: 0
                    minimumOrder = storeProductItem.store?.minOrderPrice?.toDouble() ?: 0.0
                }

                userCart?.run {
                    cartItems = cartItemsList
                    cartStoreData = cartStore
                }
                PrefMethods.saveUserCart(userCart)
            }

            else -> {
                var i = 0
                while (i < cartItemsList!!.size) {
                    when {
                        // This item is exist in cart >> update quantity
                        cartItemsList!![i].skuCode == item.skuCode && cartItemsList!![i].addonsIds == item.addonsIds -> {
                            cartItemsList!![i].productQuantity =
                                cartItemsList!![i].productQuantity + item.productQuantity // update quantity
                            cartItemsList!![i].productTotalPrice =
                                cartItemsList!![i].productUnitPrice * cartItemsList!![i].productQuantity // update price
                            val sharedCart = PrefMethods.getUserCart()
                            sharedCart?.cartItems?.clear()
                            sharedCart?.cartItems?.addAll(cartItemsList!!)
                            PrefMethods.saveUserCart(sharedCart)
                            return
                        }

                        i == cartItemsList!!.size - 1 -> {
                            // This item not exist in cart
                            cartItemsList!!.add(item)
                            cartStore?.run {
                                storeId = storeProductItem.id
                                storeName = storeProductItem.name
                                extraFees = storeProductItem.store?.deliveryPrice?.toDouble() ?: 0.0
                                deliveryTime = storeProductItem.store?.deliveryTime?.toString() ?: ""
                                deliveryType = storeProductItem.store?.deliveryType ?: ""
                                minimumOrder = storeProductItem.store?.minOrderPrice?.toDouble() ?: 0.0
                            }

                            userCart?.run {
                                cartItems = cartItemsList
                                cartStoreData = cartStore
                            }
                            PrefMethods.saveUserCart(userCart)
                            return
                        }

                        else -> i++
                    }
                }
            }
        }
    }

    fun getStoreDetails(storeId: Int) {
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<StoreDetailsResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getStoreDetails(
                    storeId,
                    PrefMethods.getUserData()?.id,
                    PrefMethods.getLanguage(),
                    0,
                    50
                )
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    obsIsLoading.set(false)
                    obsIsFull.set(true)

                    // storeData = res.storeDetailsData!!
                }

                else -> {}
            }
        }
    }
}