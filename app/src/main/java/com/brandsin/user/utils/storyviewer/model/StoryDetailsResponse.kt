package com.brandsin.user.utils.storyviewer.model

import android.os.Parcelable
import com.brandsin.user.model.order.details.Store
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class StoryDetailsResponse(

    @field:SerializedName("data")
    val storyItem: ArrayList<StoryItem?>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
) : Parcelable

@Parcelize
data class StoryItem(

    @field:SerializedName("store_id")
    val storeId: Int? = null,

    @field:SerializedName("is_favorite")
    val isFavorite: Boolean? = null,

    @field:SerializedName("video_img")
    val videoImg: @RawValue Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("store")
    val store: Store? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("text")
    val text: String? = null,

    @field:SerializedName("is_seen")
    val isSeen: Boolean? = null,

    @field:SerializedName("media_url")
    val mediaUrl: String? = null,

    @field:SerializedName("is_pinned")
    val isPinned: Int? = null,

    @field:SerializedName("fav_count")
    val favCount: Int? = null,

    @field:SerializedName("in_offers_page")
    val inOffersPage: Int? = null,

    @field:SerializedName("x")
    val x: @RawValue Any? = null,

    @field:SerializedName("y")
    val y: @RawValue Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("views")
    val views: Int? = null,

    @field:SerializedName("is_pinned_homepage")
    val isPinnedHomepage: Int? = null
) : Parcelable


