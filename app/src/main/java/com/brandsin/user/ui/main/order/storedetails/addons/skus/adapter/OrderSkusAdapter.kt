package com.brandsin.user.ui.main.order.storedetails.addons.skus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderSkusBinding
import com.brandsin.user.model.order.SearchProdactAttr.StoreItemColors
import com.brandsin.user.model.order.storedetails.StoreSkusItem
import com.brandsin.user.utils.SingleLiveEvent

class OrderSkusAdapter : RecyclerView.Adapter<OrderSkusAdapter.OrderSizeHolder>() {

    private var orderSkusList: ArrayList<StoreSkusItem> = ArrayList()

    var orderSkusLiveData = SingleLiveEvent<StoreItemColors>()

    var selectedPosition = 0

    var selectedSkuId = 0
    var selectedAttributeId = 0
    var selectedNumberValue = 0
    var selectedSkuName = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSizeHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawOrderSkusBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_order_skus, parent, false)
        return OrderSizeHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderSizeHolder, position: Int) {
        val itemViewModel = ItemOrderSkusColorViewModel(orderSkusList[position])
        holder.binding.viewModel = itemViewModel

        //holder.setSelected()
//        holder.binding.rawLayout.setOnClickListener {
//            when {
//                selectedPosition != position -> {
//                    orderSkusLiveData.value = itemViewModel.item
//                    notifyItemChanged(position)
//                    notifyItemChanged(selectedPosition)
//                    selectedPosition = position
//                }
//            }
//        }

        itemViewModel.skuChildAdapter.orderSkusColorLiveData.observeForever {
            orderSkusLiveData.value = it
        }

        selectedSkuId = itemViewModel.skuChildAdapter.selectedSkuId
        selectedAttributeId = itemViewModel.skuChildAdapter.selectedAttributeId
        selectedNumberValue = itemViewModel.skuChildAdapter.selectedNumberValue
        selectedSkuName = itemViewModel.skuChildAdapter.selectedSkuName
    }

    fun getItem(pos: Int): StoreSkusItem {
        return orderSkusList[pos]
    }

    override fun getItemCount(): Int {
        return orderSkusList.size
    }

    fun updateList(models: ArrayList<StoreSkusItem>) {
        //models[0].isSelected = true
        orderSkusList = models
        notifyDataSetChanged()
    }

    inner class OrderSizeHolder(val binding: RawOrderSkusBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        fun setSelected() {
//            when (selectedPosition) {
//                adapterPosition -> {
//                    binding.ivSelected.setImageResource(R.drawable.ic_size_radio_button_checked_24px)
//                }
//                else -> {
//                    binding.ivSelected.setImageResource(R.drawable.ic_radio_button_unchecked)
//                }
//            }
//        }
    }

}
