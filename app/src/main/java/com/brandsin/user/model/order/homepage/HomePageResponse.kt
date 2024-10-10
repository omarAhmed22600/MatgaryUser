package com.brandsin.user.model.order.homepage

import android.os.Parcelable
import com.brandsin.user.model.order.details.Store
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.model.order.reorder.CommercialRegister
import com.brandsin.user.model.order.storedetails.CoversItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.ui.profile.favoriteProduct.model.CustomProperties
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class HomePageResponse(

    @field:SerializedName("data")
    val data: @RawValue Data? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
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
data class TagsItem(

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
) : Parcelable

@Parcelize
data class StoriesItem(

    @field:SerializedName("in_offers_page")
    val inOffersPage: Int? = null,

    @field:SerializedName("store_id")
    var storeId: Int? = null,

    @field:SerializedName("created_at")
    var createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    var title: String? = null,

    @field:SerializedName("text")
    var text: String? = null,

    @field:SerializedName("store")
    var store: Store? = null,

    @field:SerializedName("media")
    val media: ArrayList<MediaItem>? = null,

    @field:SerializedName("media_url")
    var mediaUrl: String? = null,

    @field:SerializedName("views")
    var views: Int? = null,

    @field:SerializedName("fav_count")
    var favCount: Int? = null,

    @field:SerializedName("is_favorite")
    var isFavorite: Boolean? = null,

    @field:SerializedName("is_pinned")
    var isPinned: Int? = null,

    @field:SerializedName("is_pinned_homepage")
    var isPinnedHomepage: Int? = null,

    @field:SerializedName("product")
    val product: StoreProductItem? = null,

    @field:SerializedName("is_seen")
    val isSeen: Boolean? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("x")
    val x: Double? = null,

    @field:SerializedName("y")
    val y: Double? = null,

    ) : Parcelable {

//    fun isVideo() =
//        (mediaUrl.let { it!!.endsWith(".mp4", true)})
//            ?: false
}

@Parcelize
data class ShopsItem(

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
    val covers: @RawValue List<CoversItem?>? = null,

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
    val commercialRegister: @RawValue CommercialRegister? = null,

    @field:SerializedName("delivery_distance")
    val deliveryDistance: Int? = null,

    @field:SerializedName("tags")
    val tags: List<TagsItem?>? = null,

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

@Parcelize
data class Data(

    @field:SerializedName("grouped_stories")
    val stories: List<List<StoriesItem>>? = null,

    @field:SerializedName("shops")
    val shops: List<ShopsItem?>? = null,

    @field:SerializedName("categories")
    val categories: List<CategoriesItem?>? = null
) : Parcelable

@Parcelize
data class ImagesItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("url")
    val url: String? = null
) : Parcelable
