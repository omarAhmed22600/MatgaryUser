package com.brandsin.user.ui.dialogs.filter

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.TagsItem

class ItemFilterViewModel (var itemTags: TagsItem) : BaseViewModel() {

    fun onFilterTagClicked() {
        setValue("hi")
    }
}