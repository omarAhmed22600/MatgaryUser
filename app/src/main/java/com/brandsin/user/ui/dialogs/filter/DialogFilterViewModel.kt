package com.brandsin.user.ui.dialogs.filter

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.TagsItem
import java.util.ArrayList

class DialogFilterViewModel : BaseViewModel()
{

    var filterAdapter=FilterAdapter()
    var tagsList  = mutableListOf<TagsItem>()
    var tagsID  = ArrayList<Int>()
    var sort = ""

    fun getFilter() {

        obsIsEmpty.set(false)
        obsIsLoading.set(false)
        obsIsFull.set(true)
        obsIsLoadingStores.set(false)
        obsHideRecycler.set(true)

        /* Filter List */
        filterAdapter.updateList(tagsList,tagsID)
    }
}