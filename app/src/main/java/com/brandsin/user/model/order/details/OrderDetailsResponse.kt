package com.brandsin.user.model.order.details

import android.os.Parcelable
import com.brandsin.user.model.order.reorder.CommercialRegister
import com.brandsin.user.model.order.storedetails.CoversItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class OrderDetailsResponse(

    @field:SerializedName("offers")
    var offers: ArrayList<OfferItem?>? = null,

    @field:SerializedName("total")
    val total: List<TotalItem?>? = null,

    @field:SerializedName("success")
    val isSuccess: Boolean? = null,

    @field:SerializedName("discount")
    val discount: @RawValue Any? = null,

    @field:SerializedName("shipping")
    val shipping: Shipping? = null,

    @field:SerializedName("payment")
    val payment: Payment? = null,

    @field:SerializedName("items")
    val items: ArrayList<OrderItem?>? = null,

    @field:SerializedName("order")
    val order: Order? = null,

    @field:SerializedName("message")
    val message: String? = null,
) : Parcelable

@Parcelize
data class Shipping(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("sku_code")
    val skuCode: String? = null,

    @field:SerializedName("amount")
    val amount: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("quantity")
    val quantity: Int? = null,

    @field:SerializedName("type")
    val type: String? = null,

    // @field:SerializedName("item_options")
    // val itemOptions: Any? = null,

    // @field:SerializedName("user_notes")
    // val userNotes: Any? = null,

    // @field:SerializedName("created_at")
    // val createdAt: String? = null,
) : Parcelable

@Parcelize
data class ItemOptions(

    @field:SerializedName("product_options")
    val productOptions: String? = null
) : Parcelable

@Parcelize
data class Store(

    @field:SerializedName("min_order_price")
    val minOrderPrice: String? = null,

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
    var thumbnail: String? = null,

    // @field:SerializedName("images")
    // val images: List<ImagesItem?>? = null,

    @field:SerializedName("lng")
    val lng: String? = null,

    @field:SerializedName("is_followed")
    val isFollowed: Boolean? = null,

    @field:SerializedName("building_no")
    val buildingNo: @RawValue Any? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: @RawValue Any? = null,

    @field:SerializedName("commercial_register")
    val commercialRegister: CommercialRegister? = null,

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
    var name: String? = null,

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

    @field:SerializedName("avg_rating")
    val avgRating: Double? = null,

    @field:SerializedName("email")
    val email: @RawValue Any? = null,

    @field:SerializedName("covers")
    val covers: @RawValue List<CoversItem?>? = null,

    @field:SerializedName("address")
    val address: String? = null,

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
    val walletCredit: Double? = null,

    @field:SerializedName("followers_count")
    val followersCount: Int? = null,

    @field:SerializedName("fixed_commission_price")
    val fixedCommissionPrice: Int? = null,

    @field:SerializedName("is_busy")
    val isBusy: Int? = null,

    @field:SerializedName("is_featured")
    val isFeatured: Int? = null
) : Parcelable

/*
@Parcelize
data class Store(

    @field:SerializedName("min_order_price")
    val minOrderPrice: String? = null,

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
    var thumbnail: String? = null,

    // @field:SerializedName("images")
    // val images: List<ImagesItem?>? = null,

    @field:SerializedName("lng")
    val lng: String? = null,

    @field:SerializedName("is_followed")
    val isFollowed: Boolean? = null,

    @field:SerializedName("building_no")
    val buildingNo: @RawValue Any? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: @RawValue Any? = null,

    @field:SerializedName("commercial_register")
    val commercialRegister: CommercialRegister? = null,

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
    var name: String? = null,

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

    @field:SerializedName("avg_rating")
    val avgRating: Double? = null,

    @field:SerializedName("email")
    val email: @RawValue Any? = null,

    @field:SerializedName("covers")
    val covers: @RawValue List<CoversItem?>? = null,

    @field:SerializedName("address")
    val address: String? = null,

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
    val walletCredit: Double? = null,

    @field:SerializedName("followers_count")
    val followersCount: Int? = null,

    @field:SerializedName("fixed_commission_price")
    val fixedCommissionPrice: Int? = null,

    @field:SerializedName("is_busy")
    val isBusy: Int? = null,

    @field:SerializedName("is_featured")
    val isFeatured: Int? = null
) : Parcelable
 */

@Parcelize
data class ProductOptions(

    @field:SerializedName("")
    val jsonMember: String? = null
) : Parcelable

@Parcelize
data class TotalItem(

    @field:SerializedName("total")
    val total: Double? = null,

    @field:SerializedName("sub_total")
    val subTotal: Double? = null
) : Parcelable

@Parcelize
data class Order(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("order_number")
    val orderNumber: String? = null,

    @field:SerializedName("amount")
    val amount: String? = null,

    @field:SerializedName("currency")
    val currency: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("billing")
    val billing: @RawValue Billing? = null, ///////

    @field:SerializedName("user_notes")
    val userNotes: String? = null,

    @field:SerializedName("delivery_time")
    val deliveryTime: String? = null,

    @field:SerializedName("street_name")
    val streetName: String? = null,

    @field:SerializedName("city_name")
    val cityName: String? = null,

    @field:SerializedName("state_name")
    val stateName: String? = null,

    @field:SerializedName("country_name")
    val countryName: String? = null,

    @field:SerializedName("store_name")
    val storeName: String? = null,

    @field:SerializedName("state_id")
    val stateId: Int? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:SerializedName("lat")
    val lat: String? = null,

    @field:SerializedName("lng")
    val lng: String? = null,

    @field:SerializedName("discount_id")
    val discountId: Int? = null,

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("address_id")
    val addressId: Int? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("store")
    val store: Store? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("phone_number")
    val phoneNumber: String? = null,

    @field:SerializedName("country_id")
    val countryId: Int? = null,

    @field:SerializedName("city_id")
    val cityId: Int? = null,

    @field:SerializedName("has_packaging")
    val hasPackaging: Int? = null,

    @field:SerializedName("comments")
    val comments: @RawValue List<Any?>? = null,

    @field:SerializedName("is_rated")
    val isRated: Boolean? = null,

    @field:SerializedName("address")
    val address: @RawValue Address? = null,

    @field:SerializedName("user")
    val user: @RawValue User? = null,

    @field:SerializedName("amount_after_discount")
    val amountAfterDiscount: Int? = null,

    @field:SerializedName("packaging_price")
    val packagingPrice: String? = null,

    @field:SerializedName("gifter_name")
    val gifterName: @RawValue Any? = null,

    @field:SerializedName("order_type")
    val orderType: String? = null,

    @field:SerializedName("gifter_mobile")
    val gifterMobile: @RawValue Any? = null,
) : Parcelable

@Parcelize
data class Address(

    @field:SerializedName("lng")
    val lng: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("type_label")
    val typeLabel: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("is_default")
    val isDefault: Int? = null,

    @field:SerializedName("lat")
    val lat: String? = null
) : Parcelable

@Parcelize
data class User(

    @field:SerializedName("national_id")
    val nationalId: @RawValue List<Any?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("picture")
    val picture: String? = null,

    @field:SerializedName("picture_thumb")
    val pictureThumb: String? = null
) : Parcelable

@Parcelize
data class Billing(

    @field:SerializedName("billing_address")
    val billingAddress: BillingAddress? = null,

    @field:SerializedName("payment_status")
    val paymentStatus: String? = null,

    @field:SerializedName("payment_reference")
    val paymentReference: String? = null,

    @field:SerializedName("gateway")
    val gateway: String? = null
) : Parcelable

@Parcelize
data class AddonsItem(

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
) : Parcelable

@Parcelize
data class BillingAddress(

    @field:SerializedName("zip")
    val zip: String? = null,

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("address_1")
    val address1: String? = null,

    @field:SerializedName("address_2")
    val address2: String? = null,

    @field:SerializedName("phone_number")
    val phoneNumber: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("landmark")
    val landmark: @RawValue Any? = null,

    @field:SerializedName("street_name")
    val streetName: String? = null
) : Parcelable

@Parcelize
data class CreatedAt(

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("timezone")
    val timezone: String? = null,

    @field:SerializedName("timezone_type")
    val timezoneType: Int? = null
) : Parcelable

@Parcelize
data class Payment(

    @field:SerializedName("delivery")
    val delivery: Int? = null,

    @field:SerializedName("total")
    val total: Double? = null,

    @field:SerializedName("total_after_discount")
    val totalAfterDiscount: Double? = null,

    @field:SerializedName("subtotal")
    val subtotal: Double? = null,

    @field:SerializedName("discount")
    val discount: Int? = null
) : Parcelable

@Parcelize
data class OfferItem(

    @field:SerializedName("sku_code")
    var sku_code: String? = null,

    @field:SerializedName("amount")
    var amount: String? = null,

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("quantity")
    var quantity: Int? = null,

    @field:SerializedName("type")
    var type: String? = null,

    @field:SerializedName("item_options")
    var item_options: String? = null,

    @field:SerializedName("user_notes")
    var user_notes: String? = null,

    @field:SerializedName("gift_code")
    var giftCode: String? = null,

    @field:SerializedName("image")
    var image: String? = null,

    @field:SerializedName("offer")
    var offer: offerItem2? = null,

    ) : Parcelable

@Parcelize
data class offerItem2(

    @field:SerializedName("id")
    var id: Int? = null,
    @field:SerializedName("name")
    var name: String? = null,
    @field:SerializedName("description")
    var description: String? = null,
    @field:SerializedName("price")
    var price: Int? = null,
    @field:SerializedName("price_to")
    var price_to: Int? = null,
    @field:SerializedName("start_date")
    var start_date: String? = null,

    @field:SerializedName("end_date")
    var end_date: String? = null,

    @field:SerializedName("store_id")
    var store_id: Int? = null,

    @field:SerializedName("active")
    var active: Int? = null,

    @field:SerializedName("created_by")
    var created_by: Int? = null,

    @field:SerializedName("updated_by")
    var updated_by: Int? = null,
    @field:SerializedName("deleted_at")
    var deleted_at: String? = null,
    @field:SerializedName("created_at")
    var created_at: String? = null,
    @field:SerializedName("updated_at")
    var updated_at: String? = null,
    @field:SerializedName("image")
    var image: String? = null,
    @field:SerializedName("name_en")
    var name_en: String? = null,
    @field:SerializedName("description_en")
    var description_en: String? = null,

    ) : Parcelable

@Parcelize
data class OrderItem(

    @field:SerializedName("store_id")
    var storeId: Int? = null,

    @field:SerializedName("image")
    var image: String? = null,

    @field:SerializedName("amount")
    var amount: String? = null,

    @field:SerializedName("item_options")
    var itemOptions: ItemOptions? = null,

    @field:SerializedName("quantity")
    var quantity: Int? = null,

    @field:SerializedName("item_options_value")
    var itemOptionsValue: @RawValue List<Any?>? = null,

    @field:SerializedName("addons")
    var addons: List<AddonsItem> = ArrayList(),

    @field:SerializedName("user_notes")
    val userNotes: String? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("created_at")
    var createdAt: CreatedAt? = null,

    @field:SerializedName("type")
    var type: String? = null,

    @field:SerializedName("product_name")
    var productName: String? = null,

    @field:SerializedName("product_id")
    var productId: Int? = null,

    @field:SerializedName("store_name")
    var storeName: String? = null,

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("sku_code")
    var skuCode: String? = null,

    var obsRate: Float = 5.0f,

    var title: String? = null,
    var body: String? = null,
    var rate: Int? = null,
    var pickImage: String? = null,
    var video: String? = null

) : Parcelable

