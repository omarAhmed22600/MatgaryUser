package com.brandsin.user.ui.menu.payment

import com.google.gson.annotations.SerializedName

data class WalletTransactionsResponse(

    @field:SerializedName("current_credit")
    val currentCredit: Double? = null,

    @field:SerializedName("transactions")
    val transactions: List<TransactionsItem?>? = null
)

data class TransactionsItem(

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("current_credit")
    val currentCredit: Double? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("increase")
    val increase: Double? = null,

    @field:SerializedName("decrease")
    val decrease: Double? = null,

    @field:SerializedName("note")
    val note: String? = null,
)
