package com.brandsin.user.ui.dialogs.timedialog.date

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderDateBinding
import com.brandsin.user.model.order.storedetails.StoreTimeItem
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.SingleLiveEvent
import kotlin.collections.ArrayList

class OrderDatesAdapter : RecyclerView.Adapter<OrderDatesAdapter.OrderDateHolder>()
{
    var datesList: List<StoreTimeItem> = ArrayList()
    var dateLiveData = SingleLiveEvent<StoreTimeItem>()
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDateHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawOrderDateBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_order_date, parent, false)
        return OrderDateHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDateHolder, position: Int) {
        val itemViewModel = ItemOrderDateViewModel(datesList[position])
        holder.binding.viewModel = itemViewModel

        holder.setSelected()

        when (selectedPosition) {
            position -> {
                itemViewModel.item.isSelected = true
                dateLiveData.value = itemViewModel.item
            }
        }

        holder.binding.rawItem.setOnClickListener {
            when {
                selectedPosition != position -> {
                    notifyItemChanged(position)
                    when {
                        selectedPosition != -1 -> {
                            notifyItemChanged(selectedPosition)
                        }
                    }
                    selectedPosition = position
                    dateLiveData.value = itemViewModel.item
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return datesList.size
    }

    fun updateList(models: ArrayList<StoreTimeItem>) {
        datesList = models
        notifyDataSetChanged()
    }

    inner class OrderDateHolder(val binding: RawOrderDateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setSelected() {
            when (selectedPosition) {
                adapterPosition -> {
                    binding.tvDate.setTextColor(ContextCompat.getColor(MyApp.getInstance(), R.color.white))
                    binding.tvDate.background = (ContextCompat.getDrawable(MyApp.getInstance(), R.drawable.date_bg))
                    binding.rawItem.background = (ContextCompat.getDrawable(MyApp.getInstance(), R.drawable.day_bg))
                }
                else -> {
                    binding.tvDate.setTextColor(ContextCompat.getColor(MyApp.getInstance(), R.color.hint_color))
                    binding.tvDate.background = null
                    binding.rawItem.background = null
                }
            }
        }
    }
}
