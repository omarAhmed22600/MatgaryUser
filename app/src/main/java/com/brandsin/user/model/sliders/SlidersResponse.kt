package com.brandsin.user.model.sliders

import android.os.Parcelable
import com.brandsin.user.model.order.homenew.SlidesItem
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SlidersResponse(
    val `data`: @RawValue Data? = null,
    val success: Boolean? = null
) : Parcelable

@Parcelize
data class Data(
    val created_at: String,
    val created_by: Int,
    val deleted_at: @RawValue  Any,
    val id: Int,
    val init_options: @RawValue Any,
    val key: String,
    val name: String,
    val slides: List<SlidesItem>,
    val status: String,
    val type: String,
    val updated_at: String,
    val updated_by: Int
): Parcelable

//data class Slide(
//    val category_id: Int,
//    val content: String,
//    val content_en: String,
//    val created_at: String,
//    val created_by: Int,
//    val deleted_at: Any,
//    val description: Any,
//    val id: Int,
//    val name: String,
//    val product_id: Any,
//    val slider_id: Int,
//    val status: String,
//    val store_id: Any,
//    val store_ids: String,
//    val store_ids_array: List<String>,
//    val type: String,
//    val updated_at: String,
//    val updated_by: Int,
//    val url: String
//)