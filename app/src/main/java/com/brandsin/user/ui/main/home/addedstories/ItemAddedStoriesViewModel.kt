package com.brandsin.user.ui.main.home.addedstories

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.homepage.addedstories.liststories.DataItem

class ItemAddedStoriesViewModel(var item: DataItem) : BaseViewModel() {

    private var storyList: ArrayList<StoriesItem> = ArrayList()
    var storyAdapter = StoryAdapter()

    init {
        storyList = item.stories as ArrayList<StoriesItem>
        storyAdapter.updateList(storyList)
    }
}