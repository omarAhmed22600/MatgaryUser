package com.brandsin.user.network

import com.brandsin.user.database.ApiInterface
import com.brandsin.user.model.location.addaddress.AddAddressRequest
import com.brandsin.user.model.location.changeaddress.ChangeAddressRequest
import com.brandsin.user.model.menu.favourits.FavouritsResponse
import com.brandsin.user.model.menu.notifications.ReadNotificationRequest
import com.brandsin.user.model.order.cancel.CancelOrderRequest
import com.brandsin.user.model.order.confirmorder.createorder.CreateOrderRequest
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeRequest
import com.brandsin.user.model.order.confirmorder.verifyphome.VerifyCodeRequest
import com.brandsin.user.model.order.rate.RateOrderRequest
import com.brandsin.user.utils.PrefMethods

/**
 * Created by MouazSalah on 28/12/2020.
 **/

class ApiRepo(private val apiInterface: ApiInterface) {

    suspend fun setDefaultAddress(userId: Int, addressId : Int, lang: String) = apiInterface.setDefaultAddress(userId, addressId, lang)

    suspend fun getDefaultAddress(userId: Int, lang: String) = apiInterface.getDefaultAddress(userId, lang)

    suspend fun addNewAddress(addAddressRequest : AddAddressRequest) = apiInterface.addNewAddress(addAddressRequest)

    suspend fun changeAddress(changeAddressRequest: ChangeAddressRequest) = apiInterface.updateAddress(changeAddressRequest)

    suspend fun getListAddresses(userId: Int, lang: String, page: Int) = apiInterface.getListAddresses(userId, lang, page)

    suspend fun deleteDeliveryAddress(address_id: Int, lang: String) = apiInterface.deleteDeliveryAddress(address_id, lang)

    suspend fun getAddressTypes() = apiInterface.getAddressTypes("address_types", PrefMethods.getLanguage())

    suspend fun getHomePage(lat : String , lng : String, distance : String , locale : String , categoryId : String) = apiInterface.getHomePage(lat, lng, distance, locale , categoryId)

    suspend fun getSlider(key : String , lng : String) = apiInterface.getSlider(key, lng)

    suspend fun getStoreDetails(storeId : Int ,userId: Int?, locale : String, page : Int , limit : Int) = apiInterface.getStoreDetails(storeId , userId,locale , page, limit)

    suspend fun getStoreWorkingHours(storeId : Int) = apiInterface.getStoreWorkingPrice(storeId)

    suspend fun applyCoupon(userId: Int, lang : String , code : String , storeIds : String, productsIds : String, offersIds : String) =
                            apiInterface.applyCoupon(userId , lang, code , storeIds , productsIds, offersIds)

    suspend fun getMyOrders(lang : String , userId : Int , limit : Int) = apiInterface.getUserOrders(lang, userId , 50)

    suspend fun rateOrder(rateOrderRequest: RateOrderRequest) = apiInterface.rateOrder(rateOrderRequest)

    suspend fun getOrderDetails(orderId: Int, lang : String) = apiInterface.getOrderDetails(orderId , lang)

    suspend fun createOrder(createOrderRequest: CreateOrderRequest) = apiInterface.createOrder(createOrderRequest)

    suspend fun sendCode(sendCodeRequest: SendCodeRequest) = apiInterface.sendCode(sendCodeRequest)

    suspend fun verifyCode(verifyCodeRequest: VerifyCodeRequest) = apiInterface.verifyCode(verifyCodeRequest)

    suspend fun getOffers(limit : Int, page : Int, locale : String , lat : String, lng : String , distance: String ) =
                    apiInterface.getOffers(limit, page, locale, lat, lng, distance)

    suspend fun getNotifications(limit : Int, page : Int, userId : Int) = apiInterface.getNotifications(limit, page, userId)

    suspend fun readNotification(request : ReadNotificationRequest) = apiInterface.readNotification(request)

    suspend fun getCommonQues(type : String, lang : String) = apiInterface.getCommonQues(type, lang)

    suspend fun getHelpQues(type : String, lang : String) = apiInterface.getHelpQues(type, lang)

    suspend fun getPhoneNumber(type : String, lang : String) = apiInterface.getPhoneNumber(type, lang)

    suspend fun getSocialLinks(type : String, lang : String) = apiInterface.getSocialLinks(type , lang)

    suspend fun cancelOrder(cancelRequest : CancelOrderRequest) = apiInterface.cancelOrder(cancelRequest)

    suspend fun getHomeNew(lat : String , lng : String, distance : String , locale : String ) = apiInterface.getHomeNew(lat, lng, distance, locale )

    suspend fun getSearch(lat : String ,
                          lng : String ,
                          distance : String ,
                          locale : String ,
                          categoryId : String ,
                          keyword : String ,
                          sort : String ,
                          tags : ArrayList<Int>) =
        apiInterface.getSearch(lat, lng, distance,locale, categoryId, keyword, sort, tags)

    suspend fun followStore(storeId: Int?, userId: Int)=
        apiInterface.followStore(storeId, userId)

    suspend  fun getFavourits(id: Int?, language: String): FavouritsResponse? =apiInterface.getFavourits(id,language)
    suspend  fun getOffersStories(): FavouritsResponse? =apiInterface.getOffersStories()


    suspend fun getDiscover(latitude: String, longitude: String)=apiInterface.getDiscover(latitude,longitude)

}