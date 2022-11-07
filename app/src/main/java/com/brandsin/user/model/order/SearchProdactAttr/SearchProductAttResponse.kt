package com.brandsin.user.model.order.SearchProdactAttr

import com.brandsin.user.model.order.storedetails.StoreSkusItem
import com.google.gson.annotations.SerializedName

data class SearchProductAttResponse(
    val attributes: ArrayList<StoreSkusItem>,
    val images: List<Any>,
    val sku: Sku,
    val sku_id: Int
)

//data class Attribute(
//    val key: String,
//    val values: List<StoreItemColors>
//)

data class Sku(
    val allowed_quantity: Int?=null,
    val code: String?=null,
    val created_at: String?=null,
    val created_by: Int?=null,
    val deleted_at: Any?=null,
    val free_refunding: Int?=null,
    val free_shipping: Int?=null,
    val id: Int?=null,
    val inventory: String?=null,
    val inventory_value: String?=null,
    val media: List<Any>?=null,
    val name: String?=null,
    val price: String?=null,
    val product_id: Int?=null,
    val regular_price: String?=null,
    val sale_price: Any?=null,
    val secured: Int?=null,
    val shipping: Any?=null,
    val status: String?=null,
    val unit_id: Any?=null,
    val updated_at: String?=null,
    val updated_by: Int?=null
)

data class StoreItemColors(
    val attribute_id: Int,
    val is_disabled: Boolean,
    val is_selected: Int,
    val label: String,
    val number_value: Int,
    val sku_id: Int,
    val value: String
)