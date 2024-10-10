package com.brandsin.user.database

import com.brandsin.user.model.FavoriteResponse
import com.brandsin.user.model.IntroResponse
import com.brandsin.user.model.MessageResponse
import com.brandsin.user.model.auth.devicetoken.DeviceTokenRequest
import com.brandsin.user.model.auth.devicetoken.DeviceTokenResponse
import com.brandsin.user.model.auth.forgot.ForgotPassRequest
import com.brandsin.user.model.auth.forgot.ForgotPassResponse
import com.brandsin.user.model.auth.login.LoginRequest
import com.brandsin.user.model.auth.login.LoginResponse
import com.brandsin.user.model.auth.register.RegisterRequest
import com.brandsin.user.model.auth.register.RegisterResponse
import com.brandsin.user.model.auth.resendcode.ResendCodeRequest
import com.brandsin.user.model.auth.resendcode.ResendCodeResponse
import com.brandsin.user.model.auth.resetpass.ResetPassRequest
import com.brandsin.user.model.auth.resetpass.ResetPassResponse
import com.brandsin.user.model.auth.setting.countryId.CountryIdResponse
import com.brandsin.user.model.auth.verifycode.VerifyCodeRequest
import com.brandsin.user.model.auth.verifycode.VerifyCodeResponse
import com.brandsin.user.model.discover.DiscoverResponse
import com.brandsin.user.model.follow.FollowResponse
import com.brandsin.user.model.location.addaddress.AddAddressRequest
import com.brandsin.user.model.location.addaddress.AddAddressResponse
import com.brandsin.user.model.location.addresslist.ListAddressesResponse
import com.brandsin.user.model.location.addresstype.AddressTypesResponse
import com.brandsin.user.model.location.changeaddress.ChangeAddressRequest
import com.brandsin.user.model.location.changeaddress.ChangeAddressResponse
import com.brandsin.user.model.location.deleteaddress.DeleteAddressResponse
import com.brandsin.user.model.location.getdefault.GetDefaultAddressResponse
import com.brandsin.user.model.location.setdefault.SetDefaultAddressResponse
import com.brandsin.user.model.menu.commonquest.CommonQuesResponse
import com.brandsin.user.model.menu.favourits.FavoriteStoriesResponse
import com.brandsin.user.model.menu.favourits.FavouritesResponse
import com.brandsin.user.model.menu.help.HelpQuesResponse
import com.brandsin.user.model.menu.notifications.NotificationResponse
import com.brandsin.user.model.menu.notifications.ReadNotificationRequest
import com.brandsin.user.model.menu.notifications.ReadNotificationResponse
import com.brandsin.user.model.menu.offers.OfferDetailsResponse
import com.brandsin.user.model.menu.offers.OffersResponse
import com.brandsin.user.model.menu.settings.PhoneNumberResponse
import com.brandsin.user.model.menu.settings.SettingResponse
import com.brandsin.user.model.menu.settings.ShippingResponse
import com.brandsin.user.model.menu.settings.SocialLinksResponse
import com.brandsin.user.model.order.SearchProdactAttr.SearchProductAttResponse
import com.brandsin.user.model.order.cancel.CancelOrderRequest
import com.brandsin.user.model.order.cancel.CancelOrderResponse
import com.brandsin.user.model.order.confirmorder.coupon.ApplyCouponResponse
import com.brandsin.user.model.order.confirmorder.createorder.CreateOrderRequest
import com.brandsin.user.model.order.confirmorder.createorder.CreateOrderResponse
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeRequest
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeResponse
import com.brandsin.user.model.order.details.OrderDetailsResponse
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homenew.ItemsSectionResponse
import com.brandsin.user.model.order.homepage.HomePageResponse
import com.brandsin.user.model.order.homepage.addedstories.liststories.ListStoriesResponse
import com.brandsin.user.model.order.myorders.MyOrderListResponse
import com.brandsin.user.model.order.productdetails.ProductDetailsResponse
import com.brandsin.user.model.order.rate.RateOrderRequest
import com.brandsin.user.model.order.rate.RateOrderResponse
import com.brandsin.user.model.order.reorder.ReOrderResponse
import com.brandsin.user.model.order.storedetails.StoreDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreTimesResponse
import com.brandsin.user.model.profile.changePass.ChangePassRequest
import com.brandsin.user.model.profile.changePass.ChangePassResponse
import com.brandsin.user.model.profile.updateprofile.UpdateProfileRequest
import com.brandsin.user.model.profile.updateprofile.UpdateProfileResponse
import com.brandsin.user.model.refundableProduct.AddRefundableResponse
import com.brandsin.user.model.refundableProduct.ReasonReturnListResponse
import com.brandsin.user.model.refundableProduct.RefundableProductResponse
import com.brandsin.user.model.search.SearchDataResponse
import com.brandsin.user.model.sliders.SlidersResponse
import com.brandsin.user.ui.main.order.newRateOrder.model.RatingRequest
import com.brandsin.user.ui.main.order.newRateOrder.model.UploadMultiFilesResponse
import com.brandsin.user.ui.main.order.reviewStore.model.RatingsResponse
import com.brandsin.user.ui.menu.payment.UpdateCreditResponse
import com.brandsin.user.ui.menu.payment.WalletTransactionsResponse
import com.brandsin.user.ui.profile.favoriteProduct.model.FavoriteProductResponse
import com.brandsin.user.utils.storyviewer.model.StoryDetailsResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    /* ---------- Auth APIs -------- */
    // login
    @POST("api/users/login")
    fun login(@Body loginRequest: LoginRequest?): Call<LoginResponse?>

    // register
    @POST("api/users/register")
    fun register(@Body registerRequest: RegisterRequest?): Call<RegisterResponse?>

    // country_id+Condition
    @GET("api/common/settings")
    fun getCountryId(
        @Query("code") code: String?,
        @Query("lang") lang: String?
    ): Call<CountryIdResponse?>?

    // verify
    @POST("api/users/check_code")
    fun verify(@Body verifyCodeRequest: VerifyCodeRequest?): Call<VerifyCodeResponse?>

    // resendCode
    @POST("api/users/resend_code")
    fun resendCode(@Body resendCodeRequest: ResendCodeRequest?): Call<ResendCodeResponse?>

    // forgotPass
    @POST("api/users/forget_password")
    fun forgotPass(@Body forgotPassRequest: ForgotPassRequest?): Call<ForgotPassResponse?>

    // resetPass
    @POST("api/users/update_password")
    fun resetPass(@Body resetPassRequest: ResetPassRequest?): Call<ResetPassResponse?>

    // changePass
    @POST("api/users/update_user")
    fun changePass(@Body changePassRequest: ChangePassRequest?): Call<ChangePassResponse?>

    // updateProfile
    @POST("api/users/update_user")
    fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest?): Call<UpdateProfileResponse?>

    // deviceToken
    @POST("api/users/update_token")
    fun deviceToken(@Body deviceTokenRequest: DeviceTokenRequest?): Call<DeviceTokenResponse?>

    /* Set Default Address */
    @GET("api/common/set_default")
    suspend fun setDefaultAddress(
        @Query("user_id") userId: Int,
        @Query("address_id") addressId: Int,
        @Query("lang") lang: String
    ): SetDefaultAddressResponse

    /* Get Default Address */
    @GET("api/common/default_address")
    suspend fun getDefaultAddress(
        @Query("user_id") userId: Int,
        @Query("lang") lang: String
    ): GetDefaultAddressResponse

    /* Add New Address */
    @POST("api/common/add_address")
    suspend fun addNewAddress(
        @Body addAddressRequest: AddAddressRequest
    ): AddAddressResponse
    @FormUrlEncoded
    @POST("api/common/send_notification")
    suspend fun sendNotification(
        @Field("message") message:String,
        @Field("user_id") userId:Int,
        @Field("click_action_key") clickActionKey:String,
        @Field("click_action_id") clickActionId:Int,
    ): ReadNotificationResponse?

    /* Update Address */
    @POST("api/common/update_address")
    suspend fun updateAddress(@Body changeAddressRequest: ChangeAddressRequest): ChangeAddressResponse

    /* Delivery Addresses List */
    @GET("api/common/list_addresses")
    suspend fun getListAddresses(
        @Query("user_id") userId: Int,
        @Query("lang") lang: String,
        @Query("page") page: Int
    ): ListAddressesResponse

    @GET("api/common/list_addresses")
    suspend fun getAllAddresses(
        @Query("user_id") userId: Int,
        @Query("lang") lang: String,
        @Query("page") page: Int
    ): Response<ListAddressesResponse>

    /* Delete address item */
    @GET("api/common/delete_address")
    suspend fun deleteDeliveryAddress(
        @Query("address_id") addressId: Int,
        @Query("lang") lang: String
    ): DeleteAddressResponse

    /* Address TYPES */
    @GET("api/common/settings")
    suspend fun getAddressTypes(
        @Query("code") code: String,
        @Query("lang") lang: String
    ): AddressTypesResponse

    /* Home Page */
    @GET("api/hajaty/home")
    suspend fun getHomePage(
        @Query("lat") lat: String,
        @Query("lng") lng: String,
        @Query("distance") distance: String,
        @Query("locale") locale: String,
        @Query("category_id") categoryId: String,
        @Query("user_id") userId: Int,
        @Query("tags[]") tagId: Int?
    ): HomePageResponse

    /* Store Details Page */
    @GET("api/hajaty/store/show")
    suspend fun getStoreDetails(
        @Query("store_id") storeId: Int,
        @Query("user_id") userId: Int?,
        @Query("locale") locale: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): StoreDetailsResponse
    @GET("api/hajaty/store/show")
    suspend fun getStoreDetails(
        @Query("store_id") storeId: Int,
    ): StoreDetailsResponse

    /* Store Working hours Page */
    @GET("api/hajaty/store/working_hours")
    suspend fun getStoreWorkingPrice(@Query("store_id") storeId: Int): StoreTimesResponse

    /* Store Working hours Page */
    @GET("api/orders/coupons")
    suspend fun applyCoupon(
        @Query("user_id") userId: Int, @Query("lang") lang: String, @Query("code") code: String,
        @Query("store_ids") store_ids: String, @Query("product_ids") productIds: String,
        @Query("offers") offersIds: String
    ): ApplyCouponResponse

    /* Store Working hours Page */
    @GET("api/orders/list_orders")
    suspend fun getUserOrders(
        @Query("lang") lang: String,
        @Query("user_id") userId: Int,
        @Query("limit") limit: Int
    ): MyOrderListResponse

    /*Rate order */
    @POST("api/hajaty/order/rate")
    suspend fun rateOrder(@Body rateRequest: RateOrderRequest): RateOrderResponse

    @GET("api/orders/order_details")
    suspend fun getOrderDetails(
        @Query("order_id") orderId: Int,
        @Query("lang") lang: String
    ): OrderDetailsResponse

    @POST("api/orders/create_order")
    suspend fun createOrder(
        @Body createOrderRequest: CreateOrderRequest
    ): CreateOrderResponse

    @POST("api/common/generate_code")
    suspend fun sendCode(@Body sendCodeRequest: SendCodeRequest): SendCodeResponse

    @POST("api/common/check_code")
    suspend fun verifyCode(
        @Body verifyCodeRequest: com.brandsin.user.model.order.confirmorder.verifyphome.VerifyCodeRequest?
    ): com.brandsin.user.model.order.confirmorder.verifyphome.VerifyCodeResponse

    @GET("api/hajaty/offers")
    suspend fun getOffers(
        @Query("limit") limit: Int, @Query("page") page: Int,
        @Query("locale") lang: String, @Query("lat") lat: String, @Query("lng") lng: String,
        @Query("distance") distance: String = ""
    ): OffersResponse

    @GET("api/hajaty/offers/show")
    suspend fun getOfferById(
        @Query("id") offersId: Int,
        @Query("locale") lang: String,
    ): OfferDetailsResponse

    @GET("api/hajaty/notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("user_id") userId: Int
    ): NotificationResponse

    @POST("api/hajaty/notifications")
    suspend fun readNotification(@Body request: ReadNotificationRequest): ReadNotificationResponse

    @GET("api/hajaty/pages")
    suspend fun getCommonQues(
        @Query("type") type: String,
        @Query("lang") lang: String
    ): CommonQuesResponse

    @GET("api/hajaty/pages")
    suspend fun getHelpQues(
        @Query("type") type: String,
        @Query("lang") lang: String
    ): HelpQuesResponse

    @GET("api/common/settings")
    suspend fun getPhoneNumber(
        @Query("code") code: String,
        @Query("lang") lang: String
    ): PhoneNumberResponse

    @GET("api/common/settings")
    suspend fun getSocialLinks(
        @Query("code") code: String,
        @Query("lang") lang: String
    ): SocialLinksResponse

    @GET("api/common/settings")
    suspend fun getShipping(
        @Query("code[]") homeDelivery: String, // Home_Delivery
        @Query("code[]") smartSafe: String, // Smart_Safe
        @Query("code[]") selfPickup: String, // Self_Pickup
        @Query("lang") lang: String
    ): Response<ShippingResponse>

    @GET("api/common/settings")
    suspend fun getCodFeesCash(
        @Query("code") codFees: String, // Home_Delivery
        @Query("lang") lang: String
    ): Response<SettingResponse>

    @GET("api/common/settings")
    suspend fun getPackagingPrice(
        @Query("code") packagingPriceCode: String, // Home_Delivery
        @Query("lang") lang: String
    ): Response<SettingResponse>

    @POST("api/orders/cancel_order")
    suspend fun cancelOrder(@Body cancelRequest: CancelOrderRequest): CancelOrderResponse

    /* Home new */
    @GET("api/hajaty/home_categories")
    suspend fun getHomeNew(
        @Query("lat") lat: String,
        @Query("lng") lng: String,
        @Query("distance") distance: String,
        @Query("locale") locale: String,
        @Query("user_id") userId: Int,
    ): HomeNewResponse

    /* Search */
    @GET("api/hajaty/home")
    suspend fun getSearch(
        @Query("lat") lat: String,
        @Query("lng") lng: String,
        @Query("distance") distance: String,
        @Query("locale") locale: String,
        @Query("category_id") categoryId: String,
        @Query("keyword") keyword: String,
        @Query("sort") sort: String,
        @Query("tags[]") tags: ArrayList<Int>
    ): HomePageResponse

    @GET("api/hajaty/search") // ?search_for=store&keyword=ALMA
    suspend fun searchProductOrStore(
        @Query("search_for") searchFor: String, // product or store
        @Query("keyword") keyword: String,
        @Query("brand_id") brandId: Int?,
    ): SearchDataResponse

    // ListStories
    @GET("/api/hajaty/store/list_stories")
    fun getListStories(
        @Query("store_id") storeId: Int
    ): Call<ListStoriesResponse?>

    //Intro
    @GET("/api/common/introduction_app")
    fun getIntro(@Query("app") app: String): Call<IntroResponse?>

    //ReOrder
    @FormUrlEncoded
    @POST("/api/hajaty/order/reorder")
    fun getReOrder(
        @Field("order_id") orderId: Int,
        @Field("lang") lang: String
    ): Call<ReOrderResponse?>

    //sliders
    @GET("/api/hajaty/sliders")
    suspend fun getSlider(
        @Query("key") key: String,
        @Query("lang") lang: String
    ): SlidersResponse

    @FormUrlEncoded
    @POST("/api/hajaty/store/follow")
    suspend fun followStore(
        @Field("store_id") storeId: Int?,
        @Field("user_id") userId: Int,
        @Query("lang") language: String,
    ): FollowResponse

    @POST("/api/hajaty/store/follow")
    suspend fun newFollowStore(
        @Query("store_id") storeId: Int?,
        @Query("user_id") userId: Int,
        @Query("lang") language: String,
    ): Response<FollowResponse>

    // old end point -> /api/hajaty/store/follow
    @GET("/api/hajaty/stories/list_followed") // {{base}}/api/hajaty/stories/list_followed?user_id=817&is_seen=1
    suspend fun getFavourites(
        @Query("user_id") id: Int?,
        @Query("is_seen") isSeen: Int
    ): Response<FavoriteStoriesResponse>

    @GET("/api/hajaty/offers_stories")
    suspend fun getOffersStories(): FavouritesResponse?

    @GET("/api/hajaty/store/discover")
    suspend fun getDiscover(
        @Query("lat") latitude: String,
        @Query("lng") longitude: String
    ): FavouritesResponse?

    @GET("api/hajaty/stories/list") // {{base}}/api/hajaty/stories/list?user_id=1&lat=46.6858242&lng=24.6809393&is_seen=1
    suspend fun getStoriesListInDiscover(
        @Query("user_id") userId: Int,
        @Query("lat") latitude: String,
        @Query("lng") longitude: String,
        @Query("is_seen") isSeen: Int,
    ): Response<DiscoverResponse>

    @GET("api/hajaty/product/show")
    suspend fun getProductDetails(
        @Query("id") productId: Int,
        @Query("user_id") user_id: Int,
        @Query("locale") locale: String
    ): ProductDetailsResponse

    @POST("/api/search_product_attr")
    suspend fun getSearch_product_attr(
        @Query("product_id") productId: Int?,
        @Query("return_json") returnJson: Int
    ): SearchProductAttResponse

    @POST("/api/search_product_attr")
    suspend fun getSearch_product_attrSelected(
        @Query("product_id") productId: Int?,
        @Query("return_json") returnJson: Int,
        @Query("sku_id") skuId: String,
        @Query("attr") attr: String,
        @Query("value") value: String
    ): SearchProductAttResponse

    @POST("/api/favourites/add")
    suspend fun addAndRemoveFavorite(
        @Query("user_id") userId: Int,
        @Query("item_type") itemType: String,
        @Query("item_id") itemId: String, // productId
        @Query("lang") lang: String
    ): Response<FavoriteResponse>

    @GET("api/favourites/list")
    suspend fun getAllFavoriteProduct(
        @Query("user_id") userId: Int,
        @Query("item_type") itemType: String,
    ): Response<FavoriteProductResponse>

    @GET("api/hajaty/section_items")
    suspend fun getItemsOfSectionId(
        @Query("section_id") sectionId: Int,
    ): Response<ItemsSectionResponse>

    @GET("api/rate/list")
    suspend fun getRatingsStoreList(
        @Query("item_type") itemType: String, // store, driver, product => Required
        @Query("user_id") userId: Int,
        @Query("item_id") itemId: Int?, // storeId or product id
        @Query("has_media") hasMedia: Int?, // has_media = 0 -> get normal ratings store -- has_media = 1 get images ratings store
    ): Response<RatingsResponse>

    @GET("api/hajaty/store/show_story")
    suspend fun getStoryDetailsById(
        @Query("id") storyId: Int,
    ): Response<StoryDetailsResponse>

    @POST("api/hajaty/stories/update_views") // hajaty/stories/update_views
    suspend fun updateViewStory(
        @Query("id") storyId: Int,
        @Query("user_id") userId: Int,
    ): Response<MessageResponse>

    @POST("api/rate")
    suspend fun addRateStoreOrProductOrDriver(
        @Body requestBody: RatingRequest
    ): Response<MessageResponse>

    @Multipart
    @POST("/api/common/upload")
    suspend fun uploadMultiFiles(
        @Part filePart: MultipartBody.Part?
    ): Response<UploadMultiFilesResponse>

    @GET("api/refundable/list")
    suspend fun getAllRefundableProducts(
        @Query("user_id") userId: Int,
    ): Response<RefundableProductResponse>

    @GET("api/common/reasons_return")
    suspend fun getAllReasonsReturnList(
        @Query("lang") lang: String,
    ): Response<ReasonReturnListResponse>

    @Multipart
    @POST("api/refundable/add")
    suspend fun addRefundableProduct(
        @Query("user_id") userId: Int,
        @Query("product_id") productId: Int,
        @Query("reason_id") reasonId: Int,
        @Query("order_item_id") orderItemId: Int,
        @Query("note") note: String?,
        @Query("lang") lang: String,
        @Part image: MultipartBody.Part? // @Query("image") image: String?, //
    ): Response<AddRefundableResponse>

    @GET("api/users/wallet_transactions")
    suspend fun getWalletTransactions(
        @Query("user_id") userId: Int,
    ): Response<WalletTransactionsResponse>

    @FormUrlEncoded
    @POST("api/users/transfer_points")
    suspend fun transferPoints(
        @Field("user_id") userId: Int,
        @Field("phone_number") phoneNumber: String,
        @Field("points") points: Int,
    ): Response<MessageResponse>

    @FormUrlEncoded
    @POST("api/users/update_credit")
    suspend fun updateCreditIncrease(
        @Field("user_id") userId: Int,
        @Field("increase") increase: String,
        // @Field("points") points: Int,
    ): Response<UpdateCreditResponse>

    @FormUrlEncoded
    @POST("api/users/update_credit")
    suspend fun updateCreditDecrease(
        @Field("user_id") userId: Int,
        @Field("decrease") decrease: Double,
        // @Field("points") points: Int,
    ): Response<UpdateCreditResponse>

//    suspend fun submitReview(@Body request: ReviewRequest): Response<YourResponseClass>
}

/*
{
    "items": [
        {
            "item_type": "product",
            "item_id": 1758,
            "user_id": 1,
            "rate": 4,
            "title": "Good product",
            "body": "A detailed review for the product.",
            "image": "http://brandsin.local/media/temp/nAXs4DS9XH7o0UurqroCtS6i4I7gy50SYiSmUS9I.png",
            "video": "http://brandsin.local/media/temp/uZOygib7LhW1UFkn6uCGg2VB7r3PczVqq9g5ugAr.mp4"
        },
        {
            "item_type": "store",
            "item_id": 56,
            "user_id": 1,
            "rate": 5,
            "title": "Excellent store",
            "body": "A positive review for the store.",
            "image": "http://brandsin.local/media/temp/nAXs4DS9XH7o0UurqroCtS6i4I7gy50SYiSmUS9I.png",
            "video": "http://brandsin.local/media/temp/uZOygib7LhW1UFkn6uCGg2VB7r3PczVqq9g5ugAr.mp4"
        },
        {
            "item_type": "driver",
            "item_id": 1,
            "user_id": 1,
            "rate": 3,
            "title": "Average driver",
            "body": "A neutral review for the driver.",
            "image": "http://brandsin.local/media/temp/nAXs4DS9XH7o0UurqroCtS6i4I7gy50SYiSmUS9I.png",
            "video": "http://brandsin.local/media/temp/uZOygib7LhW1UFkn6uCGg2VB7r3PczVqq9g5ugAr.mp4"
        }
    ]
}
 */
