package com.brandsin.user.model.order.SearchProdactAttr

import com.google.gson.annotations.SerializedName

data class SearchProductAttRequest (
        @SerializedName("product_id") var product_id: Int? = null,
        @SerializedName("return_json") var return_json: Int? = null,
        @SerializedName("sku_id") var sku_id: String? = null,
        @SerializedName("attr") var attr: String? = null,
        @SerializedName("value") var value: String? = null,
        )