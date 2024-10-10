package com.brandsin.user.ui.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentHomeBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.UserLocation
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.model.order.homenew.SlidesItem
import com.brandsin.user.model.order.homepage.ShopsItem
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.homepage.TagsItem
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.dialogs.chooselocation.DialogChooseLocationFragment
import com.brandsin.user.ui.location.address.ListAddressesActivity
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.MapUtil
import com.brandsin.user.utils.map.observe
import com.brandsin.user.utils.storyviewer.StoryView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import timber.log.Timber
import java.util.*

class HomeFragment : BaseHomeFragment(), Observer<Any?>, StoryView.StoryViewListener,
    StoriesAdapter.OnStoryClickedListener {

    private lateinit var binding: HomeFragmentHomeBinding

    lateinit var viewModel: HomeViewModel

    private val fragmentArgs: HomeFragmentArgs by navArgs()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    private lateinit var sliderView: SliderView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.setStoriesListener(this)

        viewModel.linearLayoutMng.set(true)
        viewModel.layoutManager.set(LinearLayoutManager(requireContext()))

        // requireContext().let { viewModel.setCurrentContext(it) }

        viewModel.categoryId = fragmentArgs.categoryId
        viewModel.categoryImg = fragmentArgs.categoryImg

        if (fragmentArgs.storeIds.isNullOrEmpty()) {

        } else {
            viewModel.storeIds = fragmentArgs.storeIds.toList() as ArrayList<String>
        }

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.storesAdapter.productsLiveData.observe(viewLifecycleOwner, this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.forLanguageTag(PrefMethods.getLanguage()))

        viewModel.showProgress().observe(viewLifecycleOwner) { aBoolean ->
            if (!aBoolean!!) {
                binding.progressLayout.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } else {
                binding.progressLayout.visibility = View.VISIBLE
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }
        }

        binding.ibBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.obsIsFull.set(false)
            viewModel.obsIsLoading.set(true)
            viewModel.getHomePage(viewModel.categoryId)
        }

        observe(viewModel.categoriesAdapter.categoriesLiveData) {
            when (it) {
                is CategoriesItem -> {
                    viewModel.obsHideRecycler.set(false)
                    viewModel.obsIsLoadingStores.set(true)
                    viewModel.getHomePage(it.categoryId.toString())
                }
            }
        }

        observe(viewModel.showStories) {
            if (it!!) {
                binding.layoutStories.visibility = View.VISIBLE
            } else {
                binding.layoutStories.visibility = View.GONE
            }
        }

        observe(viewModel.subCategoryAdapter.subCatLiveData) {
            when (it) {
                is TagsItem -> {
                    viewModel.setShowProgress(true)
                    binding.storeCat.text =
                        if (it.name.equals(getString(R.string.all_tags))) getText(R.string.all_stores) else it.name

                    if (it.id != null) {
                        viewModel.filterStores(it.id)
                        viewModel.getHomePageByTagId(viewModel.categoryId, it.id)
                    } else {
                        viewModel.getHomePage(viewModel.categoryId)
                    }
                }
            }
        }

        observe(viewModel.slidersResponse) {
            // setupSlider(it!!.data!!.slides)
        }

        observe(viewModel.moreSliderAdapter.moreSliderLiveData) {
            when (it) {
                is SlidesItem -> {
                    if (it.storeIdsArray != null && it.storeIdsArray.isNotEmpty()) {
                        if (it.storeIdsArray.size > 1) {
                            val action = HomeFragmentDirections.homeToSelf(
                                "",
                                "",
                                it.storeIdsArray.toTypedArray()
                            )
                            findNavController().navigate(action)
                        } else {
                            val action =
                                HomeFragmentDirections.homeToStoreDetails(it.storeIdsArray[0].toInt())
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }

        /*observe(viewModel.storiesAdapter.showLiveData) {
            when(it) {
                is StoriesItem -> {
                    val action = HomeFragmentDirections.homeToShowStory(it)
                    findNavController().navigate(action)
                }
            }
        }*/

        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result.token
                viewModel.deviceTokenRequest.token = token
                if (PrefMethods.getUserData() != null) {
                    viewModel.deviceToken()
                }

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
            })
        // [END retrieve_current_token]
    }


    private fun setupSlider(slides: List<SlidesItem>) {
        viewModel.moreSliderAdapter.updateList(slides)
        sliderView = requireActivity().findViewById(R.id.bannerSlider)
        sliderView.setSliderAdapter(viewModel.moreSliderAdapter)
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        sliderView.indicatorSelectedColor = Color.WHITE
        sliderView.indicatorUnselectedColor = Color.GRAY
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
    }

    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        when (num) {
            1 -> {
                view?.post {
                    val action =
                        HomeFragmentDirections.homeToAddedStories(
                            storiesItem.storeId!!,
                            storiesItem
                        )
                    findNavController().navigate(action)
                }
            }

            2 -> {
                view?.post {
                    val action =
                        HomeFragmentDirections.homeToStoreDetails(storiesItem.storeId!!)
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

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            is ShopsItem -> {
                val action = HomeFragmentDirections.homeToStoreDetails(value.id!!.toInt())
                findNavController().navigate(action)
            }

            Codes.EDIT_CLICKED -> {
                findNavController().navigate(R.id.home_to_profile)
            }

            Codes.CART_CLICKED -> {
                findNavController().navigate(R.id.home_to_cart)
            }

            Codes.SEARCH_CLICKED -> {
                if (viewModel.categoriesList.isNotEmpty()) {
                    val action = HomeFragmentDirections.homeToSearch(
                        "home",
                        viewModel.categoryId,
                        viewModel.homePageResponse,
                        viewModel.homeNewResponse,
                        StoreDetailsData()
                    )
                    findNavController().navigate(action)
                }
            }

            Codes.LOCATION_CLICKED -> {
                val bundle = Bundle()
                Utils.startDialogActivity(
                    requireActivity(),
                    DialogChooseLocationFragment::class.java.name,
                    Codes.DIALOG_CHOOSE_LOCATION,
                    bundle
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /* When This page opened navigate user to select his default address from delivery addresses list */
        when (requestCode) {
            Codes.SHOW_DELIVERY_ADDRESSES_CODE -> {
                when (data) {
                    null -> {
                        Timber.e("no changes detected")
                    }

                    else -> {
                        when {
                            data.hasExtra(Params.ADDRESS_ITEM) -> {
                                val addressItem =
                                    data.getParcelableExtra<AddressListItem>(Params.ADDRESS_ITEM)
                                PrefMethods.saveDefaultAddress(addressItem)
                                when {
                                    addressItem != null -> {
                                        viewModel.getUserAddress(viewModel.categoryId)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Codes.DIALOG_CHOOSE_LOCATION -> {
                if (data != null) {
                    when {
                        data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 0 -> {
                            obtieneLocalization()
                        }

                        data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                            startActivityForResult(
                                (Intent(requireActivity(), ListAddressesActivity::class.java))
                                    .putExtra(Params.DELIVERY_ADDRESSES_FLAG, 1),
                                Codes.SHOW_DELIVERY_ADDRESSES_CODE
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserStatus(viewModel.categoryId)
    }

    @SuppressLint("MissingPermission")
    private fun obtieneLocalization() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.latitude = location.latitude.toString()
                    viewModel.longitude = location.longitude.toString()
                    viewModel.obsAddress.set(
                        MapUtil.getLocationAddress(
                            getGeoCoder(),
                            location.latitude,
                            location.longitude
                        )
                    )
                }

                PrefMethods.saveUserLocation(
                    UserLocation(
                        lat = viewModel.latitude,
                        lng = viewModel.longitude,
                        address = viewModel.obsAddress.get()
                    )
                )

                val addressItem = AddressListItem()
                addressItem.lat = viewModel.latitude
                addressItem.lng = viewModel.longitude
                addressItem.streetName = viewModel.obsAddress.get()
                PrefMethods.saveDefaultAddress(addressItem)
            }
    }

    private fun getGeoCoder(): Geocoder {
        return geocoder
    }

    override fun onStoryClicked(
        position: Int,
        stories: MutableList<ArrayList<StoriesItem>>
    ) {
        val storyV = StoryView(position, stories)
        storyV.setStoryViewListener(this)
        storyV.show(childFragmentManager, "story")
    }
}