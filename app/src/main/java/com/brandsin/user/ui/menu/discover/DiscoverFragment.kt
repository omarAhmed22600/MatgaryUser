package com.brandsin.user.ui.menu.discover

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentDiscoverBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.utils.storyviewer.StoryView

class DiscoverFragment : BaseHomeFragment(),StoryView.StoryViewListener, StoriesAdapter.OnStoryClickedListner
{
    private lateinit var binding : HomeFragmentDiscoverBinding
    private lateinit var ViewModel: DiscoverViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_discover, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        ViewModel = ViewModelProvider(this).get(DiscoverViewModel::class.java)
        binding.viewModel = ViewModel

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            ViewModel.getDiscover()
        }
        ViewModel.setStoriesListner(this)

        setBarName(getString(R.string.discover))
    }

    override fun onStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>) {
        var storyv: StoryView = StoryView(position,stories)
        storyv.setStoryViewListener(this)
        storyv.show(childFragmentManager,"story")
    }

    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        if (num == 1) {
            view?.post {
                val action =
                    DiscoverFragmentDirections.discoverToAddedStories(storiesItem.storeId!!,storiesItem)
                findNavController().navigate(action)
            }
        } else if (num == 2) {
            view?.post {
                val action =
                    DiscoverFragmentDirections.discoverToStoreDetails(storiesItem.storeId!!)
                findNavController().navigate(action)
            }
        } else {
            view?.post {
                findNavController().navigateUp()
            }
        }
    }


}