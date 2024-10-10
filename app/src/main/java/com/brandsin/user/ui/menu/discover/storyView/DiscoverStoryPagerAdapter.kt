package com.brandsin.user.ui.menu.discover.storyView

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.utils.storyviewer.PageViewOperator

class DiscoverStoryPagerAdapter(
    private var pageViewOperator: PageViewOperator,
    fragmentManager: FragmentManager,
    private var storyList: StoriesItem
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment =
        DiscoverStoryDisplayFragment.newInstance(pageViewOperator, position, storyList)

    override fun getCount(): Int {
        return 1
    }

    fun findFragmentByPosition(viewPager: ViewPager, position: Int): Fragment? {
        try {
            val f = instantiateItem(viewPager, position)
            return f as? Fragment
        } finally {
            finishUpdate(viewPager)
        }
    }
}