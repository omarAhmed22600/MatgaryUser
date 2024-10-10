package com.brandsin.user.ui.main.order.orderdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderContentItemBinding
import com.brandsin.user.model.order.details.OrderItem

class OrderContentAdapter : RecyclerView.Adapter<OrderContentAdapter.CartsHolder>() {
    var itemsList: ArrayList<OrderItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartsHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawOrderContentItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_order_content_item, parent, false)
        return CartsHolder(binding)
    }

    override fun onBindViewHolder(holder: CartsHolder, position: Int) {
        val orderDetailsAddonsAdapter = OrderDetailsAddonsAdapter()
        orderDetailsAddonsAdapter.updateList(itemsList[position].addons)
        val itemViewModel =
            ItemOrderContentViewModel(itemsList[position], orderDetailsAddonsAdapter)
        holder.binding.viewModel = itemViewModel

        if (itemsList.size - 1 == position) {
            holder.binding.seperator.visibility = View.GONE
        }
    }

    fun getItem(pos: Int): OrderItem {
        return itemsList[pos]
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(models: ArrayList<OrderItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class CartsHolder(val binding: RawOrderContentItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
