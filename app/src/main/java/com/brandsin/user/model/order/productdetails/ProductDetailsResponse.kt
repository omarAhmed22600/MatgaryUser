package com.brandsin.user.model.order.productdetails

import com.brandsin.user.model.order.searchproducts.Store
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductDetailsResponse(

    @field:SerializedName("data")
    val data: StoreProductItem? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
) : Serializable

data class ImagesIdsItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("url")
    val url: String? = null
) : Serializable

//data class StoreProductItem(
//
//    @field:SerializedName("addons")
//    val addons: List<StoreAddonsItem?>? = null,
//
//    @field:SerializedName("caption")
//    val caption: String? = null,
//
//    @field:SerializedName("today_offers")
//    val todayOffers: Int? = null,
//
//    @field:SerializedName("discount")
//    val discount: Int? = null,
//
//    @field:SerializedName("type")
//    val type: String? = null,
//
//    @field:SerializedName("external_url")
//    val externalUrl: Any? = null,
//
//    @field:SerializedName("shipping")
//    val shipping: Shipping? = null,
//
//    @field:SerializedName("admin_status")
//    val adminStatus: String? = null,
//
//    @field:SerializedName("id")
//    val id: Int? = null,
//
//    @field:SerializedName("slug_ar")
//    val slugAr: String? = null,
//
//    @field:SerializedName("slug")
//    val slug: String? = null,
//
//    @field:SerializedName("deep_link")
//    val deepLink: Any? = null,
//
//    @field:SerializedName("whatsapp_number")
//    val whatsappNumber: Any? = null,
//
//    @field:SerializedName("offers")
//    val offers: List<Any?>? = null,
//
//    @field:SerializedName("image")
//    val image: String? = null,
//
//    @field:SerializedName("images")
//    val images: List<String?>? = null,
//
//    @field:SerializedName("meta_title")
//    val metaTitle: Any? = null,
//
//    @field:SerializedName("created_by")
//    val createdBy: Int? = null,
//
//    @field:SerializedName("deleted_at")
//    val deletedAt: Any? = null,
//
//    @field:SerializedName("brand_id")
//    val brandId: Any? = null,
//
//    @field:SerializedName("is_used")
//    val isUsed: Int? = null,
//
//    @field:SerializedName("meta_description")
//    val metaDescription: Any? = null,
//
//    @field:SerializedName("user_id")
//    val userId: Any? = null,
//
//    @field:SerializedName("name")
//    val name: String? = null,
//
//    @field:SerializedName("updated_by")
//    val updatedBy: Int? = null,
//
//    @field:SerializedName("files")
//    val files: Any? = null,
//
//    @field:SerializedName("country_id")
//    val countryId: Any? = null,
//
//    @field:SerializedName("status")
//    val status: String? = null,
//
//    @field:SerializedName("city_id")
//    val cityId: Any? = null,
//
//    @field:SerializedName("code")
//    val code: Any? = null,
//
//    @field:SerializedName("flag")
//    val flag: Int? = null,
//
//    @field:SerializedName("skus")
//    val skus: List<StoreSkusItem?>? = null,
//
//    @field:SerializedName("description")
//    val description: String? = null,
//
//    @field:SerializedName("product_status")
//    val productStatus: Any? = null,
//
//    @field:SerializedName("created_at")
//    val createdAt: String? = null,
//
//    @field:SerializedName("video")
//    val video: Any? = null,
//
//    @field:SerializedName("media")
//    val media: List<MediaItem?>? = null,
//
//    @field:SerializedName("meta_keywords")
//    val metaKeywords: Any? = null,
//
//    @field:SerializedName("related_products")
//    val relatedProducts: Any? = null,
//
//    @field:SerializedName("video_url")
//    val videoUrl: Any? = null,
//
//    @field:SerializedName("updated_at")
//    val updatedAt: String? = null,
//
//    @field:SerializedName("categories")
//    val categories: List<CategoriesItem?>? = null,
//
//    @field:SerializedName("views")
//    val views: Int? = null,
//
//    @field:SerializedName("store_id")
//    val storeId: Int? = null,
//
//    @field:SerializedName("images_ids")
//    val imagesIds: List<ImagesIdsItem?>? = null,
//
//    @field:SerializedName("address")
//    val address: Any? = null,
//
//    @field:SerializedName("deep_link_ar")
//    val deepLinkAr: Any? = null,
//
//    @field:SerializedName("description_en")
//    val descriptionEn: Any? = null,
//
//    @field:SerializedName("is_shareable")
//    val isShareable: Int? = null,
//
//    @field:SerializedName("sale_by_phone")
//    val saleByPhone: Any? = null,
//
//    @field:SerializedName("properties")
//    val properties: Any? = null,
//
//    @field:SerializedName("is_featured")
//    val isFeatured: Boolean? = null,
//
//    @field:SerializedName("name_en")
//    val nameEn: Any? = null,
//
//    var isSelected : Boolean = false
//) : Serializable

data class Shipping(

    @field:SerializedName("width")
    val width: Int? = null,

    @field:SerializedName("length")
    val length: Int? = null,

    @field:SerializedName("weight")
    val weight: Int? = null,

    @field:SerializedName("enabled")
    val enabled: Int? = null,

    @field:SerializedName("height")
    val height: Int? = null
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

data class CustomProperties(

    @field:SerializedName("featured")
    val featured: Boolean? = null,

    @field:SerializedName("root")
    val root: String? = null
) : Serializable

data class CategoriesItem(

    @field:SerializedName("cover")
    val cover: Any? = null,

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
    val id: Int? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Serializable

data class StoreSkusItem(

    @field:SerializedName("allowed_quantity")
    val allowedQuantity: Int? = null,

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("free_shipping")
    val freeShipping: Int? = null,

    @field:SerializedName("inventory")
    val inventory: String? = null,

    @field:SerializedName("sale_price")
    val salePrice: Double? = null,

    @field:SerializedName("free_refunding")
    val freeRefunding: Int? = null,

    @field:SerializedName("regular_price")
    val regularPrice: Double? = null,

    @field:SerializedName("inventory_value")
    val inventoryValue: String? = null,

    @field:SerializedName("unit")
    val unit: Any? = null,

    @field:SerializedName("shipping")
    val shipping: Shipping? = null,

    @field:SerializedName("price")
    val price: Double? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("secured")
    val secured: Int? = null,

    @field:SerializedName("unit_id")
    val unitId: Any? = null,

    @field:SerializedName("status")
    val status: String? = null,

    var isSelected: Boolean = false

) : Serializable
data class StoreAddonsItem(

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("price")
    val price: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    var isSelected: Boolean = false

) : Serializable
