package com.brandsin.user.ui.main.order.storedetails.addons.skus.activity

import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.storedetails.StoreAddonsItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.model.order.storedetails.StoreSkusItem
import com.brandsin.user.ui.main.order.storedetails.addons.addons.OrderAddonsAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.banners.BannersAdapter
import com.brandsin.user.ui.main.order.storedetails.addons.skus.adapter.OrderSkusAdapter

class OrderAddonsViewModel : BaseViewModel() {

    var productItem = StoreProductItem()

    var skuAdapter = OrderSkusAdapter()
    var addonsAdapter = OrderAddonsAdapter()

    var skuPrice: Double = 0.0  // selected sku price
    var addonsPrice: Double = 0.0  // addons price
    var itemPrice: Double = 0.0 // sku + addons
    val obsItemPrice = ObservableDouble(0.0) // sku * quantoty
    val obsTotalPrice = ObservableDouble(0.0) // item price * count
    val obsCount = ObservableField(1)
    var selectedSkuCode : String = ""

    val obsNotes = ObservableField<String>()

    var bannersAdapter = BannersAdapter()

    fun setProductData(item: StoreProductItem) {
        getProductPrice()

        skuAdapter.updateList(item.skus as ArrayList<StoreSkusItem>)
        addonsAdapter.updateList(item.addons as ArrayList<StoreAddonsItem>)

        selectedSkuCode = item.skus[0].code!!
        skuPrice = item.skus[0].price!!.toDouble()
        itemPrice = skuPrice + addonsPrice
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
            itemPrice = skuPrice + addonsPrice
            obsItemPrice.set(skuPrice * obsCount.get()!!)
            obsTotalPrice.set(itemPrice * obsCount.get()!!)
        }
        else -> {
            skuPrice = productItem.skus!![0]!!.salePrice!!.toDouble()
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
}