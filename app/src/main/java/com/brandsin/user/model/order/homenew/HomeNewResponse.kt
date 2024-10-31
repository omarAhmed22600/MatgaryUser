package com.brandsin.user.model.order.homenew

import android.os.Parcelable
import com.brandsin.user.model.menu.offers.OffersItemDetails
import com.brandsin.user.model.order.details.Store
import com.brandsin.user.model.order.homepage.ImagesItem
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.homepage.TagsItem
import com.brandsin.user.model.order.productdetails.CustomProperties
import com.brandsin.user.model.order.reorder.CommercialRegister
import com.brandsin.user.model.order.storedetails.CoversItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
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
) : Parcelable

@Parcelize
data class Slider(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("slides")
    val slides: List<SlidesItem?>? = null,

    @field:SerializedName("name")
    val name: String? = null,
) : Parcelable

@Parcelize
data class CategoriesItem(

    @field:SerializedName("cover")
    val cover: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("imageEn")
    val imageEn: @RawValue Any? = null,

    @field:SerializedName("mobileImage")
    val mobileImage: @RawValue Any? = null,

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("bannerMobileImage")
    val bannerMobileImage: @RawValue Any? = null,

    @field:SerializedName("sidebar")
    val sidebar: @RawValue Any? = null,

    @field:SerializedName("name")
    val categoryName: String? = null,

    @field:SerializedName("id")
    val categoryId: Int? = null,

    @field:SerializedName("item_order")
    val itemOrder: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("tags")
    val tags: List<TagsItem?>? = null
) : Parcelable

@Parcelize
data class SlidesItem(

    @field:SerializedName("store_id")
    val storeId: @RawValue Any? = null,

    @field:SerializedName("category_id")
    val categoryId: Int? = null,

    @field:SerializedName("store_ids")
    val storeIds: String? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("store_ids_array")
    val storeIdsArray: ArrayList<String>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("content_en")
    val contentEn: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("slider_id")
    val sliderId: Int? = null,

    @field:SerializedName("content") // content
    val content: String? = null
) : Parcelable

@Parcelize
data class StoresDataItem(

    @field:SerializedName("distance")
    val distance: Double? = null,

    @field:SerializedName("delivery_time")
    val deliveryTime: Int? = null,

    @field:SerializedName("delivery_type")
    val deliveryType: String? = null,

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
    val images: List<ImagesItem?>? = null,

    @field:SerializedName("lng")
    val lng: String? = null,

    @field:SerializedName("min_order_price")
    val minOrderPrice: String? = null,

    @field:SerializedName("commercial_register")
    val commercialRegister: CommercialRegister? = null,

    @field:SerializedName("delivery_distance")
    val deliveryDistance: Int? = null,

    @field:SerializedName("min_price_product")
    val minPriceProduct: @RawValue Any? = null,

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
) : Parcelable

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

@Parcelize
data class SectionsItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("slider") // slider
    val slider: Slider? = null,

    @field:SerializedName("orderItem")
    val orderItem: Int? = null,

    @field:SerializedName("stores_data")
    val storesData: List<StoresDataItem?>? = null,

    @field:SerializedName("brands_models")
    val brandItem: ArrayList<BrandItem?>? = null,

    @field:SerializedName("products_models")
    val productItem: ArrayList<StoreProductItem?>? = null,

    @field:SerializedName("gifts_models")
    val giftItem:  ArrayList<GiftItem?>? = null,

    @field:SerializedName("categories_models")
    val categoriesModels: ArrayList<CategoriesItem?>? = null,

    @field:SerializedName("offers_models")
    val offersItem:  ArrayList<OffersItemDetails?>? = null,

    @field:SerializedName("stores")
    val stores: @RawValue Any? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("slider_id")
    val sliderId: Int? = null
) : Parcelable

@Parcelize
data class BrandItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("slug")
    val slug: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("is_featured")
    val isFeatured: Boolean? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: @RawValue Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("excel_code")
    val excelCode: @RawValue Any? = null,

    @field:SerializedName("image")
    val image: String? = null,
) : Parcelable

@Parcelize
data class GiftItem(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("price")
    val price: Double? = null,

    @SerializedName("price_to")
    val priceTo: Long? = null,

    @SerializedName("start_date")
    val startDate: String? = null,

    @SerializedName("end_date")
    val endDate: String? = null,

    @SerializedName("store_id")
    val storeId: Int? = null,

    @SerializedName("active")
    val active: Int? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("created_by")
    val createdBy: Long? = null,

    @SerializedName("updated_by")
    val updatedBy: Long? = null,

    // @SerializedName("deleted_at")
    // val deletedAt: Any? = null,

    // @SerializedName("created_at")
    // val createdAt: Any? = null,

    // @SerializedName("updated_at")
    // val updatedAt: Any? = null,

    @SerializedName("image")
    val image: String? = null,

    // @SerializedName("video")
    // val video: Any? = null,

    @SerializedName("name_en")
    val nameEn: String? = null,

    @SerializedName("description_en")
    val descriptionEn: String?,

    @SerializedName("store")
    val store: Store? = null,
) : Parcelable

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


@Parcelize
data class PopupsItem(

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("category_id")
    val categoryId: @RawValue Any? = null,

    @field:SerializedName("store_ids")
    val storeIds: ArrayList<String>? = null,

    @field:SerializedName("product_id")
    val productId: @RawValue Any? = null,

    @field:SerializedName("store_ids_array")
    val storeIdsArray: @RawValue Any? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("content")
    val content: String? = null
) : Parcelable

@Parcelize
data class MediaItem(

    @field:SerializedName("manipulations")
    val manipulations: @RawValue List<Any?>? = null,

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
) : Parcelable
