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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentHomeNewBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.UserLocation
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.menu.offers.OffersItemDetails
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartParcelableClass
import com.brandsin.user.model.order.cart.CartStoreData
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.homenew.BrandItem
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.model.order.homenew.GiftItem
import com.brandsin.user.model.order.homenew.PopupsItem
import com.brandsin.user.model.order.homenew.SlidesItem
import com.brandsin.user.model.order.homenew.StoresDataItem
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.dialogs.chooselocation.DialogChooseLocationFragment
import com.brandsin.user.ui.dialogs.confirm.DialogConfirmFragment
import com.brandsin.user.ui.dialogs.homepopup.DialogHomePopupFragment
import com.brandsin.user.ui.location.address.ListAddressesActivity
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.OrderAddonsActivity
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.DialogOrderAddonsFragment
import com.brandsin.user.utils.MyApp
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
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class HomeNewFragment : BaseHomeFragment(), Observer<Any?>, StoryView.StoryViewListener,
    StoriesAdapter.OnStoryClickedListener {

    private lateinit var binding: HomeFragmentHomeNewBinding

    private lateinit var viewModel: HomeNewViewModel
    // private lateinit var storeViewModel: StoreDetailsViewModel

    private var categoryId = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    private lateinit var sliderView: SliderView

    private var cartData: UserCart? = null

    var cartItemsList: ArrayList<CartItem>? = null

    var storeData: CartStoreData? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment_home_new, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // storeViewModel = ViewModelProvider(requireActivity())[StoreDetailsViewModel::class.java]
        //  ViewModelProvider(requireActivity()).get(StoreDetailsViewModel::class.java)
        viewModel = ViewModelProvider(this)[HomeNewViewModel::class.java]
        binding.viewModel = viewModel

        // viewModel.obsHideRecycler.set(false)
        viewModel.getSlider("homepage")

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.setStoriesListener(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.forLanguageTag(PrefMethods.getLanguage()))
        Timber.e("arrived at hom new frag")
        val chatId = arguments?.getInt("chat_id")
        if (chatId != null)
        {
            Timber.e("recieved data $chatId")
            findNavController().navigate(R.id.inboxFragment)
        }
        //       binding.ibMenu.setOnClickListener {
        //          (activity as HomeActivity).showDrawer()
        //      }

        if (PrefMethods.getUserData() == null) {
            binding.text.text = MyApp.context.getString(R.string.what_do_you_want_to_order)
        } else {
            if (PrefMethods.getUserData()!!.name.toString() != "null") {
                binding.text.text =
                    MyApp.context.getString(R.string.what_do_you_want_to_order) + PrefMethods.getUserData()!!.name.toString()
            } else {
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
            when (it) {
                is CategoriesItem -> {
                    categoryId = it.categoryId!!.toString()
                    Timber.e("id:$categoryId")
                    val action = HomeNewFragmentDirections.homeNewToHome(
                        categoryId, it.thumbnail.toString(),
                        emptyArray()
                    )
                    findNavController().navigate(action)
                }
            }
        }

        observe(viewModel.moreAdapter.categoriesClickedLiveData) {
            when (it) {
                is CategoriesItem -> {
                    categoryId = it.categoryId!!.toString()
                    Timber.e("id:$categoryId")
                    val action = HomeNewFragmentDirections.homeNewToHome(
                        categoryId, it.thumbnail.toString(),
                        emptyArray()
                    )
                    findNavController().navigate(action)
                }
            }
        }

        observe(viewModel.adsAdapter.adsLiveData) {
            when (it) {
                is String -> {
                    // findNavController().navigate(R.id.home_new_to_home)
                }
            }
        }

        observe(viewModel.moreAdapter.moreSubLiveData) {
            when (it) {
                is StoresDataItem -> {
                    val action =
                        HomeNewFragmentDirections.homeNewToStoreDetails(it.id ?: 0)
                    findNavController().navigate(action)
                }
            }
        }

        observe(viewModel.moreAdapter.moreOfferLiveData) {
            when (it) {
                is OffersItemDetails -> {
                    val bundle = Bundle()
                    bundle.putParcelable("OFFER_ITEM", it)
                    findNavController().navigate(R.id.nav_offer_details, bundle)

                    // val action = OffersFragmentDirections.offersToOfferDetails(value)
                    // findNavController().navigate(action)
                }
            }
        }

        // listen when click in more brands button
        observe(viewModel.moreAdapter.moreBrandsLiveData) {
            when (it) {
                is Int -> {
                    // val action = HomeNewFragmentDirections.homeNewToStoreDetails(it.id!!.toInt())
                    val bundle = Bundle()
                    bundle.putInt("SECTION_ID", it) // it -> sectionId
                    findNavController().navigate(R.id.brandsFragment, bundle)
                }
            }
        }

        // listen when click in more brands button
        observe(viewModel.moreAdapter.brandItemLiveData) {
            when (it) {
                is BrandItem -> {
                    // val action = HomeNewFragmentDirections.homeNewToStoreDetails(it.id!!.toInt())
                    // val bundle = Bundle()
                    // bundle.putInt("BRAND_ID", it.id ?: 0) // it -> sectionId
                    // findNavController().navigate(R.id.brandsFragment, bundle)
                    val action = HomeNewFragmentDirections.homeNewToSearch(
                        "brand",
                        it.id.toString(),
                        viewModel.homePageResponse,
                        viewModel.homeNewResponse,
                        StoreDetailsData()
                    )
                    findNavController().navigate(action)
                }
            }
        }

        // listen when click in product item
        observe(viewModel.moreAdapter.moreProductLiveData) {
            when (it) {
                is StoreProductItem -> {
                    when (it.type) {
                        "simple" -> {
                            val bundle = Bundle()
                            bundle.putParcelable(Params.STORE_PRODUCT_ITEM, it)
                            Utils.startDialogActivity(
                                requireActivity(),
                                DialogOrderAddonsFragment::class.java.name,
                                Codes.SELECT_ORDER_ADDONS_DIALOG,
                                bundle
                            )
                        }

                        "variable" -> {
                            val intent = Intent(requireActivity(), OrderAddonsActivity::class.java)
                            intent.putExtra(Params.STORE_PRODUCT_ITEM, it)
                            intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                            startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                        }
                    }
                }
            }
        }

        // listen when click in product item
        observe(viewModel.moreAdapter.moreGiftLiveData) { giftItem ->
            when {
                PrefMethods.getUserData() != null -> {
                    if (PrefMethods.getUserCart() != null) {

                        cartData = UserCart()
                        cartItemsList = ArrayList() // ArrayList<CartItem>()
                        storeData = CartStoreData()

                        cartData = PrefMethods.getUserCart()!!

                        cartItemsList = cartData!!.cartItems!!
                        storeData = cartData!!.cartStoreData!!

                        when {
                            storeData!!.storeId != null -> {
                                when {
                                    // Cart is Not empty But the saved products NOT has the same store Id >> Clear the cart before adding new products
                                    storeData!!.storeId != giftItem?.storeId -> {
                                        PrefMethods.getUserCart()?.cartItems?.clear()
                                        viewModel.setValue(Codes.CLEAR_CART)
                                    }

                                    else -> {
                                        // Cart is Not empty and the all products has the sam Id
                                        addGiftToCart(giftItem)
                                    }
                                }
                            }
                        }
                    } else {
                        cartData = UserCart()
                        cartItemsList = ArrayList() // ArrayList<CartItem>()
                        storeData = CartStoreData()

                        addGiftToCart(giftItem)
                    }
                }

                else -> {
                    viewModel.setValue(Codes.NOT_LOGIN)
                }
            }
        }

        observe(viewModel.moreAdapter.moreSliderLiveData) {
            when (it) {
                is SlidesItem -> {
                    if (!it.storeIdsArray.isNullOrEmpty()) {
                        if (it.storeIdsArray.size > 1) {
                            val action = HomeNewFragmentDirections.homeNewToHome(
                                "",
                                "",
                                it.storeIdsArray.toTypedArray()
                            )
                            findNavController().navigate(action)
                        } else {
                            val action =
                                HomeNewFragmentDirections.homeNewToStoreDetails(it.storeIdsArray[0].toInt())
                            findNavController().navigate(action)
                        }
                    } else if (it.type == "favorites") {
                        findNavController().navigate(R.id.nav_favorites)

                    } else if (it.type == "discover") {
                        findNavController().navigate(R.id.nav_discover)
                    }
                }
            }
        }

        observe(viewModel.slidersResponse) {
            setupSlider(it?.data?.slides)
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

        if (((activity as HomeActivity).orderId != -1) && ((activity as HomeActivity).orderId != 0)) {
            val action =
                HomeNewFragmentDirections.homeNewToOrderDetails((activity as HomeActivity).orderId)
            findNavController().navigate(action)
        }
    }

    private fun addGiftToCart(giftItem: GiftItem?) {
        when {
            // Cart is empty
            cartItemsList?.isEmpty() == true -> {
                cartItemsList!!.add(getItemCart(giftItem))

                storeData?.run {
                    storeId = giftItem?.storeId
                    storeName = giftItem?.store?.name.toString()
                    extraFees = giftItem?.store?.deliveryPrice?.toDouble() ?: 0.0
                    deliveryTime = giftItem?.store?.deliveryTime.toString()
                    deliveryType = giftItem?.store?.deliveryType.toString()
                    deliveryPrice = giftItem?.store?.deliveryPrice
                }

                cartData?.run {
                    cartItems = cartItemsList
                    cartStoreData = storeData
                }
                PrefMethods.saveUserCart(cartData)

                // findNavController().navigate(R.id.home_new_to_cart)
                viewModel.setValue(Codes.ADDED_TO_CART)
            }

            else -> {
                var i = 0
                while (i < cartItemsList!!.size) {
                    when {
                        // This item is exist in cart >> update quantity
                        cartItemsList!![i].productId == giftItem?.id -> {
                            cartItemsList!![i].productQuantity =
                                cartItemsList!![i].productQuantity // + 1 // update quantity
                            cartItemsList!![i].productTotalPrice =
                                giftItem?.price ?: 0.0 // cartItemsList!![i].productTotalPrice + giftItem?.price!! // update price

                            val sharedCart = PrefMethods.getUserCart()

                            sharedCart?.cartItems?.clear()
                            sharedCart?.cartItems?.addAll(cartItemsList!!)

                            PrefMethods.saveUserCart(sharedCart)
                            // findNavController().navigate(R.id.home_new_to_cart)
                            viewModel.setValue(Codes.ADDED_TO_CART)

                            return
                        }

                        i == cartItemsList!!.size - 1 -> {
                            // This item not exist in cart
                            cartItemsList!!.add(getItemCart(giftItem))

                            storeData?.run {
                                storeId = giftItem?.storeId
                                storeName = giftItem?.store?.name.toString()
                                extraFees = giftItem?.store?.deliveryPrice?.toDouble() ?: 0.0
                            }

                            cartData?.run {
                                cartItems = cartItemsList
                                cartStoreData = storeData
                            }

                            PrefMethods.saveUserCart(cartData)

                            // findNavController().navigate(R.id.home_new_to_cart)
                            viewModel.setValue(Codes.ADDED_TO_CART)

                            return
                        }

                        else -> i++
                    }
                }
            }
        }
    }

    private fun getItemCart(giftItem: GiftItem?): CartItem {
        val cartItem = CartItem()
        cartItem.productId = giftItem?.id
        cartItem.productName = giftItem?.name
        cartItem.productDescription = giftItem?.description
        cartItem.productImage = giftItem?.image
        cartItem.productQuantity = 1
        cartItem.productUnitPrice = giftItem?.price!!.toDouble()
        cartItem.productItemPrice = giftItem.price.toDouble()
        cartItem.productTotalPrice = giftItem.price.toDouble()
        cartItem.skuCode = ""
        cartItem.userNotes = ""
        cartItem.addonsIds = arrayListOf()
        cartItem.addonsNames = arrayListOf()
        cartItem.addonsPrice = 0.0
        cartItem.type = "gift"
        cartItem.isOffer = true

        return cartItem
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.ADDED_TO_CART -> {
                findNavController().navigate(R.id.home_new_to_cart)
            }

            Codes.EDIT_CLICKED -> {
                findNavController().navigate(R.id.home_new_to_profile)
            }

            Codes.CART_CLICKED -> {
                findNavController().navigate(R.id.home_new_to_cart)
            }

            Codes.CLEAR_CART -> {
                val bundle = Bundle()
                bundle.putString(
                    Params.DIALOG_CONFIRM_MESSAGE,
                    MyApp.getInstance().getString(R.string.add_item_to_cart)
                )
                bundle.putString(
                    Params.DIALOG_CONFIRM_POSITIVE,
                    MyApp.getInstance().getString(R.string.confirm)
                )
                bundle.putString(
                    Params.DIALOG_CONFIRM_NEGATIVE,
                    MyApp.getInstance().getString(R.string.ignore)
                )
                Utils.startDialogActivity(
                    requireActivity(),
                    DialogConfirmFragment::class.java.name,
                    Codes.DIALOG_CONFIRM_REQUEST,
                    bundle
                )
            }

            Codes.SEARCH_CLICKED -> {
                if (!viewModel.categoriesList.isNullOrEmpty()) {
                    val action = HomeNewFragmentDirections.homeNewToSearch(
                        "homenew",
                        categoryId,
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

            Codes.SHOW_POPUP -> {
                if (PrefMethods.getHomePopup()) {
                    val bundle = Bundle()
                    bundle.putParcelable(Params.DIALOG_HOME_POPUP, viewModel.homeNewResponse)
                    Utils.startDialogActivity(
                        requireActivity(),
                        DialogHomePopupFragment::class.java.name,
                        Codes.DIALOG_HOME_POPUP,
                        bundle
                    )
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
                                val addressItem =
                                    data.getParcelableExtra<AddressListItem>(Params.ADDRESS_ITEM)
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
                            startActivityForResult(
                                (Intent(requireActivity(), ListAddressesActivity::class.java))
                                    .putExtra(Params.DELIVERY_ADDRESSES_FLAG, 1),
                                Codes.SHOW_DELIVERY_ADDRESSES_CODE
                            )
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
                                        if (sliderItem.storeId != null) {
//                                            if(sliderItem.storeIds.size > 1) {
//                                                val action = HomeNewFragmentDirections.homeNewToHome("", "", sliderItem.storeIds.toTypedArray())
//                                                findNavController().navigate(action)
//                                            }else{
                                            val action =
                                                HomeNewFragmentDirections.homeNewToStoreDetails(
                                                    sliderItem.storeId
                                                )
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

            Codes.SELECT_ORDER_ADDONS_ACTIVITY, Codes.SELECT_ORDER_ADDONS_DIALOG -> {
                when {
                    data != null -> {
                        when {
                            data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                                when {
                                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                        val cartParcelableItem =
                                            data.getParcelableExtra<CartParcelableClass>(Params.STORE_PRODUCT_ITEM) as CartParcelableClass
                                        val cartItem = cartParcelableItem.cartItem
                                        val storeProductItem = cartParcelableItem.storeProductItem

                                        println("cartStore?.storeId ${viewModel.cartStore?.storeId}")
                                        println("storeProductItem?.storeId ${storeProductItem?.storeId}")
                                        println("cartStoreData?.storeId ${PrefMethods.getUserCart()?.cartStoreData?.storeId}")
                                        println("storeProductItem $storeProductItem")

                                        // viewModel.getStoreDetails(storeProductItem.storeId!!)

                                        when (viewModel.cartStore?.storeId) {
                                            null -> {
                                                // Cart is empty .. This is the first product added to cart
                                                when {
                                                    cartItem != null -> {
                                                        viewModel.addProductToCart(cartItem, storeProductItem!!)
                                                    }
                                                }
                                            }

                                            else -> {
                                                when {
                                                    // Cart is Not empty But the saved products NOT has the same store Id >> Clear the cart before adding new products
                                                    viewModel.cartStore?.storeId != PrefMethods.getUserCart()?.cartStoreData?.storeId -> { // storeProductItem!!.storeId
                                                        val bundle = Bundle()
                                                        bundle.putString(
                                                            Params.DIALOG_CONFIRM_MESSAGE,
                                                            MyApp.getInstance()
                                                                .getString(R.string.add_item_to_cart)
                                                        )
                                                        bundle.putString(
                                                            Params.DIALOG_CONFIRM_POSITIVE,
                                                            MyApp.getInstance()
                                                                .getString(R.string.confirm)
                                                        )
                                                        bundle.putString(
                                                            Params.DIALOG_CONFIRM_NEGATIVE,
                                                            MyApp.getInstance()
                                                                .getString(R.string.ignore)
                                                        )
                                                        bundle.putParcelable(
                                                            Params.DIALOG_STORE_ITEM,
                                                            cartParcelableItem
                                                        )
                                                        Utils.startDialogActivity(
                                                            requireActivity(),
                                                            DialogConfirmFragment::class.java.name,
                                                            Codes.DIALOG_CONFIRM_REQUEST,
                                                            bundle
                                                        )
                                                    }

                                                    else -> {
                                                        // Cart is Not empty and the all products has the sam Id
                                                        if (cartItem != null) {
                                                            viewModel.addProductToCart(
                                                                cartItem,
                                                                storeProductItem!!
                                                            )
                                                            storeProductItem.let {
                                                                // viewModel.productsAdapter.notifyItemSelected(it)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 0 -> {
                                        // User clicked back and no changed detected
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /* Ask user to clear old cart items which contains items with different store */
            Codes.DIALOG_CONFIRM_REQUEST -> {
                when {
                    data != null -> {
                        when {
                            data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                                when {
                                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                        when {
                                            data.hasExtra(Params.DIALOG_STORE_ITEM) -> {
                                                val cartParcelableItem: CartParcelableClass? =
                                                    data.getParcelableExtra(Params.DIALOG_STORE_ITEM)
                                                val cartItem = cartParcelableItem?.cartItem
                                                val storeProductItem =
                                                    cartParcelableItem?.storeProductItem
                                                if (storeProductItem != null) {
                                                    // viewModel.productsAdapter.notifyItemSelected(storeProductItem)
                                                }
                                                viewModel.cartItemsList?.clear()
                                                if (cartItem != null) {
                                                    viewModel.addProductToCart(
                                                        cartItem,
                                                        storeProductItem!!
                                                    )
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
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserStatus()

        if (PrefMethods.getUserCart() != null) {
            viewModel.userCart = UserCart()
            viewModel.cartStore = CartStoreData()
            viewModel.cartItemsList = ArrayList()

            viewModel.userCart = PrefMethods.getUserCart()!!
            viewModel.cartStore = viewModel.userCart!!.cartStoreData!!
            viewModel.cartItemsList =
                viewModel.userCart!!.cartItems as ArrayList<CartItem>
            viewModel.obsIsVisible.set(true)

            /*for (productsList in viewModel.productsList) {
                for (cartList in PrefMethods.getUserCart()!!.cartItems!!) {
                    if (cartList.productId == productsList.id) {
                        productsList.isSelected = true
                    }
                }
            }*/
        } else {
            viewModel.userCart = UserCart()
            viewModel.cartStore = CartStoreData()
            viewModel.cartItemsList = ArrayList()
            viewModel.obsIsVisible.set(false)
        }

        // viewModel.productsAdapter.updateList(viewModel.productsList)
    }

    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion() {
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

    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        when (num) {
            1 -> {
                view?.post {
                    val action =
                        HomeNewFragmentDirections.homeNewToAddedStories(
                            storiesItem.storeId!!,
                            storiesItem
                        )
                    findNavController().navigate(action)
                }
            }

            2 -> {
                view?.post {
                    val action =
                        HomeNewFragmentDirections.homeNewToStoreDetails(storiesItem.storeId!!)
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

    private fun setupSlider(sliders: List<SlidesItem?>?) {
        viewModel.moreSliderAdapter.updateList(sliders as List<SlidesItem>)
        sliderView = requireActivity().findViewById(R.id.bannerSlider)
        viewModel.moreSliderAdapter.clickListener = {slidesItem ->
            when (slidesItem.type) {
                "discover" -> {
                    findNavController().navigate(R.id.nav_discover)
                }
                "favorites" -> {
                    findNavController().navigate(R.id.nav_favorites)
                }
                "wallet" -> {
                    findNavController().navigate(R.id.nav_payment)
                }
                "offers" -> {
                    findNavController().navigate(R.id.nav_offers)
                }
                "help" -> {
                    findNavController().navigate(R.id.nav_help)
                }
                "store" -> {
                    if (slidesItem.storeIdsArray.orEmpty().size > 1) {
                        val action = HomeNewFragmentDirections.homeNewToHome(
                            "",
                            "",
                            slidesItem.storeIdsArray.orEmpty().toTypedArray()
                        )
                        findNavController().navigate(action)
                    } else {
                        val action =
                            HomeNewFragmentDirections.homeNewToStoreDetails(
                                slidesItem.storeIdsArray?.get(0)?.toInt()?:-1)
                        findNavController().navigate(action)
                    }
                }
                "category" -> {
                    /*categoryId = slidesItem.categoryId.toString().orEmpty()
                    Timber.e("id:$categoryId")
                   *//* if (categoryId.isNullOrEmpty())
                    {
                        findNavController().navigate(HomeNewFragmentDirections.homeNewToHome(
                            "",
                            "",
                            arrayOf()
                        ))
                    }else{*//*
                        val action = HomeNewFragmentDirections.homeNewToHome(
                            categoryId, if (PrefMethods.getLanguage()=="ar")slidesItem.content.orEmpty() else slidesItem.contentEn.orEmpty(),
                            emptyArray()
                        )
                        findNavController().navigate(action)
//                    }*/

                }
                "product" -> {
                    lifecycleScope.launch {

                        when (viewModel.getProductStatus(slidesItem.productId ?: -1)) {
                            "simple", "Simple" -> {
                                val bundle = Bundle().apply {
                                    putInt("productID", slidesItem.productId ?: -1)
                                }
                                Utils.startDialogActivity(
                                    requireActivity(),
                                    DialogOrderAddonsFragment::class.java.name,
                                    Codes.SELECT_ORDER_ADDONS_DIALOG,
                                    bundle
                                )
                            }

                            "variable", "Variable" -> {
                                val intent = Intent(requireActivity(), OrderAddonsActivity::class.java).apply {
                                    putExtra(Params.STORE_PRODUCT_ITEM, slidesItem.productId ?: -1)
                                    putExtra(Params.DIALOG_CLICK_ACTION, 0)
                                }
                                startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                            }
                        }
                    }

                }
                else ->{
                    Timber.e("Wrong Type")
                }
            }
        }
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