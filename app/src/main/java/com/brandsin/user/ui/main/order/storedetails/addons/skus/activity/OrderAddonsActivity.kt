package com.brandsin.user.ui.main.order.storedetails.addons.skus.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
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
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartParcelableClass
import com.brandsin.user.model.order.productdetails.*
import com.brandsin.user.model.order.searchproducts.SearchProductItem
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.brandsin.user.model.order.storedetails.StoreAddonsItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.model.order.storedetails.StoreSkusItem
import com.brandsin.user.network.Status
import com.brandsin.user.ui.dialogs.confirm.DialogConfirmFragment
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.jsoup.Jsoup
import timber.log.Timber

class OrderAddonsActivity : AppCompatActivity(), Observer<Any?>
{
    private lateinit var viewModel: OrderAddonsViewModel
    private lateinit var binding: ActivityOrderAddonsBinding
    var product = StoreProductItem()
    var productSearch = SearchProductItem()
    var isSizeSelected : Boolean = false
    var addonsList: ArrayList<String> = ArrayList()
    var addonsNamesList: ArrayList<String> = ArrayList()
    lateinit var sliderView : SliderView
    var productDetailsResponse = ProductDetailsResponse()
    var productid:Int=-1;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_addons)

        viewModel = ViewModelProvider(this).get(OrderAddonsViewModel::class.java)
        binding.viewModel = viewModel


        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this , R.color.offers_bg_color)
            }
        }

        binding.ibBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
            setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
            finish()
        }

        //dehhhhh
        val bundle = intent.extras
        when {
            bundle!!.getSerializable(Params.STORE_PRODUCT_ITEM) != null -> {
                product = (intent.extras!!.getSerializable(Params.STORE_PRODUCT_ITEM) as StoreProductItem?)!!
                viewModel.obsIsVisible.set(true)
                productid=product.id!!
                viewModel.getSearchProductAtrr(productid)
                viewModel.getProductDetails(product.id!!)
            }
            bundle.getSerializable(Params.SEARCH_PRODUCT_ITEM) != null -> {
                productSearch = (intent.extras!!.getSerializable(Params.SEARCH_PRODUCT_ITEM) as SearchProductItem?)!!
                viewModel.obsIsVisible.set(true)
                productid=productSearch.id!!
                viewModel.getSearchProductAtrr(productid)
                viewModel.getProductDetails(productSearch.id!!)
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

                            if ( productDetailsResponse.data!!.imagesIds!!.isNotEmpty()) {
                                //dep
                                setupSlider(productDetailsResponse.data!!.imagesIds)
                            }
                            if (productDetailsResponse.data!!.addons!!.isNullOrEmpty()) {
                                binding.textChooseAddons.visibility = View.GONE
                                binding.textChooseAddonsDesc.visibility = View.GONE
                                binding.addonsLayout.visibility = View.GONE
                            }else{
                                binding.textChooseAddons.visibility = View.VISIBLE
                                binding.textChooseAddonsDesc.visibility = View.VISIBLE
                                binding.addonsLayout.visibility = View.VISIBLE
                            }
                            //dep
                            viewModel.setProductData(productDetailsResponse.data!!)
                            binding.tvMealName.text = productDetailsResponse.data!!.name
                            binding.tvMealDescription.text =html2text(productDetailsResponse.data!!.description.toString())

                        }
                    }
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }

//        observe(viewModel.skuAdapter.orderSkusLiveData) {
//            when (it) {
//                is StoreSkusItem -> {
//                    if (!isSizeSelected) {
//                        isSizeSelected
//                    }
//                    viewModel.selectedSkuCode = it.code!!
//                    viewModel.selectedSkuName = it.name!!
//                    viewModel.skuPrice = it.price!!.toDouble()
//                    viewModel.itemPrice = viewModel.skuPrice + viewModel.addonsPrice
//                    viewModel.obsItemPrice.set(viewModel.skuPrice * viewModel.obsCount.get()!!)
//                    viewModel.obsTotalPrice.set(viewModel.itemPrice * viewModel.obsCount.get()!!)
//                }
//            }
//        }
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
        observe(viewModel.SkuParentAdapter.orderSkusLiveData) {
            viewModel.getSearchProductAtrrSelected(productid,it!!.sku_id.toString(),it!!.attribute_id.toString(),it!!.number_value.toString())
        }

    }

    override fun onChanged(it: Any?)
    {
        when (it) {
            null -> return
            else -> it.let {
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
                                if(viewModel.search_product_attr.price!=null) {
                                    when (viewModel.search_product_attr.price!!.toDouble()) {
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
                                                productImage = productDetailsResponse.data!!.image
                                                productQuantity = viewModel.obsCount.get()!!
                                                productUnitPrice =
                                                    viewModel.search_product_attr.price!!.toDouble()//skuPrice // unit price
                                                addonsPrice = viewModel.addonsPrice // addons
                                                productItemPrice =
                                                    viewModel.search_product_attr.price!!.toDouble() + viewModel.addonsPrice //viewModel.skuPrice + viewModel.addonsPrice // addons + unit price
                                                productTotalPrice =
                                                    viewModel.obsTotalPrice.get() // item price * quantity
                                                Log.d("codeS", skuCode.toString())
                                                skuCode =
                                                    viewModel.search_product_attr.code!! //viewModel.selectedSkuCode
                                                skuName =
                                                    viewModel.search_product_attr.name!! //viewModel.selectedSkuName
                                                if (viewModel.obsNotes.get() != null) {
                                                    userNotes = viewModel.obsNotes.get()!!
                                                }
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
                                                    Params.STORE_PRODUCT_ITEM,
                                                    cartParcelableClass
                                                )
                                                setResult(
                                                    Codes.SELECT_ORDER_ADDONS_ACTIVITY,
                                                    intent
                                                )
                                                finish()
                                            } else {
                                                intent.putExtra(
                                                    Params.SEARCH_PRODUCT_ITEM,
                                                    cartParcelableClass
                                                )
                                                setResult(
                                                    Codes.SELECT_ORDER_ADDONS_ACTIVITY,
                                                    intent
                                                )
                                                finish()
                                            }
                                        }
                                    }
                                }else{
                                    showToast(getString(R.string.please_select_order_size),1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun setupSlider(images: List<ImagesIdsItem?>?) {
        viewModel.bannersAdapter.updateList(images as List<ImagesIdsItem>)
        sliderView = findViewById(R.id.bannerSlider)
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

     fun html2text(html:String):String {
        try {
            return Jsoup.parse(html).text();
        } catch (ignored: Exception) {
            return "";
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent()
        intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
        setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
        finish()
    }
    //fonts
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { ViewPumpContextWrapper.wrap(it) })
    }
    fun showToast(msg : String, type : Int) {
        //succss 2
        //false  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(this, DialogToastFragment::class.java.name, Codes.DIALOG_TOAST_REQUEST, bundle)
    }
}