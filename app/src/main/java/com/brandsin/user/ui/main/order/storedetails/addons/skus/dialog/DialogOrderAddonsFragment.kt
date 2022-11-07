package com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.databinding.DialogOrderAddonsBinding
import com.brandsin.user.R
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartParcelableClass
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.brandsin.user.model.order.productdetails.ProductDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.model.order.searchproducts.SearchProductItem
import com.brandsin.user.network.Status
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.jsoup.Jsoup
import timber.log.Timber

class DialogOrderAddonsFragment  : DialogFragment(), Observer<Any?>
{
    lateinit  var  binding: DialogOrderAddonsBinding
    lateinit var viewModel: DialogOrderAddonsViewModel
    var product = StoreProductItem()
    var productSearch = SearchProductItem()
    lateinit var sliderView : SliderView
    var productDetailsResponse = ProductDetailsResponse()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        when {
            arguments != null -> {
                when {
                    requireArguments().containsKey(Params.STORE_PRODUCT_ITEM) -> {
                        product = (requireArguments().getSerializable(Params.STORE_PRODUCT_ITEM) as StoreProductItem?)!!
                    }
                    requireArguments().containsKey(Params.SEARCH_PRODUCT_ITEM) -> {
                        productSearch = (requireArguments().getSerializable(Params.SEARCH_PRODUCT_ITEM) as SearchProductItem?)!!
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

        viewModel.obsIsVisible.set(true)

        if (product.id !=null) {
            viewModel.getProductDetails(product.id!!)
        }else{
            viewModel.getProductDetails(productSearch.id!!)
        }

        binding.ibBack.setOnClickListener {
            requireActivity().finish()
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

                            binding.tvMealName.text = productDetailsResponse.data!!.name
                            binding.tvMealDescription.text = productDetailsResponse.data!!.description
                            binding.tvMealDescription.text = html2text(productDetailsResponse.data!!.description.toString())

                            if ( productDetailsResponse.data!!.imagesIds!!.isNotEmpty()) {
                                setupSlider(productDetailsResponse.data!!.imagesIds)
                            }

                        }
                    }
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }

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
                        productId = productDetailsResponse.data!!.id
                        productName = productDetailsResponse.data!!.name
                        productDescription = productDetailsResponse.data!!.description
                        productImage = productDetailsResponse.data!!.image
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
                        storeProductItem = productDetailsResponse.data!!
                    }

                    if (product.id !=null) {
                        intent.putExtra(Params.STORE_PRODUCT_ITEM, cartParcelableClass)
                        requireActivity().setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
                        requireActivity().finish()
                    }else{
                        intent.putExtra(Params.SEARCH_PRODUCT_ITEM, cartParcelableClass)
                        requireActivity().setResult(Codes.SELECT_ORDER_ADDONS_ACTIVITY, intent)
                        requireActivity().finish()
                    }

                }
            }
        }
    }

    fun html2text(html:String):String {
        try {
            return Jsoup.parse(html).text();
        } catch (ignored: Exception) {
            return "";
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
    fun showToast(msg : String, type : Int) {
        //succss 2
        //false  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(requireActivity(), DialogToastFragment::class.java.name, Codes.DIALOG_TOAST_REQUEST, bundle)
    }
}