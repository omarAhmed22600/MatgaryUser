package com.brandsin.user.ui.main.order.storedetails.addons.skus.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderSkuColorBinding
import com.brandsin.user.model.order.SearchProdactAttr.StoreItemColors
import com.brandsin.user.utils.SingleLiveEvent
import java.util.regex.Matcher
import java.util.regex.Pattern

class OrderSkusColorAdapter : RecyclerView.Adapter<OrderSkusColorAdapter.OrderSizeHolder>() {

    var orderSkusList: List<StoreItemColors>? = ArrayList()
    //var orderSkusLiveData = MutableLiveData<StoreSkusItem>()

    var orderSkusColorLiveData = SingleLiveEvent<StoreItemColors>()

    var selectedPosition = 0
    var selectedSkuId = 0
    var selectedAttributeId = 0
    var selectedNumberValue = 0
    var selectedSkuName = ""
    var isColor: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSizeHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RawOrderSkuColorBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_order_sku_color, parent, false)
        return OrderSizeHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderSizeHolder, position: Int) {
        val itemViewModel = ItemOrderSkusViewModelV2(orderSkusList!![position])
        holder.binding.viewModel2 = itemViewModel

        //holder.setSelected()
        /*holder.binding.root.setOnClickListener {
            when {
                selectedPosition != position -> {
                    orderSkusColorLiveData.value = itemViewModel.item
                    notifyItemChanged(position)
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                }
            }
        }*/

        val colorPattern: Pattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})")
        val m: Matcher = colorPattern.matcher(itemViewModel.item.value ?: "")

        isColor = m.matches()
        val regex = "^#(?:[0-9a-fA-F]{3}){1,2}"

        if (isColor) {
            holder.binding.colorLayout.visibility = ViewGroup.VISIBLE
            holder.binding.colorImg.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(itemViewModel.item.value))
            holder.binding.sizeLayout.visibility = ViewGroup.GONE
        } else {
            holder.binding.colorLayout.visibility = ViewGroup.GONE
            holder.binding.sizeLayout.visibility = ViewGroup.VISIBLE
        }

        holder.binding.sizeLayout.setOnClickListener {
            orderSkusColorLiveData.value = itemViewModel.item
        }

        holder.setSelected(holder.itemView.context)

        holder.binding.colorLayout.setOnClickListener {
            orderSkusColorLiveData.value = itemViewModel.item
        }
    }

    fun getItem(pos: Int): StoreItemColors {
        return orderSkusList!![pos]
    }

    override fun getItemCount(): Int {
        return orderSkusList?.size ?: 0
    }

    fun updateList(models: List<StoreItemColors>?) {
        // models[0].is_selected = 1
        orderSkusList = models
        notifyDataSetChanged()
    }

    inner class OrderSizeHolder(val binding: RawOrderSkuColorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setSelected(context: Context) {
            if (isColor) {
                if (orderSkusList!![adapterPosition].is_selected == 1) {
                    binding.checkedImg.visibility = ViewGroup.VISIBLE
                } else {
                    binding.checkedImg.visibility = ViewGroup.GONE
                }
            } else {
                if (orderSkusList!![adapterPosition].is_selected == 1) {
                    binding.sizeLayout.strokeColor = context.getColor(R.color.color_primary)
                    binding.tvSize.setTextColor(context.getColor(R.color.color_primary))
                    selectedSkuId = orderSkusList!![bindingAdapterPosition].skuId ?: 0
                    selectedAttributeId = orderSkusList!![bindingAdapterPosition].attributeId ?: 0
                    selectedNumberValue = orderSkusList!![bindingAdapterPosition].numberValue ?: 0
                    selectedSkuName = orderSkusList!![bindingAdapterPosition].value ?: ""

                } else {
                    binding.sizeLayout.strokeColor = context.getColor(R.color.color_accent)
                    binding.tvSize.setTextColor(context.getColor(R.color.grey))
                }
            }
        }
    }
}