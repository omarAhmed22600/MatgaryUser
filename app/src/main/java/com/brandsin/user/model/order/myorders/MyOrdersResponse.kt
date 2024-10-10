package com.brandsin.user.model.order.myorders

import android.os.Parcelable
import com.brandsin.user.model.order.details.Store
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class MyOrderListResponse(

    @field:SerializedName("total")
    val total: Int? = null,

    @field:SerializedName("data")
    val data: List<MyOrderItem?>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("limit")
    val limit: String? = null
)

data class NationalIdItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("url")
    val url: String? = null
)

@Parcelize
data class MyOrderItem(

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("amount")
    val amount: String? = null,

    @field:SerializedName("driver_id")
    val driverId: @RawValue Any? = null,

    @field:SerializedName("comments")
    val comments: @RawValue List<Any?>? = null,

    @field:SerializedName("order_number")
    val orderNumber: String? = null,

    @field:SerializedName("user_notes")
    val userNotes: @RawValue Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("delivery_time")
    val deliveryTime: String? = null,

    @field:SerializedName("store")
    val store: Store? = null,

    @field:SerializedName("driver")
    val driver: @RawValue Any? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("is_rated")
    val isRated: Boolean? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user")
    val user: User? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable

@Parcelize
data class Item(

	@field:SerializedName("id")
    val id: Int? = null,

	@field:SerializedName("amount")
    val amount: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("quantity")
    val quantity: Int? = null,

	@field:SerializedName("sku_code")
    val skuCode: String? = null,

	@field:SerializedName("user_notes")
    val userNotes: String? = null,

    @field:SerializedName("store_id")
    val storeId: Int? = null,

	@field:SerializedName("store_name")
    val storeName: String? = null,

	@field:SerializedName("product_id")
    val productId: Int? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("image")
	val image: String? = null,
) : Parcelable

data class Driver(

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("driving_licence")
    val drivingLicence: String? = null,

    @field:SerializedName("lng")
    val lng: String? = null,

    @field:SerializedName("user_name")
    val userName: String? = null,

    @field:SerializedName("vehicle_type")
    val vehicleType: String? = null,

    @field:SerializedName("is_working")
    val isWorking: Boolean? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("vehicle_image")
    val vehicleImage: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("vehicle_number")
    val vehicleNumber: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user")
    val user: Any? = null,

    @field:SerializedName("lat")
    val lat: String? = null
)

@Parcelize
data class User(

    @field:SerializedName("national_id")
    val nationalId: @RawValue List<Any?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("picture")
    val picture: String? = null,

    @field:SerializedName("picture_thumb")
    val pictureThumb: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null
) : Parcelable
