package com.brandsin.user.ui.main.order.storedetails.products

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var mCurrentPosition = 0

    fun BaseViewHolder(itemView: View) {


    }

    protected abstract fun clear()
    open fun onBind(position: Int) {
        mCurrentPosition = position
        clear()
    }



    open fun getCurrentPosition(): Int {
        return mCurrentPosition
    }
}