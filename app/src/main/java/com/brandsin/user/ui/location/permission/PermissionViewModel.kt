package com.brandsin.user.ui.location.permission

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes

class PermissionViewModel : BaseViewModel()
{
    fun onManuallyClicked()
    {
        setValue(Codes.MANUALL_LOCATION_CLICKED)
    }

    fun onAutoClicked()
    {
        setValue(Codes.CURRENT_LOCATION_CLICKED)
    }
}