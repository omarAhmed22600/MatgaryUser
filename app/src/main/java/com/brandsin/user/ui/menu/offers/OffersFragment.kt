package com.brandsin.user.ui.menu.offers

import android.graphics.Color
import android.os.Bundle
import android.view.*
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

class OffersFragment : BaseHomeFragment(), Observer<Any?>, StoryView.StoryViewListener, StoriesAdapter.OnStoryClickedListner
{
    private lateinit var binding : HomeFragmentOffersBinding
    private lateinit var favouritsViewModel: OffersViewModel
    lateinit var sliderView : SliderView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_offers, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        favouritsViewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        binding.viewModel = favouritsViewModel
        favouritsViewModel.setStoriesListner(this)
        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            favouritsViewModel.getOffersStories()
            favouritsViewModel.getSlider("offers")
            favouritsViewModel.getOffers()
        }
        favouritsViewModel.offersAdapter.offersLiveData.observe(viewLifecycleOwner, this)

        observe(favouritsViewModel.slidersResponse) {

            setupSlider(it!!.data!!.slides)

        }

        observe(favouritsViewModel.moreSliderAdapter.moreSliderLiveData) {
            when(it) {
                is SlidesItem -> {
                    if (it.storeIdsArray!=null && it.storeIdsArray.isNotEmpty()){
                        if(it.storeIdsArray.size > 1) {
                            val action = OffersFragmentDirections.offersToHome("", "", it.storeIdsArray.toTypedArray())
                            findNavController().navigate(action)
                        }else{
                            val action = OffersFragmentDirections.offersToStoreDetails(it.storeIdsArray[0]!!.toInt())
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
        favouritsViewModel.getOffersStories()
        favouritsViewModel.getSlider("offers")
        favouritsViewModel.getOffers()
        setBarName(getString(R.string.offers))
    }

    private fun setupSlider(slides: List<SlidesItem>) {

        favouritsViewModel.moreSliderAdapter.updateList(slides)
        sliderView = requireActivity().findViewById(R.id.bannerSlider)
        sliderView.setSliderAdapter(favouritsViewModel.moreSliderAdapter)
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        sliderView.indicatorSelectedColor = Color.WHITE
        sliderView.indicatorUnselectedColor = Color.GRAY
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
    }

    override fun onChanged(it: Any?)
    {
        if(it == null) return
        when (it)
        {
            is OffersItemDetails -> {
                val action = OffersFragmentDirections.offersToOfferDetails(it)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        if (num == 1) {
            view?.post {
                val action =
                    OffersFragmentDirections.offersToAddedStories(storiesItem.storeId!!,storiesItem)
                findNavController().navigate(action)
            }
        } else if (num == 2) {
            view?.post {
                val action =
                    OffersFragmentDirections.offersToStoreDetails(storiesItem.storeId!!)
                findNavController().navigate(action)
            }
        } else {
            view?.post {
                findNavController().navigateUp()
            }
        }
    }

    override fun onStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>) {
        var storyv: StoryView = StoryView(position,stories)
        storyv.setStoryViewListener(this)
        storyv.show(childFragmentManager,"story")
    }
}