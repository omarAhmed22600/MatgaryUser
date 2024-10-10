package com.brandsin.user.model

import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.brandsin.user.model.order.storedetails.MediaItem
import com.brandsin.user.model.order.storedetails.Shipping
import com.brandsin.user.model.order.storedetails.StoreAddonsItem
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.model.order.storedetails.StoreSkusItem
import com.brandsin.user.ui.main.order.reviewStore.model.RatingItem
import com.brandsin.user.ui.profile.favoriteProduct.model.Pivot
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.RawValue

class MessageResponse {

    @field:SerializedName("success") // "success": true,
    val success: Boolean? = null

    @field:SerializedName("message") // "message": "Updated!"
    val message: String? = null
}