package com.brandsin.user.model.order.homenew

import com.google.gson.annotations.SerializedName

data class ItemsSectionResponse(

	@field:SerializedName("data")
	val data: List<BrandItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class DataItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("excel_code")
	val excelCode: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("slug")
	val slug: String? = null,

	@field:SerializedName("is_featured")
	val isFeatured: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null
)
