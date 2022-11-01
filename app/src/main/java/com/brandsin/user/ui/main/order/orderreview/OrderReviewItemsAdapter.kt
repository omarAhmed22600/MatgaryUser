package com.brandsin.user.ui.main.order.orderreview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderReviewItemBinding
import com.brandsin.user.model.order.cart.CartItem
import kotlin.collections.ArrayList

class OrderReviewItemsAdapter : RecyclerView.Adapter<OrderReviewItemsAdapter.CartsHolder>()
{
    var itemsList: ArrayList<CartItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartsHolder
    {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawOrderReviewItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_order_review_item, parent, false)
        return CartsHolder(binding)
    }

    override fun onBindViewHolder(holder: CartsHolder, position: Int)
    {
        val itemViewModel = ItemOrderReviewViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel

        if (itemsList.size-1 == position)
        {
            holder.binding.seperator.visibility = View.GONE
        }
    }

    fun getItem(pos:Int):CartItem{
        return itemsList[pos]
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(models: ArrayList<CartItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class CartsHolder(val binding: RawOrderReviewItemBinding) : RecyclerView.ViewHolder(binding.root)
}
