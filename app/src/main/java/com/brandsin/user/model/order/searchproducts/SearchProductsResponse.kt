package com.brandsin.user.model.order.searchproducts

import android.os.Parcelable
import com.brandsin.user.model.order.homepage.ImagesItem
import com.brandsin.user.model.order.reorder.CommercialRegister
import com.brandsin.user.model.order.storedetails.CoversItem
import com.brandsin.user.model.order.storedetails.TaxesItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.io.Serializable

data class SearchProductsResponse(

    @field:SerializedName("data")
    var data: List<SearchProductItem?>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
): Serializable

data class WorkingHours(

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("sunday_to")
    val sundayTo: Any? = null,

    @field:SerializedName("thursday_to")
    val thursdayTo: Any? = null,

    @field:SerializedName("tuesday_to")
    val tuesdayTo: Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("default_to")
    val defaultTo: String? = null,

    @field:SerializedName("monday_to")
    val mondayTo: Any? = null,

    @field:SerializedName("monday_from")
    val mondayFrom: Any? = null,

    @field:SerializedName("tuesday_from")
    val tuesdayFrom: Any? = null,

    @field:SerializedName("wednesday_from")
    val wednesdayFrom: Any? = null,

    @field:SerializedName("sunday_from")
    val sundayFrom: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("thursday_from")
    val thursdayFrom: Any? = null,

    @field:SerializedName("friday_from")
    val fridayFrom: Any? = null,

    @field:SerializedName("saturday_to")
    val saturdayTo: Any? = null,

    @field:SerializedName("friday_to")
    val fridayTo: Any? = null,

    @field:SerializedName("wednesday_to")
    val wednesdayTo: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("saturday_from")
    val saturdayFrom: Any? = null,

    @field:SerializedName("default_from")
    val defaultFrom: String? = null
): Serializable

data class Store(

    @field:SerializedName("taxes")
    val taxes: List<TaxesItem?>? = null,

    @field:SerializedName("min_order_price")
    val minOrderPrice: Double? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("images")
    val images: List<Any?>? = null,

    @field:SerializedName("image_relation")
    val imageRelation: ImageRelation? = null,

    @field:SerializedName("delivery_time")
    val deliveryTime: Int? = null,

    @field:SerializedName("commercial_register")
    val commercialRegister: @RawValue CommercialRegister? = null,

    @field:SerializedName("min_price_product")
    val minPriceProduct: Any? = null,

    @field:SerializedName("thumbnail_id")
    val thumbnailId: Int? = null,

    @field:SerializedName("working_hours")
    val workingHours: WorkingHours? = null,

    @field:SerializedName("avg_rating")
    val avgRating: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("delivery_price")
    val deliveryPrice: Double? = null,

    @field:SerializedName("covers")
    val covers: List<CoversItem?>? = null,

    @field:SerializedName("is_closed")
    val isClosed: Int? = null
): Serializable

data class SkusItem(

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

    @field:SerializedName("shipping")
    val shipping: Shipping? = null,

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
    val unitId: Any? = null,

    @field:SerializedName("status")
    val status: String? = null
): Serializable

data class CustomProperties(

    @field:SerializedName("root")
    val root: String? = null,

    @field:SerializedName("featured")
    val featured: Boolean? = null
): Serializable

data class ImageRelation(

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
): Serializable

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
): Serializable

@Parcelize
data class SearchProductItem(

    @field:SerializedName("temp_id")
    val tempId: @RawValue Any? = null,

    @field:SerializedName("image_relation")
    val imageRelation: ImageRelation? = null,

    @field:SerializedName("caption")
    val caption: String? = null,

    @field:SerializedName("today_offers")
    val todayOffers: Int? = null,

    @field:SerializedName("discount")
    val discount: Int? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("external_url")
    val externalUrl: @RawValue Any? = null,

    @field:SerializedName("shipping")
    val shipping: Shipping? = null,

    @field:SerializedName("admin_status")
    val adminStatus: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("slug_ar")
    val slugAr: String? = null,

    @field:SerializedName("slug")
    val slug: String? = null,

    @field:SerializedName("deep_link")
    val deepLink: @RawValue Any? = null,

    @field:SerializedName("whatsapp_number")
    val whatsappNumber: @RawValue Any? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("images")
    val images: List<ImagesItem?>? = null,

    @field:SerializedName("meta_title")
    val metaTitle: @RawValue Any? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt:@RawValue  Any? = null,

    @field:SerializedName("brand_id")
    val brandId: @RawValue Any? = null,

    @field:SerializedName("is_used")
    val isUsed: Int? = null,

    @field:SerializedName("meta_description")
    val metaDescription: @RawValue Any? = null,

    @field:SerializedName("user_id")
    val userId:@RawValue  Any? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("files")
    val files: @RawValue Any? = null,

    @field:SerializedName("country_id")
    val countryId:@RawValue  Any? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("city_id")
    val cityId: @RawValue Any? = null,

    @field:SerializedName("code")
    val code: @RawValue Any? = null,

    @field:SerializedName("flag")
    val flag: Int? = null,

    @field:SerializedName("skus")
    val skus: List<SkusItem?>? = null,

    @field:SerializedName("display_order")
    val displayOrder: @RawValue Any? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("product_status")
    val productStatus: @RawValue Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("video")
    val video: @RawValue Any? = null,

    @field:SerializedName("media")
    val media: List<MediaItem?>? = null,

    @field:SerializedName("meta_keywords")
    val metaKeywords:@RawValue  Any? = null,

    @field:SerializedName("related_products")
    val relatedProducts: @RawValue Any? = null,

    @field:SerializedName("video_url")
    val videoUrl: @RawValue Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("views")
    val views: Int? = null,

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("images_ids")
    val imagesIds: List<ImagesItem?>? = null,

    @field:SerializedName("address")
    val address: @RawValue Any? = null,

    @field:SerializedName("deep_link_ar")
    val deepLinkAr: @RawValue Any? = null,

    @field:SerializedName("description_en")
    val descriptionEn: @RawValue Any? = null,

    @field:SerializedName("store")
    val store: Store? = null,

    @field:SerializedName("is_shareable")
    val isShareable: Int? = null,

    @field:SerializedName("sale_by_phone")
    val saleByPhone: @RawValue Any? = null,

    @field:SerializedName("properties")
    val properties: @RawValue Any? = null,

    @field:SerializedName("is_featured")
    val isFeatured: Boolean? = null,

    @field:SerializedName("name_en")
    val nameEn: @RawValue Any? = null
): Parcelable

