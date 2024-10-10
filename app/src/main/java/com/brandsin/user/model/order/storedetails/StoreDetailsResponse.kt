package com.brandsin.user.model.order.storedetails

import android.os.Parcelable
import com.brandsin.user.model.order.SearchProdactAttr.StoreItemColors
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.ui.main.order.reviewStore.model.RatingItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class StoreDetailsResponse(

    @field:SerializedName("data")
    val storeDetailsData: StoreDetailsData? = null,

    @field:SerializedName("success")
    val isSuccess: Boolean? = null
) : Parcelable

@Parcelize
data class StoreDetailsData(

    @field:SerializedName("has_delivery")
    val hasDelivery: Int? = null,

    @field:SerializedName("has_today_stories")
    val hasTodayStories: Boolean = false,

    @field:SerializedName("is_followed")
    var isFollowed: Boolean = false,

    @field:SerializedName("is_closed")
    val isClosed: Int? = null,

    @field:SerializedName("is_busy")
    val isBusy: Int? = null,

    @field:SerializedName("min_order_price")
    val minOrderPrice: String? = null,

    @field:SerializedName("min_price_product")
    val minPriceProduct: @RawValue Any? = null,

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("followers_count")
    val followersCount: String? = null,

    @field:SerializedName("products_categories")
    val storeCategoriesList: List<StoreCategoryItem?>? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("avg_rating")
    val avgRating: Double? = null,

    @field:SerializedName("working_hours_12")
    val workingHours12: WorkingHours? = null,

    @field:SerializedName("payment_method")
    val paymentMethod: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,
    @field:SerializedName("user_id")
    val userId: Int? = null,

//    @field:SerializedName("delivery_distance")
//    val deliveryDistance: Float? = null,

    @field:SerializedName("delivery_time")
    val deliveryTime: Int? = null,

    @field:SerializedName("delivery_type")
    val deliveryType: String? = null,

    @field:SerializedName("pick_up_from_store")
    val pickUpFromStore: Int? = null,

    @field:SerializedName("cash_on_delivery")
    val cashOnDelivery: Int? = null,

    @field:SerializedName("delivery_price")
    val deliveryPrice: Int? = null,

    @field:SerializedName("covers")
    val covers: List<CoversItem?>? = null,

    @field:SerializedName("products")
    val storeProductList: List<StoreProductItem?>? = null,

    @field:SerializedName("pinned_stories")
    val stories: ArrayList<StoriesItem>? = null,

    @field:SerializedName("lat")
    val lat: String = "",

    @field:SerializedName("lng")
    val lng: String = "",


) : Parcelable

@Parcelize
data class WorkingHours(
    @field:SerializedName("default_from")
    val defaultFrom: String? = null,

    @field:SerializedName("default_to")
    val defaultTo: String? = null,
) : Parcelable

@Parcelize
data class StoriesSubItem(

    @field:SerializedName("stories")
    val stories: ArrayList<StoriesItem>? = null,

    @field:SerializedName("date")
    val date: String? = null,

    ) : Parcelable

@Parcelize
data class MediaStoriesItem(

    // @field:SerializedName("manipulations")
    // val manipulations: List<Any?>? = null,

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

    // @field:SerializedName("custom_properties")
    // val customProperties: List<Any?>? = null,

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

@Parcelize
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
) : Parcelable

@Parcelize
data class StoreAddonsItem(

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("price")
    val price: Float? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    var isSelected: Boolean = false
) : Parcelable

@Parcelize
data class CoversItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("content")
    val content: String? = null

) : Parcelable

@Parcelize
data class StoreCategoryItem(

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
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
) : Parcelable

@Parcelize
data class StoreProductItem(

    @field:SerializedName("store")
    var store: StoreDetailsData? = null,

    @field:SerializedName("name_en")
    var nameEn: String? = null,

    @field:SerializedName("description_en")
    var descriptionEn: String? = null,

    @field:SerializedName("images")
    val images: List<String?>? = null,

    @field:SerializedName("images_ids")
    val imagesIds: ArrayList<ImagesIdsItem?>? = null,

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("flag")
    val flag: @RawValue Any? = null,

    @field:SerializedName("skus")
    val skus: List<StoreSkusItem?>? = null,

    @field:SerializedName("addons")
    val addons: List<StoreAddonsItem?>? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("product_status")
    val productStatus: @RawValue Any? = null,

    @field:SerializedName("caption")
    val caption: String? = null,

    @field:SerializedName("today_offers")
    val todayOffers: @RawValue Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("discount")
    val discount: @RawValue Any? = null,

    @field:SerializedName("video")
    val video: String? = null,

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

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("categories")
    val productCategoriesList: List<CategoriesItem?>? = null,

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

    @field:SerializedName("ratings")
    val ratings: List<RatingItem?>? = null,

    // @field:SerializedName("properties")
    // val properties: @RawValue List<Any?>? = null,

    @field:SerializedName("is_featured")
    val isFeatured: Boolean? = null,

    @field:SerializedName("is_favorite")
    val isFavorite: Boolean? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("city_id")
    val cityId: @RawValue Any? = null,

    @field:SerializedName("next_id")
    val nextId: Int? = null,

    @field:SerializedName("prev_id")
    val prevId: Int? = null,

    @field:SerializedName("is_shareable")
    val isShareable: @RawValue Any? = null,

    @field:SerializedName("is_refund")
    val isRefund: @RawValue Any? = null,

    @field:SerializedName("refund_days")
    val refundDays: @RawValue Any? = null,

    @field:SerializedName("sku_id")
    val skuId: @RawValue Any? = null,

    @field:SerializedName("is_refundable")
    val isRefundable: Boolean? = null,

    @field:SerializedName("avg_rating")
    val avgRating: @RawValue Any? = null,

    var isSelected: Boolean = false
) : Parcelable

@Parcelize
data class Rating(

    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("rating")
    val rating: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("body")
    val body: String? = null,

    @field:SerializedName("reviewrateable_id")
    val reviewRateableID: Long? = null,

    @field:SerializedName("reviewrateable_type")
    val reviewRateableType: String? = null,

    @field:SerializedName("author_id")
    val authorID: Long? = null,

    @field:SerializedName("author_type")
    val authorType: String? = null,

    val criteria: @RawValue Any? = null,

    val status: String? = null,

    @field:SerializedName("created_by")
    val createdBy: Long? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Long? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("order_id")
    val orderID: Long? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("video")
    val video: String? = null,

    @field:SerializedName("video_image")
    val videoImage: String? = null,

    @field:SerializedName("has_media")
    val hasMedia: Boolean? = null
) : Parcelable

@Parcelize
data class ImagesIdsItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("url")
    val url: String? = null
) : Parcelable

@Parcelize
data class StoreSkusItem(

    @field:SerializedName("allowed_quantity")
    val allowedQuantity: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("code")
    val code: String? = null,

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
    val inventoryValue: String? = null,

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
    val status: String? = null,

    var isSelected: Boolean = false,

    @field:SerializedName("key")
    val key: String? = null,

    @field:SerializedName("media")
    val media: @RawValue List<Any?>? = null,

    @field:SerializedName("unit_id")
    val unitId: Int? = null,

    @field:SerializedName("values")
    val values: List<StoreItemColors>? = null

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

@Parcelize
data class CustomProperties(

    @field:SerializedName("featured")
    val featured: Boolean? = null,

    @field:SerializedName("root")
    val root: String? = null
) : Parcelable

@Parcelize
data class TaxesItem(

    @field:SerializedName("zip")
    val zip: @RawValue Any? = null,

    @field:SerializedName("country")
    val country: @RawValue Any? = null,

    @field:SerializedName("tax_class_id")
    val taxClassId: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("priority")
    val priority: Int? = null,

    @field:SerializedName("compound")
    val compound: Int? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: @RawValue Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("rate")
    val rate: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("state")
    val state: @RawValue Any? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable
