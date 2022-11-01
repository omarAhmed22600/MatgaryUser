package com.brandsin.user.model.order.storedetails

import com.google.gson.annotations.SerializedName
import com.brandsin.user.model.order.homepage.StoriesItem
import java.io.Serializable

data class StoreDetailsResponse(

    @field:SerializedName("data")
    val storeDetailsData: StoreDetailsData? = null,

    @field:SerializedName("success")
    val isSuccess: Boolean? = null
) : Serializable

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
    val minPriceProduct: String? = null,

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("products_categories")
    val storeCategoriesList: List<StoreCategoryItem?>? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("avg_rating")
    val avgRating: Double? = null,

    @field:SerializedName("working_hours_12")
    val workingHours12: WorkingHours? = null,
    @field:SerializedName("payment_method")
    val payment_method: String? = null,
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

//    @field:SerializedName("delivery_distance")
//    val deliveryDistance: Float? = null,

    @field:SerializedName("delivery_time")
    val deliveryTime: Int? = null,

    @field:SerializedName("delivery_price")
    val deliveryPrice: Int? = null,

    @field:SerializedName("covers")
    val covers: List<CoversItem?>? = null,

    @field:SerializedName("products")
    val storeProductList: List<StoreProductItem?>? = null,

    @field:SerializedName("pinned_stories")
    val stories: ArrayList<StoriesItem>? = null
) : Serializable

data class WorkingHours(
    @field:SerializedName("default_from")
    val defaultFrom: String? = null,

    @field:SerializedName("default_to")
    val defaultTo: String? = null,
): Serializable

data class StoriesSubItem(

    @field:SerializedName("stories")
    val stories: ArrayList<StoriesItem>? = null,

    @field:SerializedName("date")
    val date: String? = null,

    ) : Serializable

data class MediaStoriesItem(

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
) : Serializable

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

) : Serializable

data class CoversItem(

    @field:SerializedName("id")
    val id: Int? = null,
    @field:SerializedName("url")
    val url: String? = null,
    @field:SerializedName("content")
    val content: String? = null
) : Serializable

data class StoreCategoryItem(

    @field:SerializedName("cover")
    val cover: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("imageEn")
    val imageEn: Any? = null,

    @field:SerializedName("mobileImage")
    val mobileImage: Any? = null,

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("bannerMobileImage")
    val bannerMobileImage: Any? = null,

    @field:SerializedName("sidebar")
    val sidebar: Any? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
) : Serializable

data class ImagesIdsItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("url")
    val url: String? = null
) : Serializable

data class StoreProductItem(

    @field:SerializedName("name_en")
    var nameEn: String? = null,
    @field:SerializedName("description_en")
    var descriptionEn: String? = null,

    @field:SerializedName("images")
    val images: List<String?>? = null,

    @field:SerializedName("images_ids")
    val imagesIds: List<ImagesIdsItem?>? = null,

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("flag")
    val flag: Int? = null,

    @field:SerializedName("skus")
    val skus: List<StoreSkusItem?>? = null,

    @field:SerializedName("addons")
    val addons: List<StoreAddonsItem?>? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("product_status")
    val productStatus: Any? = null,

    @field:SerializedName("caption")
    val caption: String? = null,

    @field:SerializedName("today_offers")
    val todayOffers: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("discount")
    val discount: Int? = null,

    @field:SerializedName("video")
    val video: Any? = null,

    @field:SerializedName("media")
    val media: List<MediaItem?>? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("meta_keywords")
    val metaKeywords: Any? = null,

    @field:SerializedName("related_products")
    val relatedProducts: Any? = null,

    @field:SerializedName("external_url")
    val externalUrl: Any? = null,

    @field:SerializedName("video_url")
    val videoUrl: Any? = null,

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
    val deepLink: Any? = null,

    @field:SerializedName("views")
    val views: Int? = null,

    @field:SerializedName("whatsapp_number")
    val whatsappNumber: Any? = null,

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("address")
    val address: Any? = null,

    @field:SerializedName("meta_title")
    val metaTitle: Any? = null,

    @field:SerializedName("deep_link_ar")
    val deepLinkAr: Any? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("brand_id")
    val brandId: Int? = null,

    @field:SerializedName("is_used")
    val isUsed: Int? = null,

    @field:SerializedName("meta_description")
    val metaDescription: Any? = null,

    @field:SerializedName("user_id")
    val userId: Any? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("files")
    val files: Any? = null,

    @field:SerializedName("sale_by_phone")
    val saleByPhone: Any? = null,

    @field:SerializedName("country_id")
    val countryId: Any? = null,

    @field:SerializedName("properties")
    val properties: List<Any?>? = null,

    @field:SerializedName("is_featured")
    val isFeatured: Boolean? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("city_id")
    val cityId: Any? = null,

    var isSelected: Boolean = false
) : Serializable

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

    var isSelected: Boolean = false

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

data class CategoriesItem(

    @field:SerializedName("cover")
    val cover: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("imageEn")
    val imageEn: Any? = null,

    @field:SerializedName("mobileImage")
    val mobileImage: Any? = null,

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("bannerMobileImage")
    val bannerMobileImage: Any? = null,

    @field:SerializedName("sidebar")
    val sidebar: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null
) : Serializable

data class CustomProperties(

    @field:SerializedName("featured")
    val featured: Boolean? = null,

    @field:SerializedName("root")
    val root: String? = null
) : Serializable

data class TaxesItem(

    @field:SerializedName("zip")
    val zip: Any? = null,

    @field:SerializedName("country")
    val country: Any? = null,

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
    val deletedAt: Any? = null,

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
    val state: Any? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Serializable
