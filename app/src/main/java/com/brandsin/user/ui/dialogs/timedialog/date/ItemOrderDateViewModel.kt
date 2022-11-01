package com.brandsin.user.ui.dialogs.timedialog.date

import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.storedetails.StoreTimeItem

class ItemOrderDateViewModel(var item: StoreTimeItem) : BaseViewModel()
{
    var obsDay = ObservableField("")
    var obsDate = ObservableField("")

    init {
       item.storeDay.let {
           obsDay.set(it!!.take(3))
        }

        item.storeDate.let {
            val dateArr: Array<String> = item.storeDate.toString().split("-".toRegex()).toTypedArray()
            obsDate.set(dateArr[2])
        }
    }
}