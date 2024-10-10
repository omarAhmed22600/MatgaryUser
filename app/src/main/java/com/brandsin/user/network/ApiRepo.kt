package com.brandsin.user.network

import com.brandsin.user.database.ApiInterface
import com.brandsin.user.model.location.addaddress.AddAddressRequest
import com.brandsin.user.model.location.changeaddress.ChangeAddressRequest
import com.brandsin.user.model.menu.favourits.FavouritesResponse
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

    suspend fun setDefaultAddress(userId: Int, addressId: Int, lang: String) =
        apiInterface.setDefaultAddress(userId, addressId, lang)

    suspend fun getDefaultAddress(userId: Int, lang: String) =
        apiInterface.getDefaultAddress(userId, lang)

    suspend fun addNewAddress(addAddressRequest: AddAddressRequest) =
        apiInterface.addNewAddress(addAddressRequest)
    suspend fun sendNotification(message: String, userId: Int, currentUserId: Int) =
        apiInterface.sendNotification(message,userId,"chat_id",currentUserId)
    suspend fun changeAddress(changeAddressRequest: ChangeAddressRequest) =
        apiInterface.updateAddress(changeAddressRequest)

    suspend fun getListAddresses(userId: Int, lang: String, page: Int) =
        apiInterface.getListAddresses(userId, lang, page)

    suspend fun getAllAddresses(userId: Int, lang: String, page: Int) =
        apiInterface.getAllAddresses(userId, lang, page)

    suspend fun deleteDeliveryAddress(address_id: Int, lang: String) =
        apiInterface.deleteDeliveryAddress(address_id, lang)

    suspend fun getAddressTypes() =
        apiInterface.getAddressTypes("address_types", PrefMethods.getLanguage())

    suspend fun getHomePage(
        lat: String,
        lng: String,
        distance: String,
        locale: String,
        categoryId: String,
        tagId: Int?,
        userId: Int
    ) = apiInterface.getHomePage(lat, lng, distance, locale, categoryId, userId, tagId)

    suspend fun getSlider(key: String, lng: String) = apiInterface.getSlider(key, lng)

    suspend fun getStoreDetails(storeId: Int, userId: Int?, locale: String, page: Int, limit: Int) =
        apiInterface.getStoreDetails(storeId, userId, locale, page, limit)
    suspend fun getStoreDetails(storeId: Int) =
        apiInterface.getStoreDetails(storeId)
    suspend fun getStoreWorkingHours(storeId: Int) = apiInterface.getStoreWorkingPrice(storeId)

    suspend fun getShipping(
        homeDelivery: String, smartSafe: String,
        selfPickup: String, lang: String,
    ) = apiInterface.getShipping(homeDelivery, smartSafe, selfPickup, lang)

    suspend fun applyCoupon(
        userId: Int,
        lang: String,
        code: String,
        storeIds: String,
        productsIds: String,
        offersIds: String
    ) =
        apiInterface.applyCoupon(userId, lang, code, storeIds, productsIds, offersIds)

    suspend fun getMyOrders(lang: String, userId: Int, limit: Int) =
        apiInterface.getUserOrders(lang, userId, 50)

    suspend fun rateOrder(rateOrderRequest: RateOrderRequest) =
        apiInterface.rateOrder(rateOrderRequest)

    suspend fun getOrderDetails(orderId: Int, lang: String) =
        apiInterface.getOrderDetails(orderId, lang)

    suspend fun createOrder(createOrderRequest: CreateOrderRequest) =
        apiInterface.createOrder(createOrderRequest)

    suspend fun sendCode(sendCodeRequest: SendCodeRequest) = apiInterface.sendCode(sendCodeRequest)

    suspend fun verifyCode(verifyCodeRequest: VerifyCodeRequest) =
        apiInterface.verifyCode(verifyCodeRequest)

    suspend fun getOffers(
        limit: Int,
        page: Int,
        locale: String,
        lat: String,
        lng: String,
        distance: String
    ) =
        apiInterface.getOffers(limit, page, locale, lat, lng, distance)

    suspend fun getOfferById(
        offersId: Int,
        locale: String,
    ) =
        apiInterface.getOfferById(offersId, locale)

    suspend fun getNotifications(limit: Int, page: Int, userId: Int) =
        apiInterface.getNotifications(limit, page, userId)

    suspend fun readNotification(request: ReadNotificationRequest) =
        apiInterface.readNotification(request)

    suspend fun getCommonQues(type: String, lang: String) = apiInterface.getCommonQues(type, lang)

    suspend fun getHelpQues(type: String, lang: String) = apiInterface.getHelpQues(type, lang)

    suspend fun getPhoneNumber(type: String, lang: String) = apiInterface.getPhoneNumber(type, lang)

    suspend fun getSocialLinks(type: String, lang: String) = apiInterface.getSocialLinks(type, lang)

    suspend fun cancelOrder(cancelRequest: CancelOrderRequest) =
        apiInterface.cancelOrder(cancelRequest)

    suspend fun getHomeNew(
        lat: String,
        lng: String,
        distance: String,
        locale: String,
        userId: Int
    ) =
        apiInterface.getHomeNew(lat, lng, distance, locale, userId)

    suspend fun getSearch(
        lat: String,
        lng: String,
        distance: String,
        locale: String,
        categoryId: String,
        keyword: String,
        sort: String,
        tags: ArrayList<Int>
    ) =
        apiInterface.getSearch(lat, lng, distance, locale, categoryId, keyword, sort, tags)

    suspend fun searchProductOrStore(
        searchFor: String,
        keyword: String,
        brandId: Int?
    ) =
        apiInterface.searchProductOrStore(searchFor, keyword, brandId)

    suspend fun followStore(storeId: Int?, userId: Int, language: String) =
        apiInterface.followStore(storeId, userId, language)

    /*suspend fun getFavourites(id: Int?, isSeen: Int): FavoriteStoriesResponse? =
        apiInterface.getFavourites(id, isSeen)*/

    suspend fun getOffersStories(): FavouritesResponse? = apiInterface.getOffersStories()


    suspend fun getDiscover(latitude: String, longitude: String) =
        apiInterface.getDiscover(latitude, longitude)

    suspend fun getProductDetails(productId: Int, userId: Int, locale: String) =
        apiInterface.getProductDetails(productId, userId, locale)

    suspend fun getSearch_product_attr(product_id: Int?, return_json: Int) =
        apiInterface.getSearch_product_attr(product_id, return_json)


    suspend fun getSearch_product_attrSelected(
        product_id: Int?, return_json: Int, sku_id: String, attr: String,
        value: String
    ) = apiInterface.getSearch_product_attrSelected(product_id, return_json, sku_id, attr, value)

}