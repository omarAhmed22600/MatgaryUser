package com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.DialogOrderAddonsBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartParcelableClass
import com.brandsin.user.model.order.productdetails.ProductDetailsResponse
import com.brandsin.user.model.order.searchproducts.SearchProductItem
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.order.reviewStore.model.RatingItem
import com.brandsin.user.ui.main.order.storedetails.addons.addons.VideoPreviewFragment
import com.brandsin.user.ui.main.order.storedetails.addons.ratingsProduct.RatingsProductFragment
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.adapter.RatingsProductAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.banners.BannersAdapter
import com.brandsin.user.ui.main.search.ImagesPreviewFragment
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.Utils.html2text
import com.brandsin.user.utils.map.observe
import com.brandsin.user.utils.storyviewer.utils.hide
import com.brandsin.user.utils.storyviewer.utils.show
import com.bumptech.glide.Glide
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import timber.log.Timber

class DialogOrderAddonsFragment : BaseHomeFragment(), Observer<Any?>,
    BannersAdapter.OnBannersClickedListener {

    private lateinit var binding: DialogOrderAddonsBinding

    lateinit var viewModel: DialogOrderAddonsViewModel

    private var productID: Int? = null
    var product = StoreProductItem()
    private var productSearch = SearchProductItem()

    private var productDetailsResponse = ProductDetailsResponse()

    private lateinit var ratingsProductAdapter: RatingsProductAdapter

    private val onItemClickCallBack: (ratingsItem: RatingItem) -> Unit =
        { ratingsItem ->
            if (ratingsItem.video?.isNotEmpty() == true) {
                val videoUrl = ratingsItem.video.toString()

                val bundle = Bundle()
                bundle.putString("videoUrl", videoUrl)

                val fragment = VideoPreviewFragment()
                fragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit()

            } else if (ratingsItem.image?.isNotEmpty() == true) {
                val imagesAdsUrlList: ArrayList<String> = ArrayList()
                imagesAdsUrlList.add(ratingsItem.image)
                val bundle = Bundle()
                bundle.putStringArrayList("COVERS_URL_LIST", imagesAdsUrlList)

                val fragment = ImagesPreviewFragment.newInstance(bundle)

                // Pass a tag to addToBackStack
                // val transactionTag = "YourTransactionTag"

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null) // Optional: Add the transaction to the back stack
                    .commit()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            arguments != null -> {
                when {
                    requireArguments().containsKey(Params.STORE_PRODUCT_ITEM) -> {
                        product =
                            (requireArguments().getParcelable(Params.STORE_PRODUCT_ITEM) as StoreProductItem?)!!
                    }

                    requireArguments().containsKey(Params.SEARCH_PRODUCT_ITEM) -> {
                        productSearch =
                            (requireArguments().getParcelable(Params.SEARCH_PRODUCT_ITEM) as SearchProductItem?)!!
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogOrderAddonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[DialogOrderAddonsViewModel::class.java]
        binding.viewModel = viewModel

        productID = requireArguments().getInt("productID")
        // viewModel.obsIsVisible.set(true)

        // Add an OnBackStackChangedListener to the fragment manager
        requireActivity().supportFragmentManager.addOnBackStackChangedListener {
            // Check if the back stack is empty
            val isBackStackEmpty = requireActivity().supportFragmentManager.backStackEntryCount == 0

            // Update the visibility of addToCartLayout based on the back stack status
            if (isBackStackEmpty) {
                binding.addToCartLayout.visibility = View.VISIBLE
            } else {
                binding.addToCartLayout.visibility = View.GONE
            }
        }

        if (product.id != null) {
            viewModel.getProductDetails(product.id ?: 0)
        } else if (productSearch.id != null) {
            viewModel.getProductDetails(productSearch.id ?: 0)
        } else {
            viewModel.getProductDetails(productID ?: 0)
        }

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        viewModel.setBannerListener(this)

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
                        is ProductDetailsResponse -> {

                            productDetailsResponse = it.data

                            binding.tvMealName.text = productDetailsResponse.data!!.name
                            binding.tvMealDescription.text =
                                productDetailsResponse.data!!.description
                            binding.tvMealDescription.text =
                                html2text(productDetailsResponse.data!!.description.toString())

                            if (productDetailsResponse.data?.imagesIds?.isNotEmpty() == true) {
                                setupSlider(productDetailsResponse.data!!.imagesIds)
                            }

                            if (productDetailsResponse.data?.isFavorite == false) {
                                binding.icFavorite.setImageResource(R.drawable.ic_favorite_un_selected)
                            } else {
                                binding.icFavorite.setImageResource(R.drawable.ic_favorite_selected)
                            }

                            if (!productDetailsResponse.data?.video.isNullOrEmpty()) {
                                binding.btnWatchingVideo.visibility = View.VISIBLE
                            }

                            binding.numberOfRatings.text =
                                productDetailsResponse.data?.ratings?.size.toString() + " " + getString(
                                    R.string.review
                                )

                            println("nextId == ${productDetailsResponse.data?.nextId}")
                            println("prevId ==  ${productDetailsResponse.data?.prevId}")

                            if (productDetailsResponse.data?.nextId == null) {
                                binding.icArrowNextProduct.hide()
                            } else {
                                binding.icArrowNextProduct.show()
                            }

                            if (productDetailsResponse.data?.prevId == null) {
                                binding.icArrowPreviousProduct.hide()
                            } else {
                                binding.icArrowPreviousProduct.show()
                            }

                            // set ratings list in adapter
                            ratingsProductAdapter.submitData(productDetailsResponse.data!!.ratings)
                        }
                    }
                }

                else -> {
                    Timber.e(it.message)
                }
            }
        }

        setBtnListener()
        subscribeData()
    }

    private fun setBtnListener() {
        binding.rvRatingsProduct.apply {
            ratingsProductAdapter = RatingsProductAdapter(onItemClickCallBack)
            adapter = ratingsProductAdapter
        }

        binding.imgSingleSlide.setOnClickListener {
            binding.addToCartLayout.visibility = View.GONE

            val imagesAdsUrlList: ArrayList<String> = ArrayList()
            imagesAdsUrlList.add(productDetailsResponse.data?.imagesIds?.get(0)?.url.toString())
            val bundle = Bundle()
            bundle.putStringArrayList("COVERS_URL_LIST", imagesAdsUrlList)

            val fragment = ImagesPreviewFragment.newInstance(bundle)

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null) // Optional: Add the transaction to the back stack
                .commit()
        }

        binding.ibBack.setOnClickListener {
            // requireActivity().finish()
            requireActivity().onBackPressed()
        }

        binding.icFavorite.setOnClickListener {
            if (viewModel.productItem.isFavorite == false) {
                viewModel.addAndRemoveFavorite()
            } else {
                viewModel.addAndRemoveFavorite()
            }
        }

        binding.btnWatchingVideo.setOnClickListener {
            binding.addToCartLayout.visibility = View.GONE

            val dataToPass = viewModel.productItem.video.toString()

            val bundle = Bundle()
            bundle.putString("videoUrl", dataToPass)

            val fragment = VideoPreviewFragment()
            fragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnShowMoreRatingsProduct.setOnClickListener {
            binding.addToCartLayout.visibility = View.GONE

            val bundle = Bundle()
            bundle.putParcelable("PRODUCT_ITEM", viewModel.productItem)

            val fragment = RatingsProductFragment()
            fragment.arguments = bundle

            // Get the FragmentManager and start a FragmentTransaction
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.container, fragment) // Replace the current fragment with the new one
                .addToBackStack(null)// Add the transaction to the back stack (optional)
                .commit() // Commit the transaction
        }

        binding.icShare.setOnClickListener {
            generateDynamicLink()
        }

        binding.icArrowNextProduct.setOnClickListener {
            onResume()
            viewModel.getProductDetails(viewModel.productItem.nextId ?: 0)
        }

        binding.icArrowPreviousProduct.setOnClickListener {
            onResume()
            viewModel.getProductDetails(viewModel.productItem.prevId ?: 0)
        }
    }

    private fun generateDynamicLink() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://dev.brandsin.net/product?product_Id=${viewModel.productItem.id}&check=Simple")) // Replace with your deep link URL
            .setDomainUriPrefix("https://brandsin.page.link") // Replace with your Firebase Dynamic Links domain
            .setAndroidParameters( // + "/product-details?product=" + product_id;
                DynamicLink.AndroidParameters.Builder(requireActivity().packageName)
                    .setFallbackUrl(Uri.parse("YOUR_FALLBACK_URL_HERE"))
                    .setMinimumVersion(1) // Optional: Minimum app version required
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(viewModel.productItem.name ?: "") // "Your Title"
                    // .setDescription("Your Description")
                    .setImageUrl(
                        Uri.parse(
                            viewModel.productItem.image ?: ""
                        )
                    ) // Optional: Image URL for sharing ("https://www.example.com/image.png")
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
                }
            }
    }

    private fun subscribeData() {
        viewModel.addAndRemoveFavoriteResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    showToast(it.data?.message.toString(), 2)
                    if (product.id != null) {
                        viewModel.getProductDetails(product.id!!)
                    } else {
                        viewModel.getProductDetails(productSearch.id!!)
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

    override fun onChanged(value: Any?) {
        when (value) {
            null -> return
            else -> when (value) {
                Codes.ADD_TO_CART -> {
                    val intent = Intent()
                    intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
                    val item = CartItem()
                    item.run {
                        productId = productDetailsResponse.data!!.id
                        productName = productDetailsResponse.data?.name.toString()
                        productDescription = productDetailsResponse.data?.description.toString()
                        productImage = productDetailsResponse.data?.image.toString()
                        productQuantity = viewModel.count
                        productUnitPrice = viewModel.unitPrice // .toDouble() // unit price
                        productTotalPrice = viewModel.totalPrice // .toDouble()
                        productItemPrice =
                            viewModel.unitPrice // .toDouble() // addons + unit price (this product simple without addons)
                        skuCode = viewModel.skuCode

                        if (viewModel.obsNotes.get() != null) {
                            userNotes = viewModel.obsNotes.get()!!
                        }

                        pickUpFromStore =
                            productDetailsResponse.data?.store?.pickUpFromStore
                        cashOnDelivery =
                            productDetailsResponse.data?.store?.cashOnDelivery

                        type = "simple"
                        isOffer = false
                    }

                    val cartParcelableClass = CartParcelableClass()
                    cartParcelableClass.run {
                        cartItem = item
                        storeProductItem = productDetailsResponse.data!!
                    }

                    if (product.id != null) {
                        intent.putExtra(Params.STORE_PRODUCT_ITEM, cartParcelableClass)
                        requireActivity().setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
                        requireActivity().finish()

                    } else if (productSearch.id != null) {
                        intent.putExtra(Params.SEARCH_PRODUCT_ITEM, cartParcelableClass)
                        requireActivity().setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
                        requireActivity().finish()
                    } else {
                        intent.putExtra(Params.STORE_PRODUCT_ITEM, cartParcelableClass)
                        requireActivity().setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private fun setupSlider(images: List<ImagesIdsItem?>?) {
        if (images != null && images.size > 1) {
            binding.bannerSlider.visibility = View.VISIBLE
            binding.imgSingleSlide.visibility = View.GONE

            viewModel.bannersAdapter.updateList(images as List<ImagesIdsItem>)
            binding.bannerSlider.setSliderAdapter(viewModel.bannersAdapter)
            binding.bannerSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
            binding.bannerSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            binding.bannerSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            binding.bannerSlider.indicatorSelectedColor = Color.WHITE
            binding.bannerSlider.indicatorUnselectedColor = Color.GRAY
            binding.bannerSlider.scrollTimeInSec = 3
            binding.bannerSlider.isAutoCycle = true
            binding.bannerSlider.startAutoCycle()
        } else {
            binding.bannerSlider.visibility = View.GONE
            binding.imgSingleSlide.visibility = View.VISIBLE

            Glide.with(this)
                .load(images?.get(0)?.url)
                .error(R.drawable.app_logo)
                .into(binding.imgSingleSlide)
        }
    }

    override fun onBannersClicked(position: Int, imagesIdsItem: List<ImagesIdsItem?>?) {
        binding.addToCartLayout.visibility = View.GONE

        val imagesAdsUrlList: ArrayList<String> = ArrayList()
        imagesIdsItem?.forEach {
            imagesAdsUrlList.add(it?.url ?: "")
        }
        val bundle = Bundle()
        bundle.putStringArrayList("COVERS_URL_LIST", imagesAdsUrlList)

        val fragment = ImagesPreviewFragment.newInstance(bundle)

        // Pass a tag to addToBackStack
        // val transactionTag = "YourTransactionTag"

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null) // Optional: Add the transaction to the back stack
            .commit()
    }

    override fun onResume() {
        super.onResume()
        binding.addToCartLayout.visibility = View.VISIBLE
    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            *//* Ask user to clear old cart items which contains items with different store *//*
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
                                                storeViewModel.cartItemsList?.clear()
                                                if (cartItem != null) {
                                                    storeViewModel.addProductToCart(cartItem)
                                                    requireActivity().finish()
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

    //                    when (PrefMethods.getUserCart()?.cartStoreData?.storeId == viewModel.productItem.storeId) {
//                        true -> {
//                            // Cart is empty .. This is the first product added to cart
//                            storeViewModel.addProductToCart(item)
//                        }
//
//                        else -> {
//                            // Cart is Not empty But the saved products NOT has the same store Id >> Clear the cart before adding new products
//                            if (PrefMethods.getUserCart()?.cartStoreData?.storeId != viewModel.productItem.storeId) {
//                                val bundle = Bundle()
//                                bundle.putString(
//                                    Params.DIALOG_CONFIRM_MESSAGE,
//                                    MyApp.getInstance()
//                                        .getString(R.string.add_item_to_cart)
//                                )
//                                bundle.putString(
//                                    Params.DIALOG_CONFIRM_POSITIVE,
//                                    MyApp.getInstance()
//                                        .getString(R.string.confirm)
//                                )
//                                bundle.putString(
//                                    Params.DIALOG_CONFIRM_NEGATIVE,
//                                    MyApp.getInstance()
//                                        .getString(R.string.ignore)
//                                )
//                                bundle.putParcelable(
//                                    Params.DIALOG_STORE_ITEM,
//                                    cartParcelableClass // PrefMethods.getUserCart()?.cartStoreData
//                                )
//                                Utils.startDialogActivity(
//                                    requireActivity(),
//                                    DialogConfirmFragment::class.java.name,
//                                    Codes.DIALOG_CONFIRM_REQUEST,
//                                    bundle
//                                )
//                            }
//                        }
//                    }


    */
}
