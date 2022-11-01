package com.brandsin.user.ui.main.order.confirmorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderPaymentWayBinding
import com.brandsin.user.utils.SingleLiveEvent
import java.util.*

class PaymentWaysAdapter : RecyclerView.Adapter<PaymentWaysAdapter.PaymentWaysHolder>()
{
    var paymentWaysList: MutableList<PaymentWayItem> = ArrayList()
    var paymentLiveData = SingleLiveEvent<PaymentWayItem>()
    var selectedPosition = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentWaysHolder
    {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawOrderPaymentWayBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_order_payment_way, parent, false)
        return PaymentWaysHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentWaysHolder, position: Int)
    {
        val itemViewModel = ItemPaymentWayViewModel(paymentWaysList[position])
        holder.binding.viewModel = itemViewModel

        holder.setInfo()
        holder.setSelected()
        holder.binding.rawPayment.setOnClickListener {
            paymentLiveData.value = itemViewModel.item
            notifyItemChanged(position)
            if (selectedPosition != -1)
            {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = position
        }
    }

    fun getItem(pos:Int):PaymentWayItem{
        return paymentWaysList[pos]
    }

    override fun getItemCount(): Int {
        return paymentWaysList.size
    }

    fun updateList(models: List<PaymentWayItem>) {
        paymentWaysList = models.toMutableList()
        notifyDataSetChanged()
    }

    inner class PaymentWaysHolder(val binding: RawOrderPaymentWayBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun setSelected()
        {
            when (selectedPosition) {
                adapterPosition -> {
                    binding.ivSelected.setImageResource(R.drawable.ic_radio_button_checked)
                }
                else -> {
                    binding.ivSelected.setImageResource(R.drawable.ic_radio_button_unchecked)
                }
            }
        }

        fun setInfo()
        {
            when (getItem(adapterPosition).id) {
                3, 4 -> {
                    binding.ivInfo.visibility = View.VISIBLE
                }
                else -> {
                    binding.ivInfo.visibility = View.GONE
                }
            }
        }
    }
}
