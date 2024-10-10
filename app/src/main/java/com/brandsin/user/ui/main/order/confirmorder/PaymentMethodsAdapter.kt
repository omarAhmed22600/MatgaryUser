package com.brandsin.user.ui.main.order.confirmorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOrderPaymentWayBinding

class PaymentMethodsAdapter(
    private val onPaymentMethodClickCallBack: (item: PaymentMethodItem) -> Unit = {}
) : RecyclerView.Adapter<PaymentMethodsAdapter.PaymentMethodsHolder>() {

    var selectedPosition = 0

    private var paymentMethodList: List<PaymentMethodItem> = ArrayList()

    private lateinit var binding: RawOrderPaymentWayBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodsHolder {
        binding =
            RawOrderPaymentWayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentMethodsHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentMethodsHolder, position: Int) {
        val paymentMethod = paymentMethodList[position]

        binding.iconPaymentMethod.setImageResource(paymentMethod.icon)
        binding.paymentMethodName.text = paymentMethod.name

        holder.setSelected()

        if (paymentMethod.id == 4) {
            binding.value.visibility = View.VISIBLE
            binding.value.text = paymentMethod.value.toString()
        } else {
            binding.value.visibility = View.GONE
        }

        binding.root.setOnClickListener {
            notifyDataSetChanged()
            selectedPosition = position
            onPaymentMethodClickCallBack.invoke(paymentMethod)
        }
    }

    fun getItem(pos: Int): PaymentMethodItem {
        return paymentMethodList[pos]
    }

    override fun getItemCount(): Int {
        return paymentMethodList.size
    }

    fun updateList(list: ArrayList<PaymentMethodItem>) {
        paymentMethodList = list
        notifyDataSetChanged()
    }

    inner class PaymentMethodsHolder(val binding: RawOrderPaymentWayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setSelected() {
            when (selectedPosition) {
                bindingAdapterPosition -> {
                    // binding.ivSelected.setImageResource(R.drawable.ic_radio_button_checked)
                    binding.root.setBackgroundResource(R.drawable.back_btn_bg)
                    binding.root.strokeColor = R.color.color_primary
                    // binding.rawPaymentWay.setBackgroundResource(R.drawable.back_btn_bg)
                }

                else -> {
                    // binding.ivSelected.setImageResource(R.drawable.ic_radio_button_unchecked)
                    binding.root.setBackgroundResource(R.drawable.item_payment_way_bg)
                    binding.root.strokeColor = R.color.raw_home_about_bg_color
                    // binding.rawPaymentWay.setBackgroundResource(R.drawable.item_payment_way_bg)
                }
            }
        }
    }
}
