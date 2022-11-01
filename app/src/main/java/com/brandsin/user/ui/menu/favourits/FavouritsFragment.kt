package com.brandsin.user.ui.menu.favourits

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentFavouritsBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.utils.storyviewer.StoryView

class FavouritsFragment : BaseHomeFragment(),StoryView.StoryViewListener, StoriesAdapter.OnStoryClickedListner
{
    private lateinit var binding : HomeFragmentFavouritsBinding
    private lateinit var favouritsViewModel: FavouritsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_favourits, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        favouritsViewModel = ViewModelProvider(this).get(FavouritsViewModel::class.java)
        binding.viewModel = favouritsViewModel

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            favouritsViewModel.getFavourits()
        }
        favouritsViewModel.setStoriesListner(this)

        setBarName(getString(R.string.favourits))
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
                    FavouritsFragmentDirections.favouritsToAddedStories(storiesItem.storeId!!,storiesItem)
                findNavController().navigate(action)
            }
        } else if (num == 2) {
            view?.post {
                val action =
                    FavouritsFragmentDirections.favouritsToStoreDetails(storiesItem.storeId!!)
                findNavController().navigate(action)
            }
        } else {
            view?.post {
                findNavController().navigateUp()
            }
        }
    }


}