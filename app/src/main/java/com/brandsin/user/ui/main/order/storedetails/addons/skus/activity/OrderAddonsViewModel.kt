package com.brandsin.user.ui.main.order.storedetails.addons.skus.activity

import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.SearchProdactAttr.SearchProductAttResponse
import com.brandsin.user.model.order.SearchProdactAttr.Sku
import com.brandsin.user.model.order.productdetails.ProductDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreAddonsItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.model.order.storedetails.StoreSkusItem
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.order.storedetails.addons.addons.OrderAddonsAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.banners.BannersAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.adapter.OrderSkusAdapter
import com.brandsin.user.utils.PrefMethods

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderAddonsViewModel : BaseViewModel() {

    var productItem = StoreProductItem()

   // var skuAdapter = OrderSkusAdapter()
    var addonsAdapter = OrderAddonsAdapter()
    var search_product_attr=Sku()

    var skuPrice: Double = 0.0  // selected sku price
    var addonsPrice: Double = 0.0  // addons price
    var itemPrice: Double = 0.0 // sku + addons
    val obsItemPrice = ObservableDouble(0.0) // sku * quantoty
    val obsTotalPrice = ObservableDouble(0.0) // item price * count
    val obsCount = ObservableField(1)
    var selectedSkuCode : String = ""
    var selectedSkuName : String = ""
    var SkuParentAdapter=OrderSkusAdapter()


    val obsNotes = ObservableField<String>()

    var bannersAdapter = BannersAdapter()

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
    private fun getProductPrice2() = when (search_product_attr.sale_price) {
        null -> {
            skuPrice = search_product_attr.regular_price!!.toDouble()
            itemPrice = (skuPrice + addonsPrice)
            obsItemPrice.set(skuPrice * obsCount.get()!!)
            obsTotalPrice.set(itemPrice * obsCount.get()!!)
        }
        else -> {
            skuPrice = search_product_attr.regular_price!!.toDouble()
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

        requestCall<ProductDetailsResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo()
                    .getProductDetails(productId, PrefMethods.getLanguage())
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    obsIsVisible.set(false)

                    productItem = res.data!!

                    notifyChange()
                    apiResponseLiveData.value = ApiResponse.success(res)
                }
                else->{}
            }
        }
    }


    fun getSearchProductAtrr(ProductId :Int){
        requestCall<SearchProductAttResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getSearch_product_attr(ProductId,
                    1,
                )
            }
        })
        { res ->
            when  {
                res!!.attributes.isNotEmpty() -> {
                    SkuParentAdapter.updateList(res.attributes)
                }else->{

            }
            }
        }
    }


    fun getSearchProductAtrrSelected(ProductId :Int,skuId:String,attr:String,value:String){
        requestCall<SearchProductAttResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getSearch_product_attrSelected(ProductId,
                    1,skuId,attr,value
                )
            }
        })
        { res ->
            when  {
                res!!.attributes.isNotEmpty() -> {
                    SkuParentAdapter.updateList(res.attributes)
                    search_product_attr=res.sku
                    getProductPrice2()
                }else->{

            }
            }
        }
    }

}