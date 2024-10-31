package com.brandsin.user.model.search

import android.os.Parcelable
import com.brandsin.user.model.order.reorder.CommercialRegister
import com.brandsin.user.model.order.storedetails.CoversItem
import com.brandsin.user.model.order.storedetails.StoreSkusItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SearchDataResponse(

    @field:SerializedName("data")
    val data: List<SearchStoresOrProducts?>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
) : Parcelable

@Parcelize
data class SearchStoresOrProducts(

    @field:SerializedName("account_type")
    val accountType: @RawValue Any? = null,

    @field:SerializedName("registration_file")
    val registrationFile: @RawValue Any? = null,

    @field:SerializedName("delivery_time")
    val deliveryTime: Int? = null,

    @field:SerializedName("land_mark")
    val landMark: @RawValue Any? = null,

    @field:SerializedName("causer_type")
    val causerType: @RawValue Any? = null,

    @field:SerializedName("approved")
    val approved: Int? = null,

    @field:SerializedName("delivery_type")
    val deliveryType: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("slug")
    val slug: String? = null,

    @field:SerializedName("lat")
    val lat: String? = null,

    @field:SerializedName("payment_method")
    val paymentMethod: @RawValue Any? = null,

    @field:SerializedName("is_closed")
    val isClosed: Int? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("images")
    val images: @RawValue List<Any?>? = null,

    @field:SerializedName("lng")
    val lng: String? = null,

    @field:SerializedName("min_order_price")
    val minOrderPrice: String? = null,

    @field:SerializedName("is_followed")
    val isFollowed: Boolean? = null,

    @field:SerializedName("building_no")
    val buildingNo: @RawValue Any? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: @RawValue Any? = null,

    @field:SerializedName("commercial_register")
    val commercialRegister: @RawValue CommercialRegister? = null,

    @field:SerializedName("phone_number2")
    val phoneNumber2: @RawValue Any? = null,

    @field:SerializedName("min_price_product")
    val minPriceProduct: @RawValue Any? = null,

    @field:SerializedName("has_delivery")
    val hasDelivery: Int? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("apartment_no")
    val apartmentNo: @RawValue Any? = null,

    @field:SerializedName("registration_image2")
    val registrationImage2: @RawValue Any? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("registration_image")
    val registrationImage: @RawValue Any? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("new_flag")
    val newFlag: Int? = null,

    @field:SerializedName("phone_number")
    val phoneNumber: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("delivery_price")
    val deliveryPrice: Int? = null,

    @field:SerializedName("whatsapp")
    val whatsapp: String? = null,

    @field:SerializedName("floor_no")
    val floorNo: @RawValue Any? = null,

    @field:SerializedName("short_description")
    val shortDescription: @RawValue Any? = null,

    @field:SerializedName("parking_domain")
    val parkingDomain: @RawValue Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("code_name")
    val codeName: @RawValue Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    /*@field:SerializedName("avg_rating")
    val avgRating: Any? = null,*/

    @field:SerializedName("email")
    val email: @RawValue Any? = null,

    @field:SerializedName("covers")
    val covers: List<CoversItem?>? = null,

    @field:SerializedName("address")
    val address: @RawValue Any? = null,

    @field:SerializedName("delivery_distance")
    val deliveryDistance: Int? = null,

    @field:SerializedName("registration_file2")
    val registrationFile2: @RawValue Any? = null,

    @field:SerializedName("causer_id")
    val causerId: @RawValue Any? = null,

    @field:SerializedName("thumbnail_id")
    val thumbnailId: Int? = null,

    @field:SerializedName("offer_flag")
    val offerFlag: Int? = null,

    @field:SerializedName("wallet_credit")
    val walletCredit: @RawValue Any? = null,

    @field:SerializedName("followers_count")
    val followersCount: Int? = null,

    @field:SerializedName("fixed_commission_price")
    val fixedCommissionPrice: Int? = null,

    @field:SerializedName("is_busy")
    val isBusy: Int? = null,

    @field:SerializedName("skus")
    val skus: ArrayList<StoreSkusItem?>? = null,

    /*@field:SerializedName("is_featured")
    val isFeatured: Boolean? = null*/
) : Parcelable
