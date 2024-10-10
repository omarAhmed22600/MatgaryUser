package com.brandsin.user.ui.menu.refundableProducts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.databinding.ItemReasonReturnBinding
import com.brandsin.user.model.refundableProduct.ReasonReturnItem
import com.brandsin.user.model.refundableProduct.RefundableProductItem

class ReasonReturnAdapter(
    private val onItemClickCallBack: (reasonReturnItem: ReasonReturnItem) -> Unit
) : RecyclerView.Adapter<ReasonReturnAdapter.ReasonReturnViewHolder>() {

    private var reasonReturnItemList: List<ReasonReturnItem?>? = ArrayList()

    private lateinit var binding: ItemReasonReturnBinding

    inner class ReasonReturnViewHolder(itemView: ItemReasonReturnBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val reasonName = itemView.reasonName
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReasonReturnAdapter.ReasonReturnViewHolder {
        binding =
            ItemReasonReturnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReasonReturnViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReasonReturnViewHolder, position: Int) {
        val reasonReturnItem = reasonReturnItemList!![position]!!

        // set data
        holder.reasonName.text = reasonReturnItem.name

        holder.reasonName.setOnClickListener {
            onItemClickCallBack.invoke(reasonReturnItem)
        }
    }

    override fun getItemCount() = reasonReturnItemList?.size ?: 0

    fun submitData(reasonReturnItem: List<ReasonReturnItem?>?) {
        reasonReturnItemList = reasonReturnItem
        notifyDataSetChanged()
    }
}