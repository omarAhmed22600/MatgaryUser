package com.brandsin.user.ui.main.order.storedetails.addons.addons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderAddonsBinding
import com.brandsin.user.model.order.storedetails.StoreAddonsItem
import com.brandsin.user.utils.SingleLiveEvent
import kotlin.collections.ArrayList

class OrderAddonsAdapter : RecyclerView.Adapter<OrderAddonsAdapter.OrderAddonsHolder>()
{
    var orderAddonsList: ArrayList<StoreAddonsItem> = ArrayList()
    var orderAddonsLiveData = SingleLiveEvent<StoreAddonsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAddonsHolder
    {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawOrderAddonsBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_order_addons, parent, false)
        return OrderAddonsHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderAddonsHolder, position: Int)
    {
        val itemViewModel = ItemOrderAddonsViewModel(orderAddonsList[position])
        holder.binding.viewModel = itemViewModel

        holder.setSelected()
        holder.binding.rawLayout.setOnClickListener {
            when {
                itemViewModel.item.isSelected -> {
                    holder.setSelected()
                    notifyItemChanged(position)
                    itemViewModel.item.isSelected = false
                }
                else -> {
                    holder.setSelected()
                    notifyItemChanged(position)
                    itemViewModel.item.isSelected = true
                }
            }
            orderAddonsLiveData.value = itemViewModel.item
        }
    }

    fun getItem(pos:Int): StoreAddonsItem {
        return orderAddonsList[pos]
    }

    override fun getItemCount(): Int {
        return orderAddonsList.size
    }

    fun updateList(models: ArrayList<StoreAddonsItem>) {
        orderAddonsList = models
        notifyDataSetChanged()
    }

    inner class OrderAddonsHolder(val binding: RawOrderAddonsBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun setSelected()
        {
            when {
                orderAddonsList[adapterPosition].isSelected -> {
                    binding.ivSelected.setImageResource(R.drawable.ic_size_check_box_24px)
                }
                else -> {
                    binding.ivSelected.setImageResource(R.drawable.ic_check_box_outline_blank_24px)
                }
            }
        }
    }
}
