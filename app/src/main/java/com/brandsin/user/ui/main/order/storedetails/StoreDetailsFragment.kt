package com.brandsin.user.ui.main.order.storedetails

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.brandsin.user.model.order.details.Store
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homepage.HomePageResponse
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.storedetails.CoversItem
import com.brandsin.user.model.order.storedetails.StoreCategoryItem
import com.brandsin.user.model.order.storedetails.StoreDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.chat.model.MessageModel
import com.brandsin.user.ui.dialogs.confirm.DialogConfirmFragment
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.OrderAddonsActivity
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.DialogOrderAddonsFragment
import com.brandsin.user.ui.main.order.storedetails.banners.BannersAdapter
import com.brandsin.user.ui.main.order.storedetails.products.StoreProductsAdapter_V2
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import com.brandsin.user.utils.storyviewer.StoryView
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StoreDetailsFragment : BaseHomeFragment(), Observer<Any?>,
    StoriesAdapter.OnStoryClickedListener, BannersAdapter.OnBannerClickedListener,
    StoryView.StoryViewListener {

    private lateinit var binding: HomeFragmentStoreDetailsV2Binding

    private lateinit var viewModel: StoreDetailsViewModel

    private val storeArgs: StoreDetailsFragmentArgs by navArgs()
    private lateinit var sliderView: SliderView
    private var storeProductItem: StoreProductItem? = null
    private var storeCategoryId = 0

    private var STORE_ID: Int? = null

    var storiesItem = StoriesItem()
    var store = Store()
    var flag: Boolean = false

    private var storeCategoriesList: List<StoreCategoryItem> = ArrayList()
    private var productsList: ArrayList<StoreProductItem> = ArrayList()
    private var productsListLimit: MutableList<StoreProductItem> = ArrayList()

    private var limit: Int = 10
    private var currentPage: Int = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false

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

        viewModel = ViewModelProvider(this)[StoreDetailsViewModel::class.java]
        binding.viewModel = viewModel
binding.lifecycleOwner = this
        viewModel.setBannerListener(this)
        viewModel.setStoriesListener(this)

        STORE_ID = requireArguments().getInt("STORE_ID")

        viewModel.getStoreDetails(storeArgs.storeId)

        /*if (!flag) {
            viewModel.getStoreDetails(storeArgs.storeId)
            flag = true
        } else {
            viewModel.obsIsFull.set(true)
            viewModel.obsIsLoading.set(false)
            viewModel.storeCategoriesList = storeCategoriesList
            viewModel.productsList = productsList
            val pages = (productsList.size / 10)
            for (i in 0..pages) {
                binding.rvMeals.post { loadMoreItems() }
            }
        }*/

        binding.ibBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.changeViewLayout.setOnClickListener {

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
                    showToast(it.message.toString(), 2)
                    viewModel.getStoreDetails(storeArgs.storeId)
                }

                else -> {
                    Timber.e(it.message)
                }
            }
        }

        observe(viewModel.apiResponseLiveData) { it ->
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
                                val stories: MutableList<ArrayList<StoriesItem>> = ArrayList()
                                store.name = viewModel.storeData.name
                                if (!it.data.storeDetailsData.thumbnail.isNullOrEmpty()) {
                                    store.thumbnail = it.data.storeDetailsData.thumbnail
                                }
                                storiesItem.store = store
                                it.data.storeDetailsData.stories.forEach { storyItem ->
                                    // it.stories!!.forEach {
                                    storyItem.store = store
                                    // }
                                    val newList: ArrayList<StoriesItem> = ArrayList()
                                    newList.add(storyItem)
                                    stories.add(newList)
                                }
//                                    Glide.with(MyApp.context).load( it.data.storeDetailsData.thumbnail)
//                                        .error(R.drawable.app_logo)
//                                        .into(binding.storyStoreImg)
                                // binding.cvStoryStoreImg.visibility = View.VISIBLE


                                viewModel.storiesAdapter.updateList(stories)
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

                            //////////// pagination offline ///////////
                            viewModel.productsList =
                                it.data.storeDetailsData.storeProductList as ArrayList<StoreProductItem>

                            Log.d("listRes", viewModel.productsList.size.toString())

                            viewModel.productsAdapter.clear()
                            productsListLimit.clear()
                            currentPage = 0
                            if (limit < viewModel.productsList.size) {
                                productsListLimit.addAll(viewModel.productsList.subList(0, limit))
                                viewModel.productsAdapter.addItems(productsListLimit.filter { it.status != "inactive" }.toMutableList())
                                //  currentPage++
                            } else {
                                productsListLimit.addAll(viewModel.productsList)
                                viewModel.productsAdapter.addItems(productsListLimit.filter { it.status != "inactive" }.toMutableList())
                                isLastPage = true
                            }
                            val pages = (viewModel.productsList.size / 10).toInt()

                            Log.d("aaa", pages.toString())

                            for (it in 0..pages) {
                                binding.rvMeals.post { loadMoreItems() }
                                Log.d("num", "mom")
                            }
                        }
                    }
                }

                else -> {
                    Timber.e(it.message)
                }
            }
        }

        setBtnListener()
    }

    private fun setBtnListener() {
        binding.materialCardView.setOnClickListener {
            // if (viewModel.storeData.stories!!.size > 0) {
            view?.post {
                val action =
                    StoreDetailsFragmentDirections.storeDetailsToAddedStories(
                        viewModel.storeData.id!!,
                        storiesItem
                    )
                findNavController().navigate(action)
            }
            // }
        }

        binding.tvStoreInfo.setOnClickListener {
            view?.post {
                val action =
                    StoreDetailsFragmentDirections.storeDetailsToStoreInformation(
                        viewModel.storeData
                    )
                findNavController().navigate(action)
            }
        }
        viewModel.isGrid.observe(viewLifecycleOwner)
        {
            if (it)
            {
                val tmpAdapter = viewModel.productsAdapter
                tmpAdapter.isGrid = it
                binding.rvMealsGrid.adapter = tmpAdapter
                binding.rvMealsGrid.visibility = View.VISIBLE
                binding.rvMeals.visibility = View.GONE
                binding.changeViewLayout.setImageResource(R.drawable.list)
            } else
            {
                val tmpAdapter = viewModel.productsAdapter
                tmpAdapter.isGrid = it
                binding.rvMeals.adapter = tmpAdapter
                binding.rvMealsGrid.visibility = View.GONE
                binding.rvMeals.visibility = View.VISIBLE
                binding.changeViewLayout.setImageResource(R.drawable.ic_grid)
            }
        }

        binding.ratingsStore.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("STORE_ID", viewModel.storeData.id!!)
            findNavController().navigate(R.id.ratingsStoreFragment, bundle)
        }

        binding.chatStore.isSelected  = true
        binding.chatStore.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            val currentTime = dateFormat.format(Date())
            val messageModel = MessageModel(
                avaterstore = viewModel.storeData.thumbnail.toString(),
                avateruser = PrefMethods.getUserData()?.picture.toString(),
                messageType = "private",
                senderName = PrefMethods.getUserData()?.name.toString(),
                storename = viewModel.storeData.name.toString(),
                senderId = PrefMethods.getUserData()?.id.toString().trim(),
                storeId = viewModel.storeData.userId.toString(),
                message = "",
                messageId = UUID.randomUUID().toString(),
                date = currentTime,
                type = "",
                typeBay = "user"
            )
            Timber.e("model is ${viewModel.storeData}\n$messageModel")

            // inboxViewModel.messageItem = messageModel

            val bundle = Bundle()
            bundle.putString("Avatar_Store", messageModel.avaterstore)
            bundle.putString("Avatar_User", messageModel.avateruser)
            bundle.putString("Sender_Id", messageModel.senderId)
            bundle.putString("Sender_Name", messageModel.senderName)
            bundle.putString("Store_Id", messageModel.storeId)
            bundle.putString("Store_Name", messageModel.storename)
            findNavController().navigate(R.id.messageFragment, bundle)
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

        binding.followStore.setOnClickListener {
            if (PrefMethods.getLoginState(context)) {
                viewModel.followStore()
            } else {
                showToast("please login first", 1)
            }
        }

        binding.icShare.setOnClickListener {
            generateDynamicLink()
        }
    }

    private fun generateDynamicLink() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://dev.brandsin.net/store?store_id=${viewModel.storeData.id}")) // Replace with your deep link URL
            .setDomainUriPrefix("https://brandsin.page.link") // Replace with your Firebase Dynamic Links domain
            .setAndroidParameters( // + "/product-details?product=" + product_id;
                DynamicLink.AndroidParameters.Builder(requireContext().packageName)
                    .setFallbackUrl(Uri.parse("YOUR_FALLBACK_URL_HERE"))
                    .setMinimumVersion(1) // Optional: Minimum app version required
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(viewModel.storeData.name ?: "") // "Your Title"
                    .setDescription("Your Description")
                    .setImageUrl(Uri.parse(viewModel.storeData.thumbnail ?: "")) // Optional: Image URL for sharing ("https://www.example.com/image.png")
                    .build()
            )
            .buildShortDynamicLink()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shortLink = task.result.shortLink
                    val flowchartLink = task.result.previewLink
                    // Handle the short link (e.g., display it or share it)
                    println("dynamicLink == Short Link: $shortLink")
                    println("dynamicLink == Preview Link: $flowchartLink")
 
                    // Handle the short link (e.g., share it)
                    // shortLink.toString() contains the shortened URL
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/Share"
                    val shareBody = shortLink.toString()
                    val shareSub = resources.getString(R.string.app_name)
                    share.putExtra(Intent.EXTRA_SUBJECT, shareSub)
                    share.putExtra(Intent.EXTRA_TEXT, shareBody)
                    startActivity(Intent.createChooser(share, "Share using"))
                } else {
                    // Handle error
                    println("dynamicLink == exception Link: ${task.exception}")
                    println("dynamicLink == exception Link: ${task.result.shortLink}")
                    println("dynamicLink == exception Link: ${task.result.previewLink}")
                }
            }
    }

    private fun loadMoreItems() {
        isLoading = true
        currentPage++
        val listSize = viewModel.productsList.size
        val listSize2 = productsListLimit.size

        if ((listSize2 + limit) < listSize) {
            productsListLimit.addAll(viewModel.productsList.subList(listSize2, listSize2 + limit))
            viewModel.productsAdapter.addItems(
                viewModel.productsList.subList(
                    listSize2,
                    listSize2 + limit
                ).filter { it.status != "inactive" }.toMutableList()
            )
        } else {
            productsListLimit.addAll(viewModel.productsList.subList(listSize2, listSize))
            viewModel.productsAdapter.addItems(
                viewModel.productsList.subList(listSize2, listSize).filter { it.status != "inactive" }.toMutableList()
            )
            isLastPage = true
        }
        isLoading = false
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.CART_CLICKED -> {
                findNavController().navigate(R.id.store_to_cart)
            }

            is StoreProductItem -> {
                storeProductItem = value
                when (value.type) {
                    "simple" -> {
                        println("StoreDetailsFragment == simple")
                        val bundle = Bundle()
                        bundle.putParcelable(Params.STORE_PRODUCT_ITEM, value)
                        Utils.startDialogActivity(
                            requireActivity(),
                            DialogOrderAddonsFragment::class.java.name,
                            Codes.SELECT_ORDER_ADDONS_DIALOG,
                            bundle
                        )
                    }

                    "variable" -> {
                        println("StoreDetailsFragment == variable")
                        val intent = Intent(requireActivity(), OrderAddonsActivity::class.java)
                        intent.putExtra(Params.STORE_PRODUCT_ITEM, value)
                        intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
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
                value.let {
                    it.id?.let { it1 ->
                        viewModel.getFilteredProducts(it1)
                        storeCategoryId = it1
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            /* When User back from Addons page */
            Codes.SELECT_ORDER_ADDONS_ACTIVITY, Codes.SELECT_ORDER_ADDONS_DIALOG -> { // Codes.SELECT_ORDER_ADDONS_ACTIVITY,
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

                                        println("cartStore!!.storeId ${viewModel.cartStore!!.storeId}")
                                        println("storeProductItem?.storeId ${storeProductItem?.storeId}")
                                        println("PrefMethods.storeId ${PrefMethods.getUserCart()?.cartStoreData?.storeId}")

                                        when (PrefMethods.getUserCart()?.cartStoreData?.storeId) {
                                            null -> {
                                                // Cart is empty .. This is the first product added to cart
                                                when {
                                                    cartItem != null -> {
                                                        viewModel.addProductToCart(cartItem)
                                                        storeProductItem?.let {
                                                            viewModel.productsAdapter
                                                                .notifyItemSelected(it)
                                                        }
                                                    }
                                                }
                                            }

                                            else -> {
                                                when {
                                                    // Cart is Not empty But the saved products NOT has the same store Id >> Clear the cart before adding new products
                                                    viewModel.cartStore!!.storeId != storeProductItem!!.storeId ||
                                                            PrefMethods.getUserCart()?.cartStoreData?.storeId != storeProductItem.storeId -> {
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
                                                            viewModel.addProductToCart(cartItem)
                                                            viewModel.productsAdapter.notifyItemSelected(
                                                                storeProductItem
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Cart is empty .. This is the first product added to cart
                                        /*if (cartItem == null) { //&& viewModel.cartStore!!.storeId == storeProductItem?.storeId
                                            viewModel.addProductToCart(cartItem)
                                            storeProductItem?.let {
                                                viewModel.productsAdapter
                                                    .notifyItemSelected(it)
                                            }
                                        }
                                        // Cart is Not empty But the saved products NOT has the same store Id >> Clear the cart before adding new products
                                        else if (viewModel.cartStore!!.storeId != storeProductItem!!.storeId) {
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
                                        } else {
                                            // Cart is Not empty and the all products has the sam Id
                                            if (cartItem != null) {
                                                viewModel.addProductToCart(cartItem)
                                                viewModel.productsAdapter.notifyItemSelected(
                                                    storeProductItem
                                                )
                                            }
                                        }*/
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

        if (PrefMethods.getUserCart() != null && PrefMethods.getUserCart()?.cartStoreData?.storeId == storeArgs.storeId) {
            viewModel.userCart = UserCart()
            viewModel.cartStore = CartStoreData()
            viewModel.cartItemsList = ArrayList()

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
            viewModel.cartItemsList = ArrayList()
            viewModel.obsIsVisible.set(false)
        }

        viewModel.productsAdapter.updateList(viewModel.productsList.filter { it.status != "inactive" } as ArrayList)

        if (viewModel.storeCategoriesList.isNotEmpty()) {
            if (storeCategoryId == 0) {
                viewModel.getFilteredProducts(viewModel.storeCategoriesList[0].id!!)
            } else {
                viewModel.getFilteredProducts(storeCategoryId)
            }
        }

        /*PrefMethods.getUserData()?.let {
            when {
                PrefMethods.getIsAskedToLogin() -> {
                    PrefMethods.saveIsAskedToLogin(false)
                    storeProductItem.let {
                        when(storeProductItem!!.type) {
                            "simple" -> {
                                val bundle = Bundle()
                                bundle.putSerializable(Params.STORE_PRODUCT_ITEM, storeProductItem)
                                Utils.startDialogActivity(requireActivity(), DialogOrderAddonsFragment::class.java.name, Codes.SELECT_ORDER_ADDONS_DIALOG, bundle)
                            }
                            "variable" -> {
                                val intent = Intent(requireActivity(), OrderAddonsActivity::class.java)
                                intent.putExtra(Params.STORE_PRODUCT_ITEM, storeProductItem)
                                startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                            }
                        }
                    }
                }
            }
        }*/
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

    override fun onBannerClicked(position: Int, coversItem: List<CoversItem>) {
        val coversUrlList: ArrayList<String> = ArrayList()
        coversItem.forEach {
            coversUrlList.add(it.url ?: "")
        }
        val bundle = Bundle()
        bundle.putStringArrayList("COVERS_URL_LIST", coversUrlList)
        findNavController().navigate(R.id.imagesPreviewFragment, bundle)
    }

    override fun onStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>) {
        val storyV = StoryView(position, stories)
        storyV.setStoryViewListener(this)
        storyV.show(childFragmentManager, "story")
    }

    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        when (num) {
            1 -> {
                view?.post {
                    val action =
                        StoreDetailsFragmentDirections.storeDetailsToAddedStories(
                            storiesItem.storeId!!,
                            storiesItem
                        )
                    findNavController().navigate(action)
                }
            }

            2 -> {
                view?.post {
                    val action =
                        StoreDetailsFragmentDirections.storeToSelf(storiesItem.storeId!!)
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
}

/*
// when (viewModel.cartStore!!.storeId) {
                                        when (viewModel.cartStore!!.storeId == storeProductItem?.storeId) {
                                             true -> { // null
                                                // Cart is empty .. This is the first product added to cart
                                                when {
                                                    cartItem != null -> {
                                                        viewModel.addProductToCart(cartItem)
                                                        storeProductItem?.let {
                                                            viewModel.productsAdapter
                                                                .notifyItemSelected(it)
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
                                                            viewModel.addProductToCart(cartItem)
                                                            viewModel.productsAdapter.notifyItemSelected(
                                                                storeProductItem
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
 */
