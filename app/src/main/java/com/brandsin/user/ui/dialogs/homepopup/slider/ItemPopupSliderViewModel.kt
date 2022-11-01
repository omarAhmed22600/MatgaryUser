package com.brandsin.user.ui.dialogs.homepopup.slider

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homenew.PopupsItem

class ItemPopupSliderViewModel (var item: PopupsItem) : BaseViewModel()
{
    fun onSliderClicked() {
        setValue("hi")
    }
}