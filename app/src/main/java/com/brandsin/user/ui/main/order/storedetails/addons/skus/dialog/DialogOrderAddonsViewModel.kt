package com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.FavoriteResponse
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartStoreData
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.productdetails.ProductDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.requestCall
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.OrderAddonsActivity
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.banners.BannersAddonsAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.banners.BannersAdapter
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogOrderAddonsViewModel : BaseViewModel() {

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _addAndRemoveFavoriteResponse: MutableLiveData<ResponseHandler<FavoriteResponse?>> =
        MutableLiveData()
    val addAndRemoveFavoriteResponse: LiveData<ResponseHandler<FavoriteResponse?>> =
        _addAndRemoveFavoriteResponse.toSingleEvent()

    var productItem = StoreProductItem()
    var unitPrice: Double = 0.0
    var totalPrice: Double = 0.0
    var count: Int = 1
    var skuCode: String = ""
    val obsUnitPrice = ObservableField<Double>()
    val obsNotes = ObservableField<String>()
    val obsTotalPrice = ObservableField(0.0)
    val obsCount = ObservableField(1)

    lateinit var bannersAdapter : BannersAdapter

    fun setBannerListener(onBannerClickedListener: DialogOrderAddonsFragment) {
        bannersAdapter = BannersAdapter(onBannerClickedListener)
    }

    fun onPlusClicked() {
        count += 1
        obsCount.set(count)
        totalPrice = (unitPrice * count)
        obsTotalPrice.set(totalPrice)
    }

    fun onMinusClicked() {
        when {
            obsCount.get()!!.toInt() != 1 -> {
                count -= 1
                obsCount.set(count)
                totalPrice = (unitPrice * count)
                obsTotalPrice.set(totalPrice)
            }
        }
    }

    private fun getProductPrice() = when (productItem.skus!![0]!!.salePrice) {
        null -> {
            obsUnitPrice.set(productItem.skus!![0]?.regularPrice?.replace(",", "")?.toDouble())
            unitPrice = productItem.skus!![0]!!.regularPrice!!.replace(",", "").toDouble()
            totalPrice = unitPrice
            obsTotalPrice.set(unitPrice)
        }

        else -> {
            obsUnitPrice.set(productItem.skus!![0]!!.salePrice!!.replace(",", "").toDouble())
            unitPrice = productItem.skus!![0]!!.salePrice!!.replace(",", "").toDouble()
            totalPrice = unitPrice     /// 2860.00
            obsTotalPrice.set(unitPrice)
        }
    }

    private fun getSkuCode() {
        when {
            productItem.skus!!.isNotEmpty() -> {
                skuCode = productItem.skus!![0]!!.code!!
            }
        }
    }

    fun onAddToCartClicked() {
        setValue(Codes.ADD_TO_CART)
    }

    fun getProductDetails(productId: Int) {
        obsIsLoading.set(true)
        requestCall<ProductDetailsResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo()
                    .getProductDetails(
                        productId,
                        PrefMethods.getUserData()?.id?:0,
                        PrefMethods.getLanguage()
                    )
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    obsIsLoading.set(false)
                    obsIsVisible.set(false)

                    productItem = res.data!!
                    getProductPrice()
                    getSkuCode()
                    notifyChange()
                    apiResponseLiveData.value = ApiResponse.success(res)
                }

                else -> {}
            }
        }
    }

    fun addAndRemoveFavorite() {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.addAndRemoveFavorite(
                    PrefMethods.getUserData()!!.id!!,
                    "product",
                    productItem.id.toString(),
                    PrefMethods.getLanguage()
                )
            }.collect {
                _addAndRemoveFavoriteResponse.value = it
            }
        }
    }
}