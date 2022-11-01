package com.brandsin.user.ui.main.homenew.more

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homenew.SectionsItem
import com.brandsin.user.model.order.homenew.SlidesItem
import com.brandsin.user.model.order.homenew.StoresDataItem
import com.brandsin.user.ui.main.homenew.moreslider.MoreSliderAdapter
import com.brandsin.user.ui.main.homenew.moresub.MoreSubAdapter

class ItemMoreViewModel (var itemMore: SectionsItem) : BaseViewModel() {

    var moreSubAdapter= MoreSubAdapter()
    var moreSubList  = mutableListOf<StoresDataItem>()

    var moreSliderAdapter= MoreSliderAdapter()
    var moreSliderList  = mutableListOf<SlidesItem>()

}