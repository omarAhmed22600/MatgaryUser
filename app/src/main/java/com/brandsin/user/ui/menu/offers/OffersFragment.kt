package com.brandsin.user.ui.menu.offers

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentOffersBinding
import com.brandsin.user.model.menu.offers.OffersItemDetails
import com.brandsin.user.model.order.homenew.SlidesItem
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.utils.map.observe
import com.brandsin.user.utils.storyviewer.StoryView
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class OffersFragment : BaseHomeFragment(), Observer<Any?>, StoryView.StoryViewListener,
    StoriesAdapter.OnStoryClickedListener {

    private lateinit var binding: HomeFragmentOffersBinding

    private lateinit var offersViewModel: OffersViewModel

    private lateinit var sliderView: SliderView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_offers, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.offers))

        offersViewModel = ViewModelProvider(this)[OffersViewModel::class.java]
        binding.viewModel = offersViewModel

        offersViewModel.getOffersStories()
        offersViewModel.getSlider("offers")
        offersViewModel.getOffers()

        offersViewModel.setStoriesListener(this)

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            offersViewModel.getOffersStories()
            offersViewModel.getSlider("offers")
            offersViewModel.getOffers()
        }

        offersViewModel.offersAdapter.offersLiveData.observe(viewLifecycleOwner, this)

        observe(offersViewModel.slidersResponse) {
            setupSlider(it!!.data!!.slides)
        }

        observe(offersViewModel.moreSliderAdapter.moreSliderLiveData) {
            when (it) {
                is SlidesItem -> {
                    if (it.storeIdsArray != null && it.storeIdsArray.isNotEmpty()) {
                        if (it.storeIdsArray.size > 1) {
                            val action = OffersFragmentDirections.offersToHome(
                                "",
                                "",
                                it.storeIdsArray.toTypedArray()
                            )
                            findNavController().navigate(action)
                        } else {
                            val action =
                                OffersFragmentDirections.offersToStoreDetails(it.storeIdsArray[0].toInt())
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun setupSlider(slides: List<SlidesItem>) {
        offersViewModel.moreSliderAdapter.updateList(slides)
        sliderView = requireActivity().findViewById(R.id.bannerSlider)
        sliderView.setSliderAdapter(offersViewModel.moreSliderAdapter)
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        sliderView.indicatorSelectedColor = Color.WHITE
        sliderView.indicatorUnselectedColor = Color.GRAY
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            is OffersItemDetails -> {
                val action = OffersFragmentDirections.offersToOfferDetails(value)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        when (num) {
            1 -> {
                view?.post {
                    val action =
                        OffersFragmentDirections.offersToAddedStories(
                            storiesItem.storeId!!,
                            storiesItem
                        )
                    findNavController().navigate(action)
                }
            }
            2 -> {
                view?.post {
                    val action =
                        OffersFragmentDirections.offersToStoreDetails(storiesItem.storeId!!)
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
        val storyView = StoryView(position, stories)
        storyView.setStoryViewListener(this)
        storyView.show(childFragmentManager, "story")
    }
}