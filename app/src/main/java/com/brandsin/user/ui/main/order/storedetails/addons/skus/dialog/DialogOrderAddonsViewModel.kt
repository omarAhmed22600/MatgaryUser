package com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog

import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.banners.BannersAdapter

class DialogOrderAddonsViewModel : BaseViewModel()
{
    var productItem = StoreProductItem()
    var unitPrice : Double = 0.0
    var totalPrice : Double = 0.0
    var count : Int = 1
    var skuCode : String = ""
    val obsUnitPrice = ObservableField<Double>()
    val obsNotes = ObservableField<String>()
    val obsTotalPrice = ObservableField(0.0)
    val obsCount = ObservableField(1)

    var bannersAdapter = BannersAdapter()

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

    fun getProductPrice() = when (productItem.skus!![0]!!.salePrice) {
        null -> {
            obsUnitPrice.set(productItem.skus!![0]!!.regularPrice!!.toDouble())
            unitPrice = productItem.skus!![0]!!.regularPrice!!.toDouble()
            totalPrice = unitPrice
            obsTotalPrice.set(productItem.skus!![0]!!.regularPrice!!.toDouble())
        }
        else -> {
            obsUnitPrice.set(productItem.skus!![0]!!.salePrice!!.toDouble())
            unitPrice = productItem.skus!![0]!!.salePrice!!.toDouble()
            totalPrice = unitPrice
            obsTotalPrice.set(unitPrice)
        }
    }

    fun getSkuCode()
    {
        when {
            productItem.skus!!.isNotEmpty() -> {
                skuCode = productItem.skus!![0]!!.code!!
            }
        }
    }

    fun onAddToCartClicked() {
        setValue(Codes.ADD_TO_CART)
    }
}