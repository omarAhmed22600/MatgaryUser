package com.brandsin.user.model.order.confirmorder.createorder

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @SerializedName("data") var data: String? = null,
    @SerializedName("lang") var lang: String? = null,
    @SerializedName("device_type") var deviceType: String? = null,
    @SerializedName("payment_method") var paymentMethod: String? = null,
    @SerializedName("delivery_time") var deliveryTime: String? = null,
    @SerializedName("discount_id") var discountId: String? = null,
    @SerializedName("discount_type") var discountType: String? = null,
    @SerializedName("discount_value") var discountValue: Double? = null,
    @SerializedName("user_notes") var userNotes: String? = null,
)

data class OrderData(
    @SerializedName("address_id") var addressId: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("order_items") var orderItems: ArrayList<OrderItemRequest>? = null,
    @SerializedName("offers") var offers: List<OfferOrderItem>? = null,
    @SerializedName("shipping") var shipping: ShippingItem? = null,
    @SerializedName("store_id") var store_id: String? = null,
    @SerializedName("user_id") var user_id: Int? = null,
)

data class ShippingItem(
    @SerializedName("amount") var amount: String = "",
    @SerializedName("provider") var provider: String = "",
)

data class OrderItemRequest(
    @SerializedName("amount") var amount: String = "0.0",
    @SerializedName("item_options") var itemOption: ItemOption? = null,
    @SerializedName("addons") var addons: ArrayList<String>? = null,
    @SerializedName("quantity") var quantity: String = "1",
    @SerializedName("sku_code") var skuCode: String? = null,
    @SerializedName("user_notes") var userNotes: String? = null,
)


data class ItemOption(
    @SerializedName("product_options") var product_options: String = ":",
)


/*
[
{
    "address_id": "2",
    "amount":"197.50",
    "order_items":[
    {
        "amount":"197.50",
        "item_options":{ "product_options":{ "":"" } },
        "addons": ["2"],
        "quantity":1,
        "sku_code":"452"
    }],
    "offers":[],
    "shipping":{ "amount":"0","provider":"" },
    "store_id":"3",
    "user_id":1
}
]*/
