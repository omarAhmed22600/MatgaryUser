package com.brandsin.user.ui.menu.favourits

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
import com.brandsin.user.databinding.HomeFragmentFavouritsBinding
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homepage.HomePageResponse
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.home.HomeFragmentDirections
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.menu.discover.DiscoverFragmentDirections
import com.brandsin.user.ui.menu.favourits.adapter.FavoriteStoriesAdapter
import com.brandsin.user.ui.menu.favourits.storyView.FavoriteStoryView
import com.brandsin.user.utils.storyviewer.StoryView
import timber.log.Timber

class FavoriteFragment : BaseHomeFragment(), /*FavoriteStoryView.FavoriteStoryViewListener*/ StoryView.StoryViewListener,
    StoriesAdapter.OnStoryClickedListener {

    private lateinit var binding: HomeFragmentFavouritsBinding
    private lateinit var viewModel: FavouritesViewModel

    private lateinit var favoriteStoriesAdapter: FavoriteStoriesAdapter

    private var isSeen: Int = 0

   /* private val onItemClickCallBack: (position: Int, storiesItem: List<StoriesItem?>?) -> Unit =
        { position, storyItem ->
            // val bundle = Bundle()
            // bundle.putInt("STORY_ID", storyItem.id ?: 0)
            // findNavController().navigate(R.id.storyDetailsFragment, bundle)

            val storyV = FavoriteStoryView(position, storyItem)
            storyV.setStoryViewListener(this)
            storyV.show(childFragmentManager, "story")
        }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment_favourits, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[FavouritesViewModel::class.java]
        binding.viewModel = viewModel

        favoriteStoriesAdapter = FavoriteStoriesAdapter(this)
        setBarName(getString(R.string.favorites))

        viewModel.getFavourites(isSeen = 0)

        initRecyclerView()
        setBtnListener()
        subscribeData()
    }

    private fun initRecyclerView() {
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvCategories.apply {
            layoutManager = staggeredGridLayoutManager
            adapter = favoriteStoriesAdapter
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setBtnListener() {
        binding.newStories.setOnClickListener {
            isSeen = 0
            binding.newStories.setBackgroundResource(R.drawable.bg_selected_text_search)
            binding.newStories.setTextColor(R.color.color_primary)
            binding.viewedStories.setTextColor(R.color.black)
            binding.viewedStories.setBackgroundResource(R.drawable.bg_unselected_text_search)
            viewModel.getFavourites(isSeen)
        }

        binding.viewedStories.setOnClickListener {
            isSeen = 1
            binding.newStories.setBackgroundResource(R.drawable.bg_unselected_text_search)
            binding.newStories.setTextColor(R.color.black)
            binding.viewedStories.setTextColor(R.color.color_primary)
            binding.viewedStories.setBackgroundResource(R.drawable.bg_selected_text_search)
            viewModel.getFavourites(isSeen)
        }

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getFavourites(isSeen)
        }

        binding.ibSearch.setOnClickListener {
            val action = FavoriteFragmentDirections.navFavoritesToNavSearch(
                "homenew",
                "",
                HomePageResponse(),
                HomeNewResponse(),
                StoreDetailsData()
            )
            findNavController().navigate(action)
        }

        binding.cvCartLayout.setOnClickListener {
            findNavController().navigate(R.id.nav_favorites_to_nav_cart)
        }
    }

    private fun subscribeData() {
        viewModel.getFavouritesListResponse.observe(viewLifecycleOwner) {
            Timber.e("is $it")
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    if (it.data?.stories.orEmpty().isNotEmpty()) {
                        viewModel.obsIsFull.set(true)
                        viewModel.obsIsLoadingStores.set(false)
                        viewModel.obsHideRecycler.set(true)
                        Timber.e("response is $it")
                        favoriteStoriesAdapter.updateList(it.data?.stories.orEmpty())
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

    override fun onResume() {
        super.onResume()
        viewModel.getUserStatus()
    }
    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        when (num) {
            1 -> {
                view?.post {
                    val action =
                        FavoriteFragmentDirections.favouritsToAddedStories(
                            storiesItem.storeId!!,
                            storiesItem
                        )
                    findNavController().navigate(action)
                }
            }

            2 -> {
                view?.post {
                    val action =
                        FavoriteFragmentDirections.favouritsToStoreDetails(storiesItem.storeId!!)
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

/*
    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        when (num) {
            1 -> {
                view?.post {
                    val action =
                        FavoriteFragmentDirections.favouritsToAddedStories(
                            storiesItem.storeId!!,
                            storiesItem
                        )
                    findNavController().navigate(action)
                }
            }

            2 -> {
                view?.post {
                    val action =
                        FavoriteFragmentDirections.favouritsToStoreDetails(storiesItem.storeId!!)
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
*/

    override fun onStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>) {
        val storyV = StoryView(position, stories)
        storyV.setStoryViewListener(this)
        storyV.show(childFragmentManager, "story")
    }
}