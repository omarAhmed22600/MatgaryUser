package com.brandsin.user.ui.MFPayment

import com.myfatoorah.sdk.entity.initiatepayment.PaymentMethod

interface OnListFragmentInteractionListener {
    fun onListFragmentInteraction(position: Int, item: PaymentMethod)
}