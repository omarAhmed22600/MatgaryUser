package com.brandsin.user.model.order.details

import com.google.gson.annotations.SerializedName

data class OrderDetailsResponse(

	@field:SerializedName("offers")
	var offers: List<offerItem?>? = null,

	@field:SerializedName("total")
	val total: List<TotalItem?>? = null,

	@field:SerializedName("success")
	val isSuccess: Boolean? = null,

	@field:SerializedName("discount")
	val discount: Any? = null,

	@field:SerializedName("items")
	val items: ArrayList<OrderItem?>? = null,

	@field:SerializedName("order")
	val order: Order? = null,

	@field:SerializedName("message")
	val message: String? = null,
)

data class ItemOptions(

	@field:SerializedName("product_options")
	val productOptions: String? = null
)

data class Store(

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class ProductOptions(

	@field:SerializedName("")
	val jsonMember: String? = null
)

data class TotalItem(

	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("sub_total")
	val subTotal: String? = null
)

data class Order(

	@field:SerializedName("order_number")
	val orderNumber: String? = null,

	@field:SerializedName("user_notes")
	val userNotes: String? = null,

	@field:SerializedName("delivery_time")
	val deliveryTime: String? = null,

	@field:SerializedName("street_name")
	val streetName: String? = null,

	@field:SerializedName("billing")
	val billing: Billing? = null,

	@field:SerializedName("city_name")
	val cityName: String? = null,

	@field:SerializedName("state_name")
	val stateName: String? = null,

	@field:SerializedName("country_name")
	val countryName: String? = null,

	@field:SerializedName("store_name")
	val storeName: String? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("state_id")
	val stateId: Int? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("lat")
	val lat: String? = null,

	@field:SerializedName("discount_id")
	val discountId: Int? = null,

	@field:SerializedName("store_id")
	val storeId: Int? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("lng")
	val lng: String? = null,

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

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("city_id")
	val cityId: Int? = null
)

data class Billing(

	@field:SerializedName("payment_status")
	val paymentStatus: String? = null,

	@field:SerializedName("billing_address")
	val billingAddress: BillingAddress? = null,

	@field:SerializedName("payment_reference")
	val paymentReference: String? = null,

	@field:SerializedName("gateway")
	val gateway: String? = null
)

data class AddonsItem(

	@field:SerializedName("store_id")
	val storeId: Int? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

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

	@field:SerializedName("street_name")
	val streetName: String? = null
)

data class CreatedAt(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("timezone")
	val timezone: String? = null,

	@field:SerializedName("timezone_type")
	val timezoneType: Int? = null
)


data class offerItem(
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
	@field:SerializedName("created_at")
	var created_at: String? = null,
	@field:SerializedName("offer")
	var offer:offerItem2? = null,

	)
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

	)

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
	var itemOptionsValue: List<Any?>? = null,

	@field:SerializedName("addons")
	var addons: List<AddonsItem> = ArrayList(),

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

	var obsRate : Float = 5.0f
)
