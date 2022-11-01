package com.brandsin.user.ui.main.order.storedetails.addons.skus.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartParcelableClass
import com.brandsin.user.model.order.storedetails.*
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class OrderAddonsActivity : AppCompatActivity(), Observer<Any?>
{
    private lateinit var viewModel: OrderAddonsViewModel
    private lateinit var binding: ActivityOrderAddonsBinding
    lateinit var productItem : StoreProductItem
    var isSizeSelected : Boolean = false
    var addonsList: ArrayList<String> = ArrayList()
    var addonsNamesList: ArrayList<String> = ArrayList()
    lateinit var sliderView : SliderView

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

        val bundle = intent.extras
        when {
            bundle!!.getSerializable(Params.STORE_PRODUCT_ITEM) != null -> {
                productItem = (intent.extras!!.getSerializable(Params.STORE_PRODUCT_ITEM) as StoreProductItem?)!!
                viewModel.productItem = productItem
               if ( productItem.imagesIds!!.isNotEmpty()) {
                    setupSlider(productItem.imagesIds)
                }
                if (productItem.addons!!.isNullOrEmpty()) {
                    binding.textChooseAddons.visibility = View.GONE
                    binding.textChooseAddonsDesc.visibility = View.GONE
                    binding.addonsLayout.visibility = View.GONE
                }else{
                    binding.textChooseAddons.visibility = View.VISIBLE
                    binding.textChooseAddonsDesc.visibility = View.VISIBLE
                    binding.addonsLayout.visibility = View.VISIBLE
                }
                viewModel.setProductData(productItem)
                binding.tvMealName.text = productItem.name
            }
        }

        viewModel.mutableLiveData.observe(this, this)

        observe(viewModel.skuAdapter.orderSkusLiveData) {
            when (it) {
                is StoreSkusItem -> {
                    if (!isSizeSelected) {
                        isSizeSelected
                    }
                    viewModel.selectedSkuCode = it.code!!
                    viewModel.skuPrice = it.price!!.toDouble()
                    viewModel.itemPrice = viewModel.skuPrice + viewModel.addonsPrice
                    viewModel.obsItemPrice.set(viewModel.skuPrice * viewModel.obsCount.get()!!)
                    viewModel.obsTotalPrice.set(viewModel.itemPrice * viewModel.obsCount.get()!!)
                }
            }
        }
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
                                when (viewModel.skuPrice) {
                                    0.0 -> {
                                        val bundle = Bundle()
                                        bundle.putString(Params.DIALOG_TOAST_MESSAGE, MyApp.getInstance().getString(R.string.please_select_order_size))
                                        bundle.putInt(Params.DIALOG_TOAST_TYPE, 1)
                                        Utils.startDialogActivity(this, DialogToastFragment::class.java.name, Codes.DIALOG_TOAST_REQUEST, bundle)
                                    }
                                    else -> {
                                        val intent = Intent()
                                        intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
                                        val item = CartItem()
                                        item.run {
                                            productId = productItem.id
                                            productName = productItem.name
                                            productDescription = productItem.description
                                            productImage = productItem.image
                                            productQuantity = viewModel.obsCount.get()!!
                                            productUnitPrice = viewModel.skuPrice // unit price
                                            addonsPrice = viewModel.addonsPrice // addons
                                            productItemPrice = viewModel.skuPrice + viewModel.addonsPrice // addons + unit price
                                            productTotalPrice = viewModel.obsTotalPrice.get() // item price * quantity
                                            skuCode = viewModel.selectedSkuCode
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
                                            storeProductItem = productItem
                                        }

                                        intent.putExtra(Params.STORE_PRODUCT_ITEM, cartParcelableClass)
                                        setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
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
    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent()
        intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
        setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
        finish()
    }
}