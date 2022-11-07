package com.brandsin.user.database

import com.brandsin.user.model.IntroResponse
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
import com.brandsin.user.model.auth.verifycode.VerifyCodeRequest
import com.brandsin.user.model.auth.verifycode.VerifyCodeResponse
import com.brandsin.user.model.order.homepage.HomePageResponse
import com.brandsin.user.model.location.addaddress.AddAddressRequest
import com.brandsin.user.model.location.addaddress.AddAddressResponse
import com.brandsin.user.model.location.addresslist.ListAddressesResponse
import com.brandsin.user.model.location.addresstype.AddressTypesResponse
import com.brandsin.user.model.location.changeaddress.ChangeAddressRequest
import com.brandsin.user.model.location.changeaddress.ChangeAddressResponse
import com.brandsin.user.model.location.deleteaddress.DeleteAddressResponse
import com.brandsin.user.model.location.getdefault.GetDefaultAddressResponse
import com.brandsin.user.model.location.setdefault.SetDefaultAddressResponse
import com.brandsin.user.model.profile.changePass.ChangePassRequest
import com.brandsin.user.model.profile.changePass.ChangePassResponse
import com.brandsin.user.model.profile.updateprofile.UpdateProfileRequest
import com.brandsin.user.model.profile.updateprofile.UpdateProfileResponse
import com.brandsin.user.model.auth.setting.countryId.CountryIdResponse
import com.brandsin.user.model.follow.FollowResponse
import com.brandsin.user.model.menu.commonquest.CommonQuesResponse
import com.brandsin.user.model.menu.favourits.FavouritsResponse
import com.brandsin.user.model.menu.help.HelpQuesResponse
import com.brandsin.user.model.menu.notifications.NotificationResponse
import com.brandsin.user.model.menu.notifications.ReadNotificationRequest
import com.brandsin.user.model.menu.notifications.ReadNotificationResponse
import com.brandsin.user.model.menu.offers.OffersResponse
import com.brandsin.user.model.menu.settings.PhoneNumberResponse
import com.brandsin.user.model.menu.settings.SocialLinksResponse
import com.brandsin.user.model.order.SearchProdactAttr.SearchProductAttResponse
import com.brandsin.user.model.order.cancel.CancelOrderRequest
import com.brandsin.user.model.order.cancel.CancelOrderResponse
import com.brandsin.user.model.order.myorders.MyOrdersResponse
import com.brandsin.user.model.order.confirmorder.coupon.ApplyCouponResponse
import com.brandsin.user.model.order.confirmorder.createorder.CreateOrderRequest
import com.brandsin.user.model.order.confirmorder.createorder.CreateOrderResponse
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeRequest
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeResponse
import com.brandsin.user.model.order.details.OrderDetailsResponse
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homepage.addedstories.liststories.ListStoriesResponse
import com.brandsin.user.model.order.productdetails.ProductDetailsResponse
import com.brandsin.user.model.order.rate.RateOrderRequest
import com.brandsin.user.model.order.rate.RateOrderResponse
import com.brandsin.user.model.order.reorder.ReOrderResponse
import com.brandsin.user.model.order.storedetails.StoreDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreTimesResponse
import com.brandsin.user.model.sliders.SlidersResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface
{
    /* ---------- Auth APIs -------- */
    // login
    @POST("api/users/login")
     fun login(@Body loginRequest: LoginRequest?): Call<LoginResponse?>
    // register
    @POST("api/users/register")
    fun register(@Body registerRequest: RegisterRequest?): Call<RegisterResponse?>

    // country_id+Condition
    @GET("api/common/settings")
    fun getCountryId(@Query("code") code: String?, @Query("lang") lang: String?): Call<CountryIdResponse?>?

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
    suspend fun setDefaultAddress(@Query("user_id") userId: Int, @Query("address_id") addressId: Int, @Query("lang") lang: String): SetDefaultAddressResponse

    /* Get Default Address */
    @GET("api/common/default_address")
    suspend fun getDefaultAddress(@Query("user_id") userId: Int, @Query("lang") lang: String): GetDefaultAddressResponse

    /* Add New Address */
    @POST("api/common/add_address")
    suspend fun addNewAddress(@Body addAddressRequest: AddAddressRequest): AddAddressResponse

    /* Update Address */
    @POST("api/common/update_address")
    suspend fun updateAddress(@Body changeAddressRequest: ChangeAddressRequest): ChangeAddressResponse

    /* Delivery Addresses List */
    @GET("api/common/list_addresses")
    suspend fun getListAddresses(@Query("user_id") userId: Int, @Query("lang") lang: String, @Query("page") page: Int): ListAddressesResponse

    /* Delete address item */
    @GET("api/common/delete_address")
    suspend fun deleteDeliveryAddress(@Query("address_id") addressId: Int, @Query("lang") lang: String): DeleteAddressResponse

    /* Address TYPES */
    @GET("api/common/settings")
    suspend fun getAddressTypes(@Query("code") code: String, @Query("lang") lang: String): AddressTypesResponse

    /* Home Page */
    @GET("api/hajaty/home")
    suspend fun getHomePage(@Query("lat") lat: String, @Query("lng") lng: String,
                            @Query("distance") distance: String, @Query("locale") locale: String,
                            @Query("category_id") categoryId: String): HomePageResponse

    /* Store Details Page */
    @GET("api/hajaty/store/show")
    suspend fun getStoreDetails(@Query("store_id") storeId: Int,@Query("user_id") userId: Int?, @Query("locale") locale: String ,
                                @Query("page") page: Int , @Query("limit") limit: Int): StoreDetailsResponse

    /* Store Working hours Page */
    @GET("api/hajaty/store/working_hours")
    suspend fun getStoreWorkingPrice(@Query("store_id") storeId: Int): StoreTimesResponse

    /* Store Working hours Page */
    @GET("api/orders/coupons")
    suspend fun applyCoupon(@Query("user_id") userId: Int, @Query("lang") lang: String, @Query("code") code: String,
                            @Query("store_ids") store_ids: String, @Query("product_ids") productIds: String,
                            @Query("offers") offersIds: String): ApplyCouponResponse

    /* Store Working hours Page */
    @GET("api/orders/list_orders")
    suspend fun getUserOrders(@Query("lang") lang: String, @Query("user_id") userId: Int, @Query("limit") limit : Int): MyOrdersResponse

    /*Rate order */
    @POST("api/hajaty/order/rate")
    suspend fun rateOrder(@Body rateRequest : RateOrderRequest): RateOrderResponse

    @GET("api/orders/order_details")
    suspend fun getOrderDetails(@Query("order_id") orderId: Int, @Query("lang") lang: String): OrderDetailsResponse

    @POST("api/orders/create_order")
    suspend fun createOrder(@Body createOrderRequest : CreateOrderRequest): CreateOrderResponse

    @POST("api/common/generate_code")
    suspend fun sendCode(@Body sendCodeRequest: SendCodeRequest): SendCodeResponse

    @POST("api/common/check_code")
    suspend fun verifyCode(@Body verifyCodeRequest: com.brandsin.user.model.order.confirmorder.verifyphome.VerifyCodeRequest?): com.brandsin.user.model.order.confirmorder.verifyphome.VerifyCodeResponse

    @GET("api/hajaty/offers")
    suspend fun getOffers(@Query("limit") limit: Int, @Query("page") page: Int,
                            @Query("locale") lang: String, @Query("lat") lat: String,@Query("lng") lng: String,
                            @Query("distance") distance: String = ""): OffersResponse

    @GET("api/hajaty/notifications")
    suspend fun getNotifications(@Query("limit") limit: Int, @Query("page") page: Int,
                          @Query("user_id") user_id: Int): NotificationResponse

    @POST("api/hajaty/notifications")
    suspend fun readNotification(@Body request : ReadNotificationRequest): ReadNotificationResponse

    @GET("api/hajaty/pages")
    suspend fun getCommonQues(@Query("type") type: String, @Query("lang") lang: String): CommonQuesResponse

    @GET("api/hajaty/pages")
    suspend fun getHelpQues(@Query("type") type: String, @Query("lang") lang: String): HelpQuesResponse

    @GET("api/common/settings")
    suspend fun getPhoneNumber(@Query("code") code: String, @Query("lang") lang: String): PhoneNumberResponse

    @GET("api/common/settings")
    suspend fun getSocialLinks(@Query("code") code: String, @Query("lang") lang: String): SocialLinksResponse

    @POST("api/orders/cancel_order")
    suspend fun cancelOrder(@Body cancelRequest : CancelOrderRequest): CancelOrderResponse

    /* Home new */
    @GET("api/hajaty/home_categories")
    suspend fun getHomeNew(@Query("lat") lat: String,
                           @Query("lng") lng: String,
                           @Query("distance") distance: String,
                           @Query("locale") locale: String): HomeNewResponse

    /* Search */
    @GET("api/hajaty/home")
    suspend fun getSearch(@Query("lat") lat: String,
                          @Query("lng") lng: String,
                          @Query("distance") distance: String,
                          @Query("locale") locale: String,
                          @Query("category_id") categoryId: String,
                          @Query("keyword") keyword: String,
                          @Query("sort") sort: String,
                          @Query("tags[]") tags: ArrayList<Int>
    ): HomePageResponse

    // ListStories
    @GET("/api/hajaty/store/list_stories")
    fun getListStories(
        @Query("store_id") store_id: Int
    ): Call<ListStoriesResponse?>

    //Intro
    @GET("/api/common/introduction_app")
    fun getIntro(@Query("app") app: String ): Call<IntroResponse?>

    //ReOrder
    @FormUrlEncoded
    @POST("/api/hajaty/order/reorder")
    fun getReOrder(
        @Field("order_id") orderId: Int,
        @Field("lang") lang: String): Call<ReOrderResponse?>

    //sliders
    @GET("/api/hajaty/sliders")
    suspend fun getSlider(
        @Query("key") Key: String,
        @Query("lang") lang: String): SlidersResponse

    @FormUrlEncoded
    @POST("/api/hajaty/store/follow")
    suspend fun followStore(@Field("store_id") storeId: Int?,
                            @Field("user_id") userId: Int): FollowResponse

    @GET("/api/hajaty/store/follow")
    suspend fun getFavourits(@Query("user_id") id: Int?,
                             @Query("lang")language: String): FavouritsResponse?


    @GET("/api/hajaty/offers_stories")
    suspend fun getOffersStories(): FavouritsResponse?

    @GET("/api/hajaty/store/discover")
    suspend fun getDiscover(@Query("lat") latitude: String,
                            @Query("lng") longitude: String): FavouritsResponse?

    @GET("api/hajaty/product/show")
    suspend fun getProductDetails(@Query("id") productId: Int, @Query("locale") locale: String): ProductDetailsResponse


    @POST("/api/search_product_attr")
    suspend fun getSearch_product_attr(@Query("product_id") product_id: Int?,
                                       @Query("return_json") return_json: Int): SearchProductAttResponse


    @POST("/api/search_product_attr")
    suspend fun getSearch_product_attrSelected(@Query("product_id") product_id: Int?,
                                       @Query("return_json") return_json: Int,
                                               @Query("sku_id") sku_id: String,
                                               @Query("attr") attr: String,
                                               @Query("value") value: String): SearchProductAttResponse

}

