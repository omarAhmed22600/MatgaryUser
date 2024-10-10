package com.brandsin.user.ui.main.order.storedetails.addons.skus.activity

import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.FavoriteResponse
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.SearchProdactAttr.SearchProductAttResponse
import com.brandsin.user.model.order.SearchProdactAttr.Sku
import com.brandsin.user.model.order.SearchProdactAttr.StoreItemColors
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartStoreData
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.productdetails.ProductDetailsResponse
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.brandsin.user.model.order.storedetails.StoreAddonsItem
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.requestCall
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.ui.main.order.storedetails.addons.addons.OrderAddonsAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.banners.BannersAddonsAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.adapter.OrderSkusAdapter
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderAddonsViewModel : BaseViewModel() {

    var productItem = StoreProductItem()

    // var skuAdapter = OrderSkusAdapter()
    var addonsAdapter = OrderAddonsAdapter()
    var skuParentAdapter = OrderSkusAdapter()

    var searchProductAttr = Sku()

    var skuPrice: Double = 0.0  // selected sku price
    var addonsPrice: Double = 0.0  // addons price
    var itemPrice: Double = 0.0 // sku + addons
    val obsItemPrice = ObservableDouble(0.0) // sku * quantity
    val obsTotalPrice = ObservableDouble(0.0) // item price * count
    val obsCount = ObservableField(1)
    var selectedSkuCode: String = ""
    var selectedSkuName: String = ""

    val obsNotes = ObservableField<String>()

    lateinit var bannersAddonsAdapter: BannersAddonsAdapter

    fun setBannerAddonsListener(onBannerClickedListener: OrderAddonsActivity) {
        bannersAddonsAdapter = BannersAddonsAdapter(onBannerClickedListener)
    }

    fun setProductData(item: StoreProductItem) {
        getProductPrice()
        //getProductPrice2()

        //  skuAdapter.updateList(item.skus as ArrayList<StoreSkusItem>)
        addonsAdapter.updateList(item.addons as ArrayList<StoreAddonsItem>)

        //selectedSkuCode = item.skus[0].code!!
        //selectedSkuName = item.skus[0].name!!
        //skuPrice = item.skus[0].price!!.toDouble()

        //selectedSkuCode = search_product_attr.code!!
        //selectedSkuName = search_product_attr.name!!
        //skuPrice = search_product_attr.price!!.toDouble()

        itemPrice = (skuPrice + addonsPrice)
        obsItemPrice.set(skuPrice * obsCount.get()!!)
        obsTotalPrice.set(itemPrice * obsCount.get()!!)
    }

    // add price of item to the total
    fun onPlusClicked() {
        obsCount.set(obsCount.get()!! + 1)
        obsItemPrice.set(skuPrice * obsCount.get()!!)
        obsTotalPrice.set(itemPrice * obsCount.get()!!)
    }

    // subtract item unit price from the calculated total price
    fun onMinusClicked() {
        when {
            obsCount.get()!!.toInt() != 1 -> {
                obsCount.set(obsCount.get()!! - 1)
                obsItemPrice.set(skuPrice * obsCount.get()!!)
                obsTotalPrice.set(itemPrice * obsCount.get()!!)
            }
        }
    }

    private fun getProductPrice() = when (productItem.skus!![0]!!.salePrice) {
        null -> {
            skuPrice = productItem.skus!![0]!!.regularPrice!!.toDouble()
            itemPrice = (skuPrice + addonsPrice)
            obsItemPrice.set(skuPrice * obsCount.get()!!)
            obsTotalPrice.set(itemPrice * obsCount.get()!!)
        }

        else -> {
            skuPrice = productItem.skus!![0]!!.salePrice!!.toDouble()
            itemPrice = (skuPrice + addonsPrice)
            obsItemPrice.set(skuPrice * obsCount.get()!!)
            obsTotalPrice.set(itemPrice * obsCount.get()!!)
        }
    }

    private fun getProductPrice2() = when (searchProductAttr.sale_price) {
        null -> {
            skuPrice = searchProductAttr.regular_price!!.toDouble()
            itemPrice = (skuPrice + addonsPrice)
            obsItemPrice.set(skuPrice * obsCount.get()!!)
            obsTotalPrice.set(itemPrice * obsCount.get()!!)
        }

        else -> {
            skuPrice = searchProductAttr.regular_price?.toDouble() ?: 0.0
            itemPrice = (skuPrice + addonsPrice)
            obsItemPrice.set(skuPrice * obsCount.get()!!)
            obsTotalPrice.set(itemPrice * obsCount.get()!!)
        }
    }

    fun onBackPressed() {
        setValue(Codes.BACK_PRESSED)
    }

    fun onAddToCartClicked() {
        setValue(Codes.ADD_TO_CART)
    }

    fun getProductDetails(productId: Int) {
        obsIsLoading.set(true)
        requestCall<ProductDetailsResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo()
                    .getProductDetails(productId, PrefMethods.getUserData()!!.id!!, PrefMethods.getLanguage())
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    obsIsLoading.set(false)
                    obsIsVisible.set(false)

                    productItem = res.data!!

                    notifyChange()

                    apiResponseLiveData.value = ApiResponse.success(res)
                }

                else -> {
                    obsIsLoading.set(false)
                }
            }
        }
    }

    fun getSearchProductAttr(productId: Int) {
        requestCall<SearchProductAttResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getSearch_product_attr(
                    productId,
                    1,
                )
            }
        })
        { res ->
            when {
                res!!.attributes.isNotEmpty() -> {
                    skuParentAdapter.updateList(res.attributes)
                }

                else -> {
                    obsIsLoading.set(false)
                }
            }
        }
    }

    fun getSearchProductAttrSelected(productId: Int, skuId: String, attr: String, value: String) {
        obsIsLoading.set(true)
        requestCall<SearchProductAttResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getSearch_product_attrSelected(
                    productId,
                    1, skuId, attr, value
                )
            }
        })
        { res ->
            when {
                res?.attributes?.isNotEmpty() == true -> {
                    obsIsLoading.set(false)
                    skuParentAdapter.updateList(res.attributes)
                    searchProductAttr = res.sku
                    getProductPrice2()
                }

                else -> {
                    obsIsLoading.set(false)
                }
            }
        }
    }

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _addAndRemoveFavoriteResponse: MutableLiveData<ResponseHandler<FavoriteResponse?>> =
        MutableLiveData()
    val addAndRemoveFavoriteResponse: LiveData<ResponseHandler<FavoriteResponse?>> =
        _addAndRemoveFavoriteResponse.toSingleEvent()

    fun addAndRemoveFavorite() {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.addAndRemoveFavorite(
                    PrefMethods.getUserData()!!.id!!,
                    "product",
                    productItem.id.toString(),
                    PrefMethods.getLanguage(),
                )
            }.collect {
                _addAndRemoveFavoriteResponse.value = it
            }
        }
    }
}