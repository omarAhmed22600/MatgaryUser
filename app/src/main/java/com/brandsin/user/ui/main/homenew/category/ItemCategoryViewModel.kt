package com.brandsin.user.ui.main.homenew.category

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homenew.CategoriesItem

class ItemCategoryViewModel(var itemCategory: CategoriesItem) : BaseViewModel()
{
    fun onCategoryClicked() {
        setValue("hi")
    }
}