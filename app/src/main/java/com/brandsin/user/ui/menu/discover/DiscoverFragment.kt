package com.brandsin.user.ui.menu.discover

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentDiscoverBinding
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homepage.HomePageResponse
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.home.HomeFragmentDirections
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.menu.discover.storyView.DiscoverStoryView
import com.brandsin.user.ui.menu.favourits.adapter.FavoriteStoriesAdapter
import com.brandsin.user.utils.storyviewer.StoryView

class DiscoverFragment : BaseHomeFragment(), /*DiscoverStoryView.DiscoverStoryViewListener*/ StoryView.StoryViewListener,
    StoriesAdapter.OnStoryClickedListener {

    private lateinit var binding: HomeFragmentDiscoverBinding

    private lateinit var viewModel: DiscoverViewModel

    private lateinit var discoverStoriesAdapter: DiscoverStoriesAdapter

    private var isSeen: Int = 0

   /* private val onItemClickCallBack: (position: Int, storiesList: StoriesItem) -> Unit =
        { position, storiesList ->
            val storyV = DiscoverStoryView(position, storiesList)
            storyV.setStoryViewListener(this)
            storyV.show(childFragmentManager, "story")
        }
*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment_discover, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.discover))
        discoverStoriesAdapter = DiscoverStoriesAdapter(this)

        viewModel = ViewModelProvider(this)[DiscoverViewModel::class.java]
        binding.viewModel = viewModel

        // viewModel.setStoriesListener(this)

        viewModel.getLatLng()
        viewModel.getStoriesList(isSeen = 0)

        initRecyclerView()
        setBtnListener()
        subscribeData()
    }

    private fun initRecyclerView() {
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvCategories.apply {
            layoutManager = staggeredGridLayoutManager
            adapter = discoverStoriesAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getLatLng()
        viewModel.getUserStatus()
    }


    @SuppressLint("ResourceAsColor")
    private fun setBtnListener() {
        binding.newStories.setOnClickListener {
            isSeen = 0
            binding.newStories.setBackgroundResource(R.drawable.bg_selected_text_search)
            binding.newStories.setTextColor(R.color.color_primary)
            binding.viewedStories.setTextColor(R.color.black)
            binding.viewedStories.setBackgroundResource(R.drawable.bg_unselected_text_search)
            viewModel.getStoriesList(isSeen)
        }

        binding.viewedStories.setOnClickListener {
            isSeen = 1
            binding.newStories.setBackgroundResource(R.drawable.bg_unselected_text_search)
            binding.newStories.setTextColor(R.color.black)
            binding.viewedStories.setTextColor(R.color.color_primary)
            binding.viewedStories.setBackgroundResource(R.drawable.bg_selected_text_search)
            viewModel.getStoriesList(isSeen)
        }

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            // viewModel.getDiscover()
            if (isSeen == 1) {
                viewModel.getStoriesList(isSeen)
            } else {
                viewModel.getStoriesList(isSeen)
            }
        }

        binding.ibSearch.setOnClickListener {
            val action = DiscoverFragmentDirections.discoverToNavSearch(
                "homenew",
                "",
                HomePageResponse(),
                HomeNewResponse(),
                StoreDetailsData()
            )
            findNavController().navigate(action)
        }

        binding.cvCartLayout.setOnClickListener {
            findNavController().navigate(R.id.nav_cart)
        }
    }

    /*override fun onStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>) {
        val storyV = StoryView(position, stories)
        storyV.setStoryViewListener(this)
        storyV.show(childFragmentManager, "story")
    }*/

    private fun subscribeData() {
        viewModel.getStoriesListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    if (it.data?.stories?.isNotEmpty() == true) {
                        viewModel.obsIsEmpty.set(false)
                        viewModel.obsIsFull.set(true)
                        viewModel.obsIsLoadingStores.set(false)
                        viewModel.obsHideRecycler.set(true)
                        discoverStoriesAdapter.updateList(it.data.stories)
                    } else {
                        viewModel.obsIsEmpty.set(true)
                        viewModel.obsIsFull.set(false)
                    }
                }

                is ResponseHandler.Error -> {
                    // show error message
                    viewModel.obsIsEmpty.set(true)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }
    }

    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        when (num) {
            1 -> {
                view?.post {
                    val action =
                        DiscoverFragmentDirections.discoverToAddedStories(
                            storiesItem.storeId!!,
                            storiesItem
                        )
                    findNavController().navigate(action)
                }
            }

            2 -> {
                view?.post {
                    val action =
                        DiscoverFragmentDirections.discoverToStoreDetails(storiesItem.storeId!!)
                    findNavController().navigate(action)
                }
            }

            else -> {
                view?.post {
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>) {
        val storyV = StoryView(position, stories)
        storyV.setStoryViewListener(this)
        storyV.show(childFragmentManager, "story")
    }
}