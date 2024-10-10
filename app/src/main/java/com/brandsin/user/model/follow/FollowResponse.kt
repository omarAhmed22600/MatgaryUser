package com.brandsin.user.model.follow

import com.google.gson.annotations.SerializedName

data class FollowResponse(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
