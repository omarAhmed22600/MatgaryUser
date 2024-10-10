package com.brandsin.user.ui.dialogs.timedialog

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.order.storedetails.StoreTimeItem
import com.brandsin.user.ui.dialogs.timedialog.date.OrderDatesAdapter

class DateViewModel : BaseViewModel() {

    var datesAdapter = OrderDatesAdapter()
    lateinit var selectedDate: String
    lateinit var selectedTime: String

    fun setDates(itemsList: ArrayList<StoreTimeItem>) {
        datesAdapter.updateList(itemsList)
        datesAdapter.notifyDataSetChanged()

        selectedTime = itemsList[0].storeDate.toString()
        obsSize.set(10)
    }

    fun onConfirmClicked() {
        setValue(Codes.CONFIRM_CLICKED)
    }

    fun onCancelClicked() {
        setValue(Codes.CANCEL_CLICKED)
    }
}