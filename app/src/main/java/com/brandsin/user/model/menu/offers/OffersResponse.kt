package com.brandsin.user.model.menu.offers

import android.os.Parcelable
import com.brandsin.user.model.order.details.Store
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.io.Serializable

data class OffersResponse(

	@field:SerializedName("data")
	val offersList: List<OffersItemDetails?>? = null,

	@field:SerializedName("success")
	val isSuccess: Boolean? = null
) : Serializable

data class SkusItem(

	@field:SerializedName("allowed_quantity")
	val allowedQuantity: Int? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("media")
	val media: List<Any?>? = null,

	@field:SerializedName("name")
	val name: Any? = null,

	@field:SerializedName("unit_id")
	val unitId: Any? = null,

	@field:SerializedName("free_shipping")
	val freeShipping: Int? = null,

	@field:SerializedName("inventory")
	val inventory: String? = null,

	@field:SerializedName("sale_price")
	val salePrice: String? = null,

	@field:SerializedName("free_refunding")
	val freeRefunding: Int? = null,

	@field:SerializedName("regular_price")
	val regularPrice: String? = null,

	@field:SerializedName("inventory_value")
	val inventoryValue: Any? = null,

	@field:SerializedName("shipping")
	val shipping: Shipping? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("secured")
	val secured: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Serializable

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
) : Serializable

@Parcelize
data class OffersItemDetails(

	@field:SerializedName("store_id")
	val storeId: Int? = null,

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("name_en")
	val nameEn: String? = null,

	@field:SerializedName("description_en")
	val descriptionEn: String? = null,

	@field:SerializedName("price_to")
	val priceTo: Double? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("active")
	val active: Int? = null,

	val type: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("store")
	val store: Store? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("video")
	val video: String? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: String? = null,

	@field:SerializedName("categories")
	val categories: @RawValue List<CategoriesItem?>? = null,

	@field:SerializedName("products")
	val productsList: ArrayList<ProductsItem>? = null,

	) : Parcelable

@Parcelize
data class ProductsItem(

	@field:SerializedName("code")
	val code: @RawValue Any? = null,

	@field:SerializedName("flag")
	val flag: Int? = null,

	@field:SerializedName("skus")
	val skus: List<SkusItem?>? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("product_status")
	val productStatus: @RawValue Any? = null,

	@field:SerializedName("caption")
	val caption: String? = null,

	@field:SerializedName("today_offers")
	val todayOffers: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("discount")
	val discount: Int? = null,

	@field:SerializedName("video")
	val video: @RawValue Any? = null,

	@field:SerializedName("media")
	val media: List<MediaItem?>? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("meta_keywords")
	val metaKeywords: @RawValue Any? = null,

	@field:SerializedName("related_products")
	val relatedProducts: @RawValue Any? = null,

	@field:SerializedName("external_url")
	val externalUrl: @RawValue Any? = null,

	@field:SerializedName("video_url")
	val videoUrl: @RawValue Any? = null,

	@field:SerializedName("shipping")
	val shipping: Shipping? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("admin_status")
	val adminStatus: String? = null,

	@field:SerializedName("pivot")
	val pivot: Pivot? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("slug_ar")
	val slugAr: String? = null,

	@field:SerializedName("slug")
	val slug: String? = null,

	@field:SerializedName("deep_link")
	val deepLink: @RawValue Any? = null,

	@field:SerializedName("views")
	val views: Int? = null,

	@field:SerializedName("whatsapp_number")
	val whatsappNumber: @RawValue Any? = null,

	@field:SerializedName("store_id")
	val storeId: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("address")
	val address: @RawValue Any? = null,

	@field:SerializedName("meta_title")
	val metaTitle: @RawValue Any? = null,

	@field:SerializedName("deep_link_ar")
	val deepLinkAr: @RawValue Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: @RawValue Any? = null,

	@field:SerializedName("brand_id")
	val brandId: Int? = null,

	@field:SerializedName("is_used")
	val isUsed: Int? = null,

	@field:SerializedName("meta_description")
	val metaDescription: @RawValue Any? = null,

	@field:SerializedName("user_id")
	val userId: @RawValue Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Int? = null,

	@field:SerializedName("files")
	val files: @RawValue Any? = null,

	@field:SerializedName("sale_by_phone")
	val saleByPhone: @RawValue Any? = null,

	@field:SerializedName("country_id")
	val countryId: @RawValue Any? = null,

	@field:SerializedName("properties")
	val properties: @RawValue List<Any?>? = null,

	@field:SerializedName("is_featured")
	val isFeatured: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("city_id")
	val cityId: @RawValue Any? = null,

	@field:SerializedName("addons")
	val addons: @RawValue List<Any?>? = null,

	@field:SerializedName("images")
	val images: List<String?>? = null,

	@field:SerializedName("is_favorite")
	val isFavorite: Boolean? = null,

	@field:SerializedName("refund_days")
	val refundDays: Int? = null,

	@field:SerializedName("avg_rating")
	val avgRating: Int? = null,

	@field:SerializedName("is_refund")
	val isRefund: Int? = null,

	@field:SerializedName("images_ids")
	val imagesIds: List<ImagesIdsItem?>? = null,

	@field:SerializedName("description_en")
	val descriptionEn: String? = null,

	@field:SerializedName("is_refundable")
	val isRefundable: Boolean? = null,

	@field:SerializedName("is_shareable")
	val isShareable: Int? = null,

	@field:SerializedName("name_en")
	val nameEn: String? = null

) : Parcelable

data class CustomProperties(

	@field:SerializedName("featured")
	val featured: Boolean? = null,

	@field:SerializedName("root")
	val root: String? = null
) : Serializable

/*@Parcelize
data class Store(

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("lng")
	val lng: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("name_en")
	val nameEn: String? = null,

	@field:SerializedName("description_en")
	val descriptionEn: String? = null,

	@field:SerializedName("media")
	val media: @RawValue List<Any?>? = null,

	@field:SerializedName("lat")
	val lat: String? = null,

	@field:SerializedName("delivery_price")
	val deliveryPrice: Int? = null
) : Parcelable*/

data class Shipping(

	@field:SerializedName("width")
	val width: String? = null,

	@field:SerializedName("length")
	val length: String? = null,

	@field:SerializedName("weight")
	val weight: String? = null,

	@field:SerializedName("enabled")
	val enabled: String? = null,

	@field:SerializedName("height")
	val height: String? = null
) : Serializable

data class Pivot(

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("offer_id")
	val offerId: Int? = null
) : Serializable
