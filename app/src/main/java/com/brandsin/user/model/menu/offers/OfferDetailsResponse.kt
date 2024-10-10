package com.brandsin.user.model.menu.offers

import com.google.gson.annotations.SerializedName

data class OfferDetailsResponse(

    @field:SerializedName("data")
    val offer: OffersItemDetails? = null,

    @field:SerializedName("success")
    val isSuccess: Boolean? = null
)

data class CategoriesItem(

    @field:SerializedName("bannerMobileImage")
    val bannerMobileImage: Any? = null,

    @field:SerializedName("description")
    val description: Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("external_id")
    val externalId: Any? = null,

    @field:SerializedName("cover")
    val cover: Any? = null,

    @field:SerializedName("imageEn")
    val imageEn: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("sidebar")
    val sidebar: Any? = null,

    @field:SerializedName("show_in_home_page")
    val showInHomePage: Boolean? = null,

    @field:SerializedName("excel_code")
    val excelCode: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("KuteStyle")
    val kuteStyle: String? = null,

    @field:SerializedName("slug")
    val slug: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("mobileImage")
    val mobileImage: Any? = null,

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("vertical_menu_theme")
    val verticalMenuTheme: Any? = null,

    @field:SerializedName("category_order")
    val categoryOrder: Int? = null,

    @field:SerializedName("in_vertical_menu")
    val inVerticalMenu: Int? = null,

    @field:SerializedName("parent_id")
    val parentId: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("fixed_commission_price")
    val fixedCommissionPrice: Any? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("is_featured")
    val isFeatured: Boolean? = null,

    @field:SerializedName("status")
    val status: String? = null
)



