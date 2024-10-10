package com.brandsin.user.ui.main.order.reviewStore.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class RatingsResponse(

    @field:SerializedName("avg_ratings")
    val avgRatings: @RawValue Any? = null,

    @field:SerializedName("data")
    val ratingItem: List<RatingItem?>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
):Parcelable

@Parcelize
data class RatingItem(

    @field:SerializedName("reviewrateable_type")
    val reviewRateableType: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("reviewrateable_id")
    val reviewRateableId: Int? = null,

    // @field:SerializedName("criteria")
    // val criteria: Any? = null,

    @field:SerializedName("author")
    val author: Author? = null,

    val criteria: @RawValue Any? = null,

    @field:SerializedName("rating")
    val rating: String? = null,

    @field:SerializedName("has_media")
    val hasMedia: Boolean? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("video")
    val video: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("body")
    val body: String? = null,

    @field:SerializedName("author_type")
    val authorType: String? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    // @field:SerializedName("deleted_at")
    // val deletedAt: Any? = null,

    @field:SerializedName("video_image")
    val videoImage: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("author_id")
    val authorId: Int? = null,

    // @field:SerializedName("order_id")
    // val orderId: @RawValue Any? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable

@Parcelize
data class Author(

    // @field:SerializedName("national_id")
    // val nationalId: List<Any?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("picture")
    val picture: String? = null,

    @field:SerializedName("picture_thumb")
    val pictureThumb: String? = null
) : Parcelable
