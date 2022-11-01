package com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.DialogOrderAddonsBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartParcelableClass
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class DialogOrderAddonsFragment  : DialogFragment(), Observer<Any?>
{
    lateinit  var  binding: DialogOrderAddonsBinding
    lateinit var viewModel: DialogOrderAddonsViewModel
    private lateinit var productItem : StoreProductItem

    lateinit var sliderView : SliderView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        when {
            arguments != null -> {
                when {
                    requireArguments().containsKey(Params.STORE_PRODUCT_ITEM) -> {
                        productItem = (requireArguments().getSerializable(Params.STORE_PRODUCT_ITEM) as StoreProductItem?)!!
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogOrderAddonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DialogOrderAddonsViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.productItem = productItem
        binding.tvMealName.text = productItem.name
        binding.tvMealDescription.text = productItem.description
        viewModel.getProductPrice()
        viewModel.getSkuCode()

        if ( productItem.imagesIds!!.isNotEmpty()) {
            setupSlider(productItem.imagesIds)
        }

        binding.ibBack.setOnClickListener {
            requireActivity().finish()
        }

        viewModel.mutableLiveData.observe(this, this)
    }

    override fun onChanged(it: Any?)
    {
        when (it) {
            null -> return
            else -> when (it) {
                Codes.ADD_TO_CART -> {
                    val intent = Intent()
                    intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
                    val item = CartItem()
                    item.run {
                        productId = productItem.id
                        productName = productItem.name
                        productDescription = productItem.description
                        productImage = productItem.image
                        productQuantity = viewModel.count
                        productUnitPrice = viewModel.unitPrice // unit price
                        productTotalPrice = viewModel.totalPrice
                        productItemPrice = viewModel.unitPrice // addons + unit price (this product simple without addons)
                        skuCode = viewModel.skuCode
                        if (viewModel.obsNotes.get() != null) {
                            userNotes = viewModel.obsNotes.get()!!
                        }
                        type = "simple"
                        isOffer = false
                    }

                    val cartParcelableClass = CartParcelableClass()
                    cartParcelableClass.run {
                        cartItem = item
                        storeProductItem = productItem
                    }

                    intent.putExtra(Params.STORE_PRODUCT_ITEM, cartParcelableClass)
                    requireActivity().setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
                    requireActivity().finish()
                }
            }
        }
    }
    private fun setupSlider(images: List<ImagesIdsItem?>?) {
        viewModel.bannersAdapter.updateList(images as List<ImagesIdsItem>)
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
}