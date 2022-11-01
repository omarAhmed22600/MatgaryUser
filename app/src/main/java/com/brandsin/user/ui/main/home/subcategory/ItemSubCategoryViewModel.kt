package com.brandsin.user.ui.main.home.subcategory

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.TagsItem

class ItemSubCategoryViewModel(var itemCategory: TagsItem) : BaseViewModel()
{
    fun onSubCategoryClicked()
    {
        setValue("Hi")
    }
}