package com.brandsin.user.model.menu.favourits

import com.brandsin.user.model.order.homepage.StoriesItem
import com.google.gson.annotations.SerializedName

data class FavoriteStoriesResponse(

	@field:SerializedName("data")
	val stories: List<List<StoriesItem?>>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class Store(

	@field:SerializedName("account_type")
	val accountType: Any? = null,

	@field:SerializedName("registration_file")
	val registrationFile: Any? = null,

	@field:SerializedName("delivery_time")
	val deliveryTime: Int? = null,

	@field:SerializedName("land_mark")
	val landMark: Any? = null,

	@field:SerializedName("causer_type")
	val causerType: Any? = null,

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
	val paymentMethod: Any? = null,

	@field:SerializedName("is_closed")
	val isClosed: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("images")
	val images: List<ImagesItem?>? = null,

	@field:SerializedName("owner_name")
	val ownerName: Any? = null,

	@field:SerializedName("lng")
	val lng: String? = null,

	@field:SerializedName("min_order_price")
	val minOrderPrice: String? = null,

	@field:SerializedName("is_followed")
	val isFollowed: Boolean? = null,

	@field:SerializedName("building_no")
	val buildingNo: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("commercial_register")
	val commercialRegister: Any? = null,

	@field:SerializedName("phone_number2")
	val phoneNumber2: Any? = null,

	@field:SerializedName("min_price_product")
	val minPriceProduct: Int? = null,

	@field:SerializedName("has_delivery")
	val hasDelivery: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("apartment_no")
	val apartmentNo: Any? = null,

	@field:SerializedName("registration_image2")
	val registrationImage2: Any? = null,

	@field:SerializedName("iban")
	val iban: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("registration_image")
	val registrationImage: Any? = null,

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
	val floorNo: Any? = null,

	@field:SerializedName("short_description")
	val shortDescription: Any? = null,

	@field:SerializedName("parking_domain")
	val parkingDomain: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("code_name")
	val codeName: Any? = null,

	@field:SerializedName("cash_on_delivery")
	val cashOnDelivery: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("avg_rating")
	val avgRating: Int? = null,

	@field:SerializedName("bank_name")
	val bankName: Any? = null,

	@field:SerializedName("email")
	val email: Any? = null,

	@field:SerializedName("covers")
	val covers: List<CoversItem?>? = null,

	@field:SerializedName("address")
	val address: Any? = null,

	@field:SerializedName("pick_up_from_store")
	val pickUpFromStore: Int? = null,

	@field:SerializedName("delivery_distance")
	val deliveryDistance: Int? = null,

	@field:SerializedName("registration_file2")
	val registrationFile2: Any? = null,

	@field:SerializedName("causer_id")
	val causerId: Any? = null,

	@field:SerializedName("thumbnail_id")
	val thumbnailId: Int? = null,

	@field:SerializedName("offer_flag")
	val offerFlag: Int? = null,

	@field:SerializedName("wallet_credit")
	val walletCredit: Int? = null,

	@field:SerializedName("followers_count")
	val followersCount: Int? = null,

	@field:SerializedName("fixed_commission_price")
	val fixedCommissionPrice: Any? = null,

	@field:SerializedName("is_busy")
	val isBusy: Int? = null,

	@field:SerializedName("is_featured")
	val isFeatured: Int? = null
)

data class ImagesItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class CustomProperties(

	@field:SerializedName("featured")
	val featured: Boolean? = null,

	@field:SerializedName("root")
	val root: String? = null
)

data class ImagesIdsItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class CoversItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class SkusItem(

	@field:SerializedName("allowed_quantity")
	val allowedQuantity: Int? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("free_shipping")
	val freeShipping: Int? = null,

	@field:SerializedName("media")
	val media: List<Any?>? = null,

	@field:SerializedName("inventory")
	val inventory: String? = null,

	@field:SerializedName("sale_price")
	val salePrice: String? = null,

	@field:SerializedName("free_refunding")
	val freeRefunding: Int? = null,

	@field:SerializedName("regular_price")
	val regularPrice: String? = null,

	@field:SerializedName("inventory_value")
	val inventoryValue: String? = null,

	@field:SerializedName("shipping")
	val shipping: Any? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("secured")
	val secured: Int? = null,

	@field:SerializedName("unit_id")
	val unitId: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class MediaItem(

	@field:SerializedName("manipulations")
	val manipulations: List<Any?>? = null,

	@field:SerializedName("order_column")
	val orderColumn: Int? = null,

	@field:SerializedName("file_name")
	val fileName: String? = null,

	@field:SerializedName("model_type")
	val modelType: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("model_id")
	val modelId: Int? = null,

	@field:SerializedName("custom_properties")
	val customProperties: CustomProperties? = null,

	@field:SerializedName("disk")
	val disk: String? = null,

	@field:SerializedName("size")
	val size: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("mime_type")
	val mimeType: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("collection_name")
	val collectionName: String? = null
)

data class Product(

	@field:SerializedName("images_ids")
	val imagesIds: List<ImagesIdsItem?>? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("images")
	val images: List<String?>? = null,

	@field:SerializedName("is_favorite")
	val isFavorite: Boolean? = null,

	@field:SerializedName("skus")
	val skus: List<SkusItem?>? = null,

	@field:SerializedName("description_en")
	val descriptionEn: String? = null,

	@field:SerializedName("discount")
	val discount: Int? = null,

	@field:SerializedName("video")
	val video: Any? = null,

	@field:SerializedName("media")
	val media: List<MediaItem?>? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("is_refundable")
	val isRefundable: Boolean? = null,

	@field:SerializedName("avg_rating")
	val avgRating: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("files")
	val files: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name_en")
	val nameEn: String? = null
)

data class CommercialRegister(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
)
