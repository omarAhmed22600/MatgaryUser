package com.brandsin.user.ui.main.order.rateorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawRateOrderBinding
import com.brandsin.user.model.order.details.OrderItem
import java.util.*

class RateOrderAdapter : RecyclerView.Adapter<RateOrderAdapter.RateOrderHolder>()
{
    var itemsList: ArrayList<OrderItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateOrderAdapter.RateOrderHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawRateOrderBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_rate_order, parent, false)
        return RateOrderHolder(binding)
    }

    override fun onBindViewHolder(holder: RateOrderAdapter.RateOrderHolder, position: Int) {
        val itemViewModel = ItemRateOrderViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(models: ArrayList<OrderItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class RateOrderHolder(val binding: RawRateOrderBinding) : RecyclerView.ViewHolder(binding.root)
}
