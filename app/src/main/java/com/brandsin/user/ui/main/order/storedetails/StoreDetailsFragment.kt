package com.brandsin.user.ui.main.order.storedetails

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentStoreDetailsV2Binding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartParcelableClass
import com.brandsin.user.model.order.cart.CartStoreData
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homepage.HomePageResponse
import com.brandsin.user.model.order.homepage.Store
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.storedetails.CoversItem
import com.brandsin.user.model.order.storedetails.StoreCategoryItem
import com.brandsin.user.model.order.storedetails.StoreDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.dialogs.confirm.DialogConfirmFragment
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.DialogOrderAddonsFragment
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.OrderAddonsActivity
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import com.brandsin.user.utils.storyviewer.StoryView
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import timber.log.Timber

class StoreDetailsFragment : BaseHomeFragment(), Observer<Any?>,
    StoriesAdapter.OnStoryClickedListner,
    StoryView.StoryViewListener {
    private lateinit var viewModel: StoreDetailsViewModel
    private lateinit var binding: HomeFragmentStoreDetailsV2Binding
    private val storeArgs: StoreDetailsFragmentArgs by navArgs()
    lateinit var sliderView: SliderView
    private var storeProductItem: StoreProductItem? = null
    var StoreCategoryId = 0

    var storiesItem = StoriesItem()
    var store = Store()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_fragment_store_details_v2,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(StoreDetailsViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.setStoriesListner(this)
        viewModel.getStoreDetails(storeArgs.storeId)



        binding.ibBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.productsAdapter.productLiveData.observe(viewLifecycleOwner, this)
        viewModel.catAdapter.subCategoriesLiveData.observe(viewLifecycleOwner, this)

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.obsIsFull.set(false)
            viewModel.obsIsLoading.set(true)
            viewModel.getStoreDetails(storeArgs.storeId)
        }
        observe(viewModel.followResponse) {
            when (it!!.success) {
                true -> {
                    showToast(getString(R.string.success), 2)
                }
                else -> {
                    Timber.e(it.error)
                }
            }
        }

        observe(viewModel.apiResponseLiveData) {
            when (it.status) {
                Status.ERROR_MESSAGE -> {
                    showToast(it.message.toString(), 1)
                }
                Status.SUCCESS_MESSAGE -> {
                    showToast(it.message.toString(), 2)
                }
                Status.SUCCESS -> {
                    when (it.data) {
                        is StoreDetailsResponse -> {

                            if (it.data.storeDetailsData!!.covers!!.isNotEmpty()) {
                                setupSlider(it.data.storeDetailsData.covers)
                            }
                            if (it.data.storeDetailsData.stories!!.isNotEmpty()) {
                                var sotories: MutableList<ArrayList<StoriesItem>> = ArrayList()
                                store.name = viewModel.storeData.name
                                if (!it.data.storeDetailsData.thumbnail.isNullOrEmpty()) {
                                    store.thumbnail = it.data.storeDetailsData.thumbnail
                                }
                                storiesItem.store = store
                                it.data.storeDetailsData.stories.forEach {
                                   // it.stories!!.forEach {
                                        it!!.store = store
                                   // }
                                    val newList: ArrayList<StoriesItem> = ArrayList()
                                    newList.add(it)
                                    sotories.add(newList)
                                }
//                                    Glide.with(MyApp.context).load( it.data.storeDetailsData.thumbnail)
//                                        .error(R.drawable.app_logo)
//                                        .into(binding.storyStoreImg)
                                // binding.cvStoryStoreImg.visibility = View.VISIBLE


                                viewModel.storiesAdapter.updateList(sotories)
                                binding.layoutStories.visibility = View.VISIBLE
                            }
                            if (it.data.storeDetailsData.hasDelivery == 0) {
                                binding.tvDeliveryPrice.visibility = View.GONE
                            }
                            if (it.data.storeDetailsData.hasDelivery == 1) {
                                binding.tvDeliveryPrice.visibility = View.VISIBLE
                            }
                            if (it.data.storeDetailsData.hasTodayStories) {
                                binding.materialCardView.strokeWidth = 2
                            } else {
                                binding.materialCardView.strokeWidth = 0
                            }
                        }
                    }
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }

        binding.materialCardView.setOnClickListener {
            view?.post {
                val action =
                    StoreDetailsFragmentDirections.storeDetailsToAddedStories(
                        viewModel.storeData.id!!,
                        storiesItem
                    )
                findNavController().navigate(action)
            }
        }

        binding.storeInformation.setOnClickListener {
            view?.post {
                val action =
                    StoreDetailsFragmentDirections.storeDetailsToStoreInformation(
                        viewModel.storeData
                    )
                findNavController().navigate(action)
            }
        }

        binding.ibSearch.setOnClickListener {

            val action = StoreDetailsFragmentDirections.storeDetailsToSearch(
                "store",
                "",
                HomePageResponse(),
                HomeNewResponse(),
                viewModel.storeData

            )
            findNavController().navigate(action)
        }
        binding.followStore.setOnClickListener{
            if (PrefMethods.getLoginState(context)){
                viewModel.followStore()
            }else{
                showToast("please login first",1)
            }
        }
    }

    override fun onChanged(it: Any?) {
        if (it == null) return
        when (it) {
            Codes.CART_CLICKED -> {
                findNavController().navigate(R.id.store_to_cart)
            }
            is StoreProductItem -> {
                storeProductItem = it
                when (it.type) {
                    "simple" -> {
                        val bundle = Bundle()
                        bundle.putSerializable(Params.STORE_PRODUCT_ITEM, it)
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
                        startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                    }
                }

                // This block of code if you want to check the user is logged in or not
                /* when {
                     PrefMethods.getUserData() == null -> {
                         Utils.startDialogActivity(requireActivity(), DialogNotLoginFragment::class.java.name, Codes.DIALOG_NOT_LOGIN_REQUEST, null)
                     }
                     else -> {
                         when(it.type) {
                             "simple" -> {
                                 val bundle = Bundle()
                                 bundle.putSerializable(Params.STORE_PRODUCT_ITEM, it)
                                 Utils.startDialogActivity(requireActivity(), DialogOrderAddonsFragment::class.java.name, Codes.SELECT_ORDER_ADDONS_DIALOG, bundle)
                             }
                             "variable" -> {
                                 val intent = Intent(requireActivity(), OrderAddonsActivity::class.java)
                                 intent.putExtra(Params.STORE_PRODUCT_ITEM, it)
                                 startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                             }
                         }
                     }
                 }*/
            }
            is StoreCategoryItem -> {
                it.let {
                    it.id?.let { it1 ->
                        viewModel.getFilteredProducts(it1)
                        StoreCategoryId = it1
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            /* When User back from Addons page */
            Codes.SELECT_ORDER_ADDONS_ACTIVITY, Codes.SELECT_ORDER_ADDONS_DIALOG -> {
                when {
                    data != null -> {
                        when {
                            data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                                when {
                                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                        val cartParcelableItem =
                                            data.getSerializableExtra(Params.STORE_PRODUCT_ITEM) as CartParcelableClass
                                        val cartItem = cartParcelableItem.cartItem
                                        val storeProductItem = cartParcelableItem.storeProductItem
                                        when (viewModel.cartStore!!.storeId) {
                                            null -> {
                                                // Cart is empty .. This is the first product added to cart
                                                when {
                                                    cartItem != null -> {
                                                        viewModel.addProductToCart(cartItem)
                                                        storeProductItem?.let {
                                                            viewModel.productsAdapter.notifyItemSelected(
                                                                it)
                                                        }
                                                    }
                                                }
                                            }
                                            else -> {
                                                when {
                                                    // Cart is Not empty But the saved products NOT has the same store Id >> Clear the cart before adding new products
                                                    viewModel.cartStore!!.storeId != storeProductItem!!.storeId -> {
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
                                                        bundle.putSerializable(
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
                                                            viewModel.addProductToCart(cartItem)
                                                            viewModel.productsAdapter.notifyItemSelected(
                                                                storeProductItem
                                                            )
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
                                                val cartParcelableItem =
                                                    data.getSerializableExtra(Params.DIALOG_STORE_ITEM) as CartParcelableClass
                                                val cartItem = cartParcelableItem.cartItem
                                                val storeProductItem =
                                                    cartParcelableItem.storeProductItem
                                                if (storeProductItem != null) {
                                                    viewModel.productsAdapter.notifyItemSelected(
                                                        storeProductItem
                                                    )
                                                }
                                                viewModel.cartItemsList!!.clear()
                                                if (cartItem != null) {
                                                    viewModel.addProductToCart(cartItem)
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

            /*       *//* Ask user To Log In *//*
            Codes.DIALOG_NOT_LOGIN_REQUEST -> {
                when {
                    data != null -> {
                        when {
                            data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                                when {
                                    data.getIntExtra(Params.DIALOG_CLICK_ACTION,1) == 1 -> {
                                        PrefMethods.saveIsAskedToLogin(true)
                                        startActivity(Intent(requireActivity(), AuthActivity::class.java).putExtra(Const.ACCESS_LOGIN , true))
                                    }
                                }
                            }
                        }
                    }
                }
            }*/
        }
    }

    override fun onResume() {
        super.onResume()

        if (PrefMethods.getUserCart() != null) {
            viewModel.userCart = UserCart()
            viewModel.cartStore = CartStoreData()
            viewModel.cartItemsList = ArrayList<CartItem>()

            viewModel.userCart = PrefMethods.getUserCart()!!
            viewModel.cartStore = viewModel.userCart!!.cartStoreData!!
            viewModel.cartItemsList = viewModel.userCart!!.cartItems as ArrayList<CartItem>
            viewModel.obsIsVisible.set(true)

            for (productsList in viewModel.productsList) {
                for (cartList in PrefMethods.getUserCart()!!.cartItems!!) {
                    if (cartList.productId == productsList.id) {
                        productsList.isSelected = true
                    }
                }
            }
        } else {
            viewModel.userCart = UserCart()
            viewModel.cartStore = CartStoreData()
            viewModel.cartItemsList = ArrayList<CartItem>()
            viewModel.obsIsVisible.set(false)
        }

        viewModel.productsAdapter.updateList(viewModel.productsList)
        if (viewModel.storeCategoriesList.isNotEmpty()) {
            if (StoreCategoryId == 0) {
                viewModel.getFilteredProducts(viewModel.storeCategoriesList[0].id!!)
            } else {
                viewModel.getFilteredProducts(StoreCategoryId)
            }
        }


//        PrefMethods.getUserData()?.let {
//            when {
//                PrefMethods.getIsAskedToLogin() -> {
//                    PrefMethods.saveIsAskedToLogin(false)
//                    storeProductItem.let {
//                        when(storeProductItem!!.type) {
//                            "simple" -> {
//                                val bundle = Bundle()
//                                bundle.putSerializable(Params.STORE_PRODUCT_ITEM, storeProductItem)
//                                Utils.startDialogActivity(requireActivity(), DialogOrderAddonsFragment::class.java.name, Codes.SELECT_ORDER_ADDONS_DIALOG, bundle)
//                            }
//                            "variable" -> {
//                                val intent = Intent(requireActivity(), OrderAddonsActivity::class.java)
//                                intent.putExtra(Params.STORE_PRODUCT_ITEM, storeProductItem)
//                                startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }


    private fun setupSlider(covers: List<CoversItem?>?) {
        viewModel.bannersAdapter.updateList(covers as List<CoversItem>)
        sliderView = requireActivity().findViewById(R.id.bannerSlider)
        sliderView.setSliderAdapter(viewModel.bannersAdapter)
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        sliderView.indicatorSelectedColor = Color.WHITE
        sliderView.indicatorUnselectedColor = Color.GRAY
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
    }

    override fun onStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>) {
        var storyv: StoryView = StoryView(position, stories)
        storyv.setStoryViewListener(this)
        storyv.show(childFragmentManager, "story")
    }

    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        if (num == 1) {
            view?.post {
                val action =
                    StoreDetailsFragmentDirections.storeDetailsToAddedStories(
                        storiesItem.storeId!!,
                        storiesItem
                    )
                findNavController().navigate(action)
            }
        } else if (num == 2) {
            view?.post {
                val action =
                    StoreDetailsFragmentDirections.storeToSelf(storiesItem.storeId!!)
                findNavController().navigate(action)
            }
        } else {
            view?.post {
                findNavController().navigateUp()
            }
        }
    }
}