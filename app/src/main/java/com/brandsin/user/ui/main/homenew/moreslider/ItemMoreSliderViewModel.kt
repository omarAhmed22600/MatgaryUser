package com.brandsin.user.ui.main.homenew.moreslider

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homenew.SlidesItem

class ItemMoreSliderViewModel (var item: SlidesItem) : BaseViewModel() {

    fun onSliderClicked() {
        setValue("hi")
    }

}