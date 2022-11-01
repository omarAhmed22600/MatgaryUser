package com.brandsin.user.ui.main.order.orderdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderDetailsAddonsBinding
import com.brandsin.user.model.order.details.AddonsItem
import kotlin.collections.ArrayList

class OrderDetailsAddonsAdapter : RecyclerView.Adapter<OrderDetailsAddonsAdapter.CartsHolder>()
{
    var itemsList: List<AddonsItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartsHolder
    {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawOrderDetailsAddonsBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_order_details_addons, parent, false)
        return CartsHolder(binding)
    }

    override fun onBindViewHolder(holder: CartsHolder, position: Int)
    {
        val itemViewModel = ItemOrderAddonsContentViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel

    }

    fun getItem(pos:Int):AddonsItem{
        return itemsList[pos]
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(models: List<AddonsItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class CartsHolder(val binding: RawOrderDetailsAddonsBinding) : RecyclerView.ViewHolder(binding.root)
}
