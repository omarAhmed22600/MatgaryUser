package com.brandsin.user.ui.profile.favoriteProduct.model

import android.os.Parcelable
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class FavoriteProductResponse(

    @field:SerializedName("data")
    val data: List<StoreProductItem?>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
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

@Parcelize
data class Shipping(

    @field:SerializedName("enabled")
    val enabled: Int? = null
): Parcelable

@Parcelize
data class CustomProperties(

    @field:SerializedName("root")
    val root: String? = null,

    @field:SerializedName("featured")
    val featured: Boolean? = null
):Parcelable

@Parcelize
data class Pivot(

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("wishlistable_id")
    val wishlistableId: Int? = null
) : Parcelable


