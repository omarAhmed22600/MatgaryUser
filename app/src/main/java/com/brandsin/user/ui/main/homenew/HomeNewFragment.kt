package com.brandsin.user.ui.main.homenew

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentHomeNewBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.UserLocation
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.model.order.homenew.PopupsItem
import com.brandsin.user.model.order.homenew.SlidesItem
import com.brandsin.user.model.order.homenew.StoresDataItem
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.dialogs.chooselocation.DialogChooseLocationFragment
import com.brandsin.user.ui.dialogs.homepopup.DialogHomePopupFragment
import com.brandsin.user.ui.location.address.ListAddressesActivity
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.MapUtil
import com.brandsin.user.utils.map.observe
import com.brandsin.user.utils.storyviewer.StoryView
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import timber.log.Timber
import java.util.*

class HomeNewFragment : BaseHomeFragment(), Observer<Any?>,StoryView.StoryViewListener, StoriesAdapter.OnStoryClickedListner
{
    lateinit var viewModel: HomeNewViewModel
    private lateinit var binding: HomeFragmentHomeNewBinding
    var categoryId = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    lateinit var sliderView : SliderView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_home_new, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeNewViewModel::class.java)
        binding.viewModel = viewModel

        //viewModel.obsHideRecycler.set(false)
        viewModel.getSlider("homepage")

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.setStoriesListner(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.forLanguageTag(PrefMethods.getLanguage()))

//        binding.ibMenu.setOnClickListener {
//            (activity as HomeActivity).showDrawer()
//        }

        if (PrefMethods.getUserData() == null) {
            binding.text.text = MyApp.context.getString(R.string.what_do_you_want_to_order)
        } else {
            if (PrefMethods.getUserData()!!.name.toString()!="null") {
                binding.text.text = MyApp.context.getString(R.string.what_do_you_want_to_order)+PrefMethods.getUserData()!!.name.toString()
            }else{
                binding.text.text = MyApp.context.getString(R.string.what_do_you_want_to_order)
            }
        }
        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.obsHideRecycler.set(false)
            viewModel.obsIsFull.set(false)
            viewModel.obsIsLoading.set(true)
            viewModel.getHomePage()
        }

        observe(viewModel.categoriesAdapter.categoriesLiveData) {
            when(it) {
                is CategoriesItem -> {
                    categoryId = it.categoryId!!.toString()
                    val action = HomeNewFragmentDirections.homeNewToHome(categoryId,it.img.toString(),
                        emptyArray()
                    )
                    findNavController().navigate(action)
                }
            }
        }


        observe(viewModel.adsAdapter.adsLiveData) {
            when(it) {
                is String -> {
//                    findNavController().navigate(R.id.home_new_to_home)
                }
            }
        }

        observe(viewModel.moreAdapter.moresubLiveData) {
            when(it) {
                is StoresDataItem -> {
                    val action = HomeNewFragmentDirections.homeNewToStoreDetails(it.id!!.toInt())
                    findNavController().navigate(action)
                }
            }
        }

        observe(viewModel.moreAdapter.moreSliderLiveData) {
            when(it) {
                is SlidesItem -> {
                    if (it.storeIdsArray!=null && it.storeIdsArray.isNotEmpty()){
                        if(it.storeIdsArray.size > 1) {
                            val action = HomeNewFragmentDirections.homeNewToHome("", "", it.storeIdsArray.toTypedArray())
                            findNavController().navigate(action)
                        }else{
                            val action = HomeNewFragmentDirections.homeNewToStoreDetails(it.storeIdsArray[0]!!.toInt())
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }

        observe(viewModel.slidersResponse) {

            setupSlider(it!!.data!!.slides)

        }




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

        if (((activity as HomeActivity).orderId != -1)&&((activity as HomeActivity).orderId != 0)){
            val action = HomeNewFragmentDirections.homeNewToOrderDetails((activity as HomeActivity).orderId)
            findNavController().navigate(action)
        }
    }

    override fun onChanged(it: Any?)
    {
        if(it == null) return
        when (it) {
            Codes.EDIT_CLICKED -> {
                findNavController().navigate(R.id.home_new_to_profile)
            }
            Codes.CART_CLICKED -> {
                findNavController().navigate(R.id.home_new_to_cart)
            }
            Codes.SEARCH_CLICKED -> {
                if (!viewModel.categoriesList.isNullOrEmpty()) {
                    val action = HomeNewFragmentDirections.homeNewToSearch("homenew",categoryId,viewModel.homePageResponse,viewModel.homeNewResponse,
                        StoreDetailsData()
                    )
                    findNavController().navigate(action)
                }
            }
            Codes.LOCATION_CLICKED -> {
                val bundle = Bundle()
                Utils.startDialogActivity(requireActivity(), DialogChooseLocationFragment::class.java.name, Codes.DIALOG_CHOOSE_LOCATION, bundle)
            }
            Codes.SHOW_POPUP -> {
                if (PrefMethods.getHomePopup()) {
                    val bundle = Bundle()
                    bundle.putSerializable(Params.DIALOG_HOME_POPUP, viewModel.homeNewResponse)
                    Utils.startDialogActivity(requireActivity(), DialogHomePopupFragment::class.java.name, Codes.DIALOG_HOME_POPUP, bundle)
                }
            }
        }
    }

//    override fun onPause() {
//        super.onPause()
//        (activity as HomeActivity).lockDrawer()
//    }

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
                                val addressItem = data.getParcelableExtra<AddressListItem>(Params.ADDRESS_ITEM)
                                PrefMethods.saveDefaultAddress(addressItem)
                                when {
                                    addressItem != null -> {
                                        viewModel.getUserAddress()
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
                            obtieneLocalizacion()
                        }
                        data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                            startActivityForResult((Intent(requireActivity(), ListAddressesActivity::class.java))
                                .putExtra(Params.DELIVERY_ADDRESSES_FLAG , 1), Codes.SHOW_DELIVERY_ADDRESSES_CODE)
                        }
                    }
                }
            }

            Codes.DIALOG_HOME_POPUP -> {
                when {
                    data != null -> {
                        when {
                            data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                                when {
                                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                        val sliderItem =
                                            data.getSerializableExtra(Params.DIALOG_HOME_POPUP) as PopupsItem
                                        if (sliderItem.storeId!=null){
//                                            if(sliderItem.storeIds.size > 1) {
//                                                val action = HomeNewFragmentDirections.homeNewToHome("", "", sliderItem.storeIds.toTypedArray())
//                                                findNavController().navigate(action)
//                                            }else{
                                                val action = HomeNewFragmentDirections.homeNewToStoreDetails(sliderItem.storeId)
                                                findNavController().navigate(action)
                                            //}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserStatus()
    }
    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                viewModel.latitude = location.latitude.toString()
                viewModel.longitude = location.longitude.toString()
                    viewModel.obsAddress.set(MapUtil.getLocationAddress(getGeoCoder(), location.latitude, location.longitude))
                }

                PrefMethods.saveUserLocation(
                    UserLocation(
                        lat = viewModel.latitude,
                        lng = viewModel.longitude,
                        address =  viewModel.obsAddress.get()))

                val addressItem = AddressListItem()
                addressItem.lat = viewModel.latitude
                addressItem.lng = viewModel.longitude
                addressItem.streetName =  viewModel.obsAddress.get()
                PrefMethods.saveDefaultAddress(addressItem)
            }
    }
    fun getGeoCoder(): Geocoder {
        return geocoder
    }

    override fun onDoneClicked(num:Int,storiesItem: StoriesItem) {
        if (num == 1) {
            view?.post {
                val action =
                    HomeNewFragmentDirections.homeNewToAddedStories(storiesItem.storeId!!,storiesItem)
                findNavController().navigate(action)
            }
        } else if (num == 2) {
            view?.post {
                val action =
                    HomeNewFragmentDirections.homeNewToStoreDetails(storiesItem.storeId!!)
                findNavController().navigate(action)
            }
        } else {
            view?.post {
                findNavController().navigateUp()
            }
        }
    }

    override fun onStoryClicked(position: Int,stories:MutableList<ArrayList<StoriesItem>>) {
        var storyv: StoryView = StoryView(position,stories)
        storyv.setStoryViewListener(this)
        storyv.show(childFragmentManager,"story")
    }

    private fun setupSlider(sliders: List<SlidesItem?>?) {
        viewModel.moreSliderAdapter.updateList(sliders as List<SlidesItem>)
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
}