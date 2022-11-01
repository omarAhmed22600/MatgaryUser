package com.brandsin.user.ui.menu.rateapp

import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes

class RateAppViewModel : BaseViewModel()
{
    val obsRate = ObservableField<Float>()
    val obsMsg = ObservableField<String>()

    init {
        obsRate.set(5f)
    }

    fun onRateClicked()
    {
        setValue(Codes.RATING_SUCCESS)
    }

}