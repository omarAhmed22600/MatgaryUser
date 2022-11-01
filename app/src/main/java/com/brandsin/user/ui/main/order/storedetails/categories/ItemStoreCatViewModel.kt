package com.brandsin.user.ui.main.order.storedetails.categories

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.storedetails.StoreCategoryItem

class ItemStoreCatViewModel(var itemCategory: StoreCategoryItem) : BaseViewModel()
{
    fun onCategoryClicked() {
        setValue("hi")
    }
}