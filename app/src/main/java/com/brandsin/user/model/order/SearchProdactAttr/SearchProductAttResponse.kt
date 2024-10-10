package com.brandsin.user.model.order.SearchProdactAttr

import android.os.Parcelable
import com.brandsin.user.model.order.storedetails.StoreSkusItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SearchProductAttResponse(

    @field:SerializedName("attributes")
    val attributes: ArrayList<StoreSkusItem>,

    @field:SerializedName("images")
    val images: @RawValue List<Any>,

    @field:SerializedName("sku")
    val sku: Sku,

    @field:SerializedName("sku_id")
    val sku_id: Int
): Parcelable

//data class Attribute(
//    val key: String,
//    val values: List<StoreItemColors>
//)

@Parcelize
data class Sku(

    @field:SerializedName("allowed_quantity")
    val allowed_quantity: Int? = null,

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("free_refunding")
    val free_refunding: Int? = null,

    @field:SerializedName("free_shipping")
    val free_shipping: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("inventory")
    val inventory: String? = null,

    @field:SerializedName("inventory_value")
    val inventory_value: String? = null,

    val media: @RawValue List<Any>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("product_id")
    val product_id: Int? = null,

    @field:SerializedName("regular_price")
    val regular_price: String? = null,

    @field:SerializedName("sale_price")
    val sale_price: String? = null,

    @field:SerializedName("secured")
    val secured: Int? = null,

    val shipping: @RawValue Any? = null,

    @field:SerializedName("status")
    val status: String? = null,

    val unit_id: @RawValue Any? = null,

):Parcelable

@Parcelize
data class StoreItemColors(

    @field:SerializedName("attribute_id")
    val attributeId: Int? = null,

    @field:SerializedName("is_disabled")
    val is_disabled: Boolean? = null,

    @field:SerializedName("is_selected")
    var is_selected: Int? = null,

    @field:SerializedName("label")
    val label: String? = null,

    @field:SerializedName("number_value")
    val numberValue: Int? = null,

    @field:SerializedName("sku_id")
    val skuId: Int? = null,

    @field:SerializedName("value")
    val value: String? = null
) : Parcelable