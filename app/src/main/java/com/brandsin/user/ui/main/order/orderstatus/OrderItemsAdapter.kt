package com.brandsin.user.ui.main.order.orderstatus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderItemBinding
import com.brandsin.user.model.order.details.OrderItem
import kotlin.collections.ArrayList

class OrderItemsAdapter : RecyclerView.Adapter<OrderItemsAdapter.CartsHolder>()
{
    var cartsList: ArrayList<OrderItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartsHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawOrderItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_order_item, parent, false)
        return CartsHolder(binding)
    }

    override fun onBindViewHolder(holder: CartsHolder, position: Int) {
        val itemViewModel = OrderItemViewModel(cartsList[position])
        holder.binding.viewModel = itemViewModel
    }

    fun getItem(pos:Int):OrderItem {
        return cartsList[pos]
    }

    override fun getItemCount(): Int {
        return cartsList.size
    }

    fun updateList(models: ArrayList<OrderItem>)
    {
        cartsList = models
        notifyDataSetChanged()
    }

    inner class CartsHolder(val binding: RawOrderItemBinding) : RecyclerView.ViewHolder(binding.root)
}
