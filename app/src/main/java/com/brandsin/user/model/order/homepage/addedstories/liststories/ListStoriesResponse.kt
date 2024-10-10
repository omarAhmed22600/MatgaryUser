package com.brandsin.user.model.order.homepage.addedstories.liststories

import com.google.gson.annotations.SerializedName
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.productdetails.CustomProperties
import java.io.Serializable

data class ListStoriesResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
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

data class DataItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("stories")
	val stories: ArrayList<StoriesItem>? = null
) : Serializable

//data class StoriesItem(
//
//	@field:SerializedName("store_id")
//	var storeId: Int? = null,
//
//	@field:SerializedName("created_at")
//	var createdAt: String? = null,
//
//	@field:SerializedName("id")
//	var id: Int? = null,
//
//	@field:SerializedName("text")
//	var text: String? = null,
//
//	@field:SerializedName("media")
//	var media: List<MediaItem?>? = null,
//
//	@field:SerializedName("media_url")
//	var mediaUrl: String? = null,
//
//	@field:SerializedName("views")
//	var views: Int? = null
//) : Serializable
