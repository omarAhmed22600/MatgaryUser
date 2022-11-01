package com.brandsin.user.ui.main.home.category

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.CategoriesItem

class ItemCategoryViewModel(var itemCategory: CategoriesItem) : BaseViewModel()
{
    fun onCategoryClicked() {
        setValue("hi")
    }
}