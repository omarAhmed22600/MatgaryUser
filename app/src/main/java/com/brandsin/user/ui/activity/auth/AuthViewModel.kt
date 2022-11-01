package com.brandsin.user.ui.activity.auth

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.brandsin.user.database.BaseViewModel

class AuthViewModel : BaseViewModel()
{
    val obsShowToolbar = ObservableBoolean()
    val obsTitle = ObservableField<String>()

}