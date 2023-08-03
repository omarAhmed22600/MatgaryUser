package com.brandsin.user.model.order.homenew

import com.google.gson.annotations.SerializedName
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.homepage.TagsItem
import java.io.Serializable

data class HomeNewResponse(

	@field:SerializedName("stories")
	val stories: List<List<StoriesItem?>>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("popups")
	val popups: List<PopupsItem?>? = null,

	@field:SerializedName("categories")
	val categories: List<CategoriesItem?>? = null,

	@field:SerializedName("sections")
	val sections: List<SectionsItem?>? = null
): Serializable

data class SlidesItem(

	@field:SerializedName("store_id")
	val storeId: Any? = null,

	@field:SerializedName("category_id")
	val categoryId: Int? = null,

	@field:SerializedName("store_ids")
	val storeIds: String? = null,

	@field:SerializedName("product_id")
	val productId: Any? = null,

	@field:SerializedName("store_ids_array")
	val storeIdsArray: ArrayList<String>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("content_en")
	val contentEn: String? = null,

	@field:SerializedName("slider_id")
	val sliderId: Int? = null,

	@field:SerializedName("content")
	val content: String? = null
): Serializable

data class StoresDataItem(

	@field:SerializedName("distance")
	val distance: Double? = null,

	@field:SerializedName("delivery_time")
	val deliveryTime: Int? = null,

	@field:SerializedName("approved")
	val approved: Int? = null,

	@field:SerializedName("avg_rating")
	val avgRating: Double? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("categories")
	val categories: List<CategoriesItem?>? = null,

	@field:SerializedName("lat")
	val lat: String? = null,

	@field:SerializedName("is_closed")
	val isClosed: Int? = null,

	@field:SerializedName("covers")
	val covers: List<CoversItem?>? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("images")
	val images: List<Images?>? = null,

	@field:SerializedName("lng")
	val lng: String? = null,

	@field:SerializedName("min_order_price")
	val minOrderPrice: String? = null,

	@field:SerializedName("commercial_register")
	val commercialRegister: Any? = null,

	@field:SerializedName("delivery_distance")
	val deliveryDistance: Int? = null,

	@field:SerializedName("min_price_product")
	val minPriceProduct: String? = null,

	@field:SerializedName("has_delivery")
	val hasDelivery: Int? = null,

	@field:SerializedName("thumbnail_id")
	val thumbnailId: Int? = null,

	@field:SerializedName("offer_flag")
	val offerFlag: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("new_flag")
	val newFlag: Int? = null,

	@field:SerializedName("is_busy")
	val isBusy: Int? = null,

	@field:SerializedName("in_distance")
	val inDistance: Int? = null,

	@field:SerializedName("delivery_price")
	val deliveryPrice: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
): Serializable

//data class TagsItem(
//
//	@field:SerializedName("thumbnail")
//	val thumbnail: String? = null,
//
//	@field:SerializedName("name")
//	val name: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("status")
//	val status: Int? = null,
//
//	@field:SerializedName("item_order")
//	val itemOrder: Int? = null
//): Serializable

data class CategoriesItem(

	@field:SerializedName("thumbnail")
	val img: String? = null,

	@field:SerializedName("name")
	val categoryName: String? = null,

	@field:SerializedName("id")
	val categoryId: Int? = null,

	@field:SerializedName("item_order")
	val itemOrder: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("tags")
	val tags: List<TagsItem?>? = null
): Serializable

data class SectionsItem(

	@field:SerializedName("slider")
	val slider: Slider? = null,

	@field:SerializedName("orderItem")
	val orderItem: Int? = null,

	@field:SerializedName("stores_data")
	val storesData: List<StoresDataItem?>? = null,

	@field:SerializedName("stores")
	val stores: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("slider_id")
	val sliderId: Int? = null
): Serializable

data class Slider(

	@field:SerializedName("slides")
	val slides: List<SlidesItem?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
): Serializable

data class CoversItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
): Serializable

data class Images(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
): Serializable

//data class StoriesItemItem(
//
//	@field:SerializedName("store_id")
//	val storeId: Int? = null,
//
//	@field:SerializedName("in_offers_page")
//	val inOffersPage: Int? = null,
//
//	@field:SerializedName("created_at")
//	val createdAt: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("text")
//	val text: Any? = null,
//
//	@field:SerializedName("store")
//	val store: Store? = null,
//
//	@field:SerializedName("media")
//	val media: List<MediaItem?>? = null,
//
//	@field:SerializedName("media_url")
//	val mediaUrl: String? = null,
//
//	@field:SerializedName("views")
//	val views: Int? = null,
//
//	@field:SerializedName("is_pinned")
//	val isPinned: Int? = null,
//
//	@field:SerializedName("is_pinned_homepage")
//	val isPinnedHomepage: Int? = null
//): Serializable

data class Store(

	@field:SerializedName("min_price_product")
	val minPriceProduct: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("images")
	val images: List<Any?>? = null,

	@field:SerializedName("thumbnail_id")
	val thumbnailId: Int? = null,

	@field:SerializedName("avg_rating")
	val avgRating: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("commercial_register")
	val commercialRegister: Any? = null,

	@field:SerializedName("covers")
	val covers: List<CoversItem?>? = null
): Serializable

data class PopupsItem(

	@field:SerializedName("store_id")
	val storeId: Int? = null,

	@field:SerializedName("category_id")
	val categoryId: Any? = null,

	@field:SerializedName("store_ids")
	val storeIds: ArrayList<String>? = null,

	@field:SerializedName("product_id")
	val productId: Any? = null,

	@field:SerializedName("store_ids_array")
	val storeIdsArray: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("content")
	val content: String? = null
): Serializable

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
	val customProperties: List<Any?>? = null,

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
): Serializable
