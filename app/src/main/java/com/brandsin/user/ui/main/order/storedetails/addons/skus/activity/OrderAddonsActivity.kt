package com.brandsin.user.ui.main.order.storedetails.addons.skus.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.ActivityOrderAddonsBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.SearchProdactAttr.StoreItemColors
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartParcelableClass
import com.brandsin.user.model.order.productdetails.ProductDetailsResponse
import com.brandsin.user.model.order.searchproducts.SearchProductItem
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.brandsin.user.model.order.storedetails.StoreAddonsItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.Status
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.ui.main.order.reviewStore.model.RatingItem
import com.brandsin.user.ui.main.order.storedetails.addons.addons.VideoPreviewFragment
import com.brandsin.user.ui.main.order.storedetails.addons.ratingsProduct.RatingsProductFragment
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.adapter.RatingsProductAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.banners.BannersAddonsAdapter
import com.brandsin.user.ui.main.search.ImagesPreviewFragment
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import com.brandsin.user.utils.storyviewer.utils.hide
import com.brandsin.user.utils.storyviewer.utils.show
import com.bumptech.glide.Glide
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import timber.log.Timber

class OrderAddonsActivity : AppCompatActivity(), Observer<Any?>,
    BannersAddonsAdapter.OnBannerAddonsClickedListener {

    private lateinit var binding: ActivityOrderAddonsBinding

    private lateinit var viewModel: OrderAddonsViewModel

    private var product = StoreProductItem()
    private var productSearch: SearchProductItem = SearchProductItem()
    var isSizeSelected: Boolean = false

    private var addonsList: ArrayList<String> = ArrayList()
    private var addonsNamesList: ArrayList<String> = ArrayList()

    // private lateinit var sliderView: SliderView

    private var skuSelected: StoreItemColors? = null

    private var productDetailsResponse = ProductDetailsResponse()
    var productId: Int = -1

    private lateinit var ratingsProductAdapter: RatingsProductAdapter

    private val onItemClickCallBack: (ratingsItem: RatingItem) -> Unit =
        { ratingsItem ->
            if (ratingsItem.video?.isNotEmpty() == true) {
                binding.addToCartLayout.visibility = View.GONE

                val bundle = Bundle()
                bundle.putString("videoUrl", ratingsItem.video.toString())

                val fragment = VideoPreviewFragment()
                fragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()

                // Dismiss the dialog (optional)
                finish()

            } else if (ratingsItem.image?.isNotEmpty() == true) {
                binding.addToCartLayout.visibility = View.GONE

                val imagesAdsUrlList: ArrayList<String> = ArrayList()
                imagesAdsUrlList.add(ratingsItem.image)
                val bundle = Bundle()
                bundle.putStringArrayList("COVERS_URL_LIST", imagesAdsUrlList)

                val fragment = ImagesPreviewFragment.newInstance(bundle)

                // Pass a tag to addToBackStack
                // val transactionTag = "YourTransactionTag"

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Optional: Add the transaction to the back stack
                    .commit()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_addons)

        viewModel = ViewModelProvider(this)[OrderAddonsViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.setBannerAddonsListener(this)

        initView()

        // Add an OnBackStackChangedListener to the fragment manager
        supportFragmentManager.addOnBackStackChangedListener {
            // Check if the back stack is empty
            val isBackStackEmpty = supportFragmentManager.backStackEntryCount == 0

            // Update the visibility of addToCartLayout based on the back stack status
            if (isBackStackEmpty) {
                binding.addToCartLayout.visibility = View.VISIBLE
            } else {
                binding.addToCartLayout.visibility = View.GONE
            }
        }

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.offers_bg_color)
            }
        }

        // Retrieve the Bundle from the Intent
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val storeProductItem: Parcelable? = bundle.getParcelable(Params.STORE_PRODUCT_ITEM)
            val searchProductItem: Parcelable? = bundle.getParcelable(Params.SEARCH_PRODUCT_ITEM)
            val shareProductItem: String = bundle.getString(Params.STORE_PRODUCT_ITEM) ?: ""
            when { // bundle.getParcelable(Params.STORE_PRODUCT_ITEM) != null
                storeProductItem != null -> {
                    product =
                        intent.extras?.getParcelable(Params.STORE_PRODUCT_ITEM)
                            ?: StoreProductItem()
                    viewModel.obsIsVisible.set(true)
                    productId = product.id!!
                    viewModel.getSearchProductAttr(productId)
                    viewModel.getProductDetails(product.id!!)
                }

                searchProductItem != null -> { // bundle.getSerializable(Params.SEARCH_PRODUCT_ITEM) != null
                    productSearch =
                        intent.extras?.getParcelable(Params.SEARCH_PRODUCT_ITEM)
                            ?: SearchProductItem()
                    viewModel.obsIsVisible.set(true)
                    productId = productSearch.id!!
                    viewModel.getSearchProductAttr(productId)
                    viewModel.getProductDetails(productSearch.id!!)
                }

                shareProductItem.isNotEmpty() -> {
                    viewModel.obsIsVisible.set(true)
                    productId = shareProductItem.toInt()
                    viewModel.getSearchProductAttr(productId)
                    viewModel.getProductDetails(productId)
                }
            }
        }

        viewModel.mutableLiveData.observe(this, this)

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

                            if (productDetailsResponse.data?.imagesIds?.isNotEmpty() == true) {
                                // dep
                                setupSlider(productDetailsResponse.data!!.imagesIds)
                            }

                            if (productDetailsResponse.data?.addons?.isEmpty() == true) {
                                binding.textChooseAddons.visibility = View.GONE
                                binding.textChooseAddonsDesc.visibility = View.GONE
                                binding.addonsLayout.visibility = View.GONE
                            } else {
                                binding.textChooseAddons.visibility = View.VISIBLE
                                binding.textChooseAddonsDesc.visibility = View.VISIBLE
                                binding.addonsLayout.visibility = View.VISIBLE
                            }

                            if (productDetailsResponse.data?.isFavorite == false) {
                                binding.icFavorite.setImageResource(R.drawable.ic_favorite_un_selected)
                            } else {
                                binding.icFavorite.setImageResource(R.drawable.ic_favorite_selected)
                            }

                            //dep
                            viewModel.setProductData(productDetailsResponse.data!!)
                            binding.tvMealName.text = productDetailsResponse.data!!.name
                            binding.tvMealDescription.text =
                                Utils.html2text(productDetailsResponse.data!!.description.toString())

                            binding.numberOfRatings.text =
                                productDetailsResponse.data?.ratings?.size.toString() + " " + getString(
                                    R.string.review
                                )

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

        /*observe(viewModel.skuAdapter.orderSkusLiveData) {
            when (it) {
                is StoreSkusItem -> {
                    if (!isSizeSelected) {
                        isSizeSelected
                    }
                    viewModel.selectedSkuCode = it.code!!
                    viewModel.selectedSkuName = it.name!!
                    viewModel.skuPrice = it.price!!.toDouble()
                    viewModel.itemPrice = viewModel.skuPrice + viewModel.addonsPrice
                    viewModel.obsItemPrice.set(viewModel.skuPrice * viewModel.obsCount.get()!!)
                    viewModel.obsTotalPrice.set(viewModel.itemPrice * viewModel.obsCount.get()!!)
                }
            }
        }*/

        observe(viewModel.addonsAdapter.orderAddonsLiveData) {
            when (it) {
                is StoreAddonsItem -> {
                    when {
                        // add addon price to AllAddonsPrice then change the total append on this changes
                        it.isSelected -> {
                            viewModel.addonsPrice = viewModel.addonsPrice + it.price!!.toDouble()
                            viewModel.itemPrice = viewModel.skuPrice + viewModel.addonsPrice
                            viewModel.obsItemPrice.set(viewModel.skuPrice * viewModel.obsCount.get()!!)
                            viewModel.obsTotalPrice.set(viewModel.itemPrice * viewModel.obsCount.get()!!)
                            addonsList.add(it.id!!.toInt().toString())
                            addonsNamesList.add(it.name.toString())
                        }

                        else -> {
                            viewModel.addonsPrice = viewModel.addonsPrice - it.price!!.toDouble()
                            viewModel.itemPrice = viewModel.skuPrice + viewModel.addonsPrice
                            viewModel.obsItemPrice.set(viewModel.skuPrice * viewModel.obsCount.get()!!)
                            viewModel.obsTotalPrice.set(viewModel.itemPrice * viewModel.obsCount.get()!!)
                            addonsList.remove(it.id!!.toInt().toString())
                            addonsNamesList.remove(it.name.toString())
                        }
                    }
                }
            }
        }

        observe(viewModel.skuParentAdapter.orderSkusLiveData) {
            skuSelected = it
            viewModel.getSearchProductAttrSelected(
                productId,
                it?.skuId.toString(),
                it?.attributeId.toString(),
                it?.numberValue.toString()
            )
        }

        viewModel.getSearchProductAttrSelected(
            productId,
            viewModel.skuParentAdapter.selectedSkuId.toString(),
            viewModel.skuParentAdapter.selectedAttributeId.toString(),
            viewModel.skuParentAdapter.selectedNumberValue.toString(),
        )

        subscribeData()

        setBtnListener()
    }

    private fun initView() {
        binding.addToCartLayout.visibility = View.VISIBLE
        binding.rvRatingsProduct.apply {
            ratingsProductAdapter = RatingsProductAdapter(onItemClickCallBack)
            adapter = ratingsProductAdapter
        }

        if (!viewModel.productItem.video.isNullOrEmpty()) {
            binding.btnWatchingVideo.visibility = View.VISIBLE
        }
    }

    private fun setBtnListener() {
        binding.ibBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
            setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
            finish()
        }

        binding.imgSingleSlide.setOnClickListener {
            binding.addToCartLayout.visibility = View.GONE

            val imagesAdsUrlList: ArrayList<String> = ArrayList()
            imagesAdsUrlList.add(productDetailsResponse.data?.imagesIds?.get(0)?.url.toString())
            val bundle = Bundle()
            bundle.putStringArrayList("COVERS_URL_LIST", imagesAdsUrlList)

            val fragment = ImagesPreviewFragment.newInstance(bundle)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Optional: Add the transaction to the back stack
                .commit()
        }

        binding.icFavorite.setOnClickListener {
            if (viewModel.productItem.isFavorite == false) {
                // call api
                viewModel.addAndRemoveFavorite()
            } else {
                viewModel.addAndRemoveFavorite()
                // call api
            }
        }

        binding.btnWatchingVideo.setOnClickListener {
            binding.addToCartLayout.visibility = View.GONE
            val dataToPass = viewModel.productItem.video.toString()

            val bundle = Bundle()
            bundle.putString("videoUrl", dataToPass)

            val fragment = VideoPreviewFragment()
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

            // Dismiss the dialog (optional)
            finish()
        }

        binding.btnShowMoreRatingsProduct.setOnClickListener {
            binding.addToCartLayout.visibility = View.GONE
            binding.productHeaderLayout.visibility = View.GONE

            val bundle = Bundle()
            bundle.putParcelable("PRODUCT_ITEM", viewModel.productItem)

            val fragment = RatingsProductFragment()
            fragment.arguments = bundle

            // Get the FragmentManager and start a FragmentTransaction
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
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
            //https://dev.brandsin.net/product?product_Id?check
//            Timber.e("link is ${}")
            .setLink(Uri.parse("https://dev.brandsin.net/product?product_Id=${viewModel.productItem.id}&check=Variable")) // Replace with your deep link URL
            .setDomainUriPrefix("https://brandsin.page.link") // Replace with your Firebase Dynamic Links domain
            .setAndroidParameters( // + "/product-details?product=" + product_id;
                DynamicLink.AndroidParameters.Builder(this.packageName)
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
        viewModel.addAndRemoveFavoriteResponse.observe(this) {
            when (it) {
                is ResponseHandler.Success -> {
                    showToast(it.data?.message.toString(), 2)
                    viewModel.getProductDetails(productId)
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
            else -> value.let {
                when (it) {
                    is Int -> {
                        when (it) {
                            Codes.BACK_PRESSED -> {
                                val intent = Intent()
                                intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                                setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
                                finish()
                            }

                            Codes.ADD_TO_CART -> {
                                if (viewModel.searchProductAttr.price != null) {
                                    when (viewModel.searchProductAttr.price?.toDouble()) {
                                        0.0 -> {
                                            val bundle = Bundle()
                                            bundle.putString(
                                                Params.DIALOG_TOAST_MESSAGE,
                                                MyApp.getInstance()
                                                    .getString(R.string.please_select_order_size)
                                            )
                                            bundle.putInt(Params.DIALOG_TOAST_TYPE, 1)
                                            Utils.startDialogActivity(
                                                this,
                                                DialogToastFragment::class.java.name,
                                                Codes.DIALOG_TOAST_REQUEST,
                                                bundle
                                            )
                                        }

                                        else -> {
                                            val intent = Intent()
                                            intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
                                            val item = CartItem()

                                            item.run {
                                                productId = productDetailsResponse.data!!.id
                                                productName = productDetailsResponse.data!!.name
                                                productDescription =
                                                    productDetailsResponse.data!!.description
                                                productImage =
                                                    productDetailsResponse.data!!.image
                                                productQuantity = viewModel.obsCount.get()!!
                                                productUnitPrice =
                                                    viewModel.searchProductAttr.price!!.toDouble()//skuPrice // unit price
                                                addonsPrice = viewModel.addonsPrice // addons
                                                productItemPrice =
                                                    viewModel.searchProductAttr.price!!.toDouble() + viewModel.addonsPrice //viewModel.skuPrice + viewModel.addonsPrice // addons + unit price
                                                productTotalPrice =
                                                    viewModel.obsTotalPrice.get() // item price * quantity

                                                skuCode =
                                                    viewModel.searchProductAttr.code
                                                        ?: "" // viewModel.selectedSkuCode
                                                skuName =
                                                    viewModel.searchProductAttr.name
                                                        ?: "" //viewModel.selectedSkuName

                                                if (viewModel.obsNotes.get() != null) {
                                                    userNotes = viewModel.obsNotes.get()!!
                                                }

                                                pickUpFromStore =
                                                    productDetailsResponse.data?.store?.pickUpFromStore
                                                cashOnDelivery =
                                                    productDetailsResponse.data?.store?.cashOnDelivery

                                                addonsIds = addonsList
                                                addonsNames = addonsNamesList
                                                type = "variable"
                                                isOffer = false
                                            }

                                            val cartParcelableClass = CartParcelableClass()
                                            cartParcelableClass.run {
                                                cartItem = item
                                                storeProductItem = productDetailsResponse.data!!
                                            }

                                            if (product.id != null) {
                                                intent.putExtra(
                                                    Params.STORE_PRODUCT_ITEM, cartParcelableClass
                                                )
                                                setResult(
                                                    Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent
                                                )
                                                finish()
                                            } else if (productSearch.id != null) {
                                                intent.putExtra(
                                                    Params.SEARCH_PRODUCT_ITEM, cartParcelableClass
                                                )
                                                setResult(
                                                    Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent
                                                )
                                                finish()
                                            } else {
                                                intent.putExtra(
                                                    Params.STORE_PRODUCT_ITEM, cartParcelableClass
                                                )
                                                setResult(
                                                    Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent
                                                )
                                                finish()
                                            }
                                        }
                                    }
                                } else {
                                    showToast(getString(R.string.please_select_order_size), 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupSlider(images: List<ImagesIdsItem?>?) {
        if (images != null && images.size > 1) {
            binding.bannerSlider.visibility = View.VISIBLE
            binding.imgSingleSlide.visibility = View.GONE

            viewModel.bannersAddonsAdapter.updateList(images as ArrayList<ImagesIdsItem>)
            // sliderView = findViewById(R.id.bannerSlider)
            binding.bannerSlider.setSliderAdapter(viewModel.bannersAddonsAdapter)
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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }

        // val intent = Intent()
        // intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
        // setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
        // finish()
    }

    // Fonts
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { ViewPumpContextWrapper.wrap(it) })
    }

    fun showToast(msg: String, type: Int) {
        // Success 2
        // False  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(
            this,
            DialogToastFragment::class.java.name,
            Codes.DIALOG_TOAST_REQUEST,
            bundle
        )
    }

    override fun onBannerAddonsClicked(position: Int, imagesIdsItem: List<ImagesIdsItem?>?) {
        binding.addToCartLayout.visibility = View.GONE

        val imagesAdsUrlList: ArrayList<String> = ArrayList()
        imagesIdsItem?.forEach {
            imagesAdsUrlList.add(it?.url ?: "")
        }
        val bundle = Bundle()
        bundle.putStringArrayList("COVERS_URL_LIST", imagesAdsUrlList)
        val fragment = ImagesPreviewFragment.newInstance(bundle) // findNavController().navigate(R.id.imagesPreviewFragment, bundle)

        // Pass a tag to addToBackStack
        // val transactionTag = "YourTransactionTag"
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Optional: Add the transaction to the back stack
            .commit()
    }

    /*@Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                                                    finish()
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

    if (PrefMethods.getUserCart()?.cartStoreData?.storeId == viewModel.productItem.storeId) {
        // Cart is empty .. This is the first product added to cart
        storeViewModel.addProductToCart(item)
    }
    // Cart is Not empty But the saved products NOT has the same store Id >> Clear the cart before adding new products
    else if (PrefMethods.getUserCart()?.cartStoreData?.storeId != viewModel.productItem.storeId) {
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
            cartParcelableClass // PrefMethods.getUserCart()?.cartStoreData
        )
        Utils.startDialogActivity(
            this,
            DialogConfirmFragment::class.java.name,
            Codes.DIALOG_CONFIRM_REQUEST,
            bundle
        )
    } else {
        // Cart is Not empty and the all products has the sam Id
        storeViewModel.addProductToCart(item)
    }*/
}