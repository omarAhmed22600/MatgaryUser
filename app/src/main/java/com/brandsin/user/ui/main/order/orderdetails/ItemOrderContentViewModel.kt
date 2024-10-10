package com.brandsin.user.ui.main.order.orderdetails

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.details.AddonsItem
import com.brandsin.user.model.order.details.OfferItem
import com.brandsin.user.model.order.details.OrderItem

class ItemOrderContentViewModel(var item: OrderItem,var orderDetailsAddonsAdapter: OrderDetailsAddonsAdapter) : BaseViewModel()

class ItemOrderAddonsContentViewModel(var item: AddonsItem) : BaseViewModel()