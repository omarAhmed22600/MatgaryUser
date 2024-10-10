package com.brandsin.user.ui.main.order.orderdetails.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemGiftCodeBinding
import com.brandsin.user.model.order.details.OfferItem

class GiftCodeAdapter(
    private val onClickIconCopyCallBack: (position: Int, item: OfferItem) -> Unit,
) : RecyclerView.Adapter<GiftCodeAdapter.GiftCodeViewHolder>() {

    private var itemList: List<OfferItem?>? = ArrayList()

    private lateinit var binding: ItemGiftCodeBinding

    inner class GiftCodeViewHolder(itemView: ItemGiftCodeBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val giftCode = itemView.giftCode
        val giftPrice = itemView.giftPrice
        val icCopyCode = itemView.icCopyCode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftCodeViewHolder {
        binding =
            ItemGiftCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GiftCodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GiftCodeViewHolder, position: Int) {
        val item = itemList!![position]

        // bind view
        bindData(holder, position, item!!)
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(
        holder: GiftCodeViewHolder,
        position: Int,
        item: OfferItem
    ) {

        holder.giftCode.text = item.giftCode ?: ""
        holder.giftPrice.text = item.amount.toString() + " " +
                holder.itemView.context.getString(R.string.currency)

        holder.icCopyCode.setOnClickListener {
            onClickIconCopyCallBack.invoke(position, item)
        }
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    fun submitData(offersList: List<OfferItem?>?) {
        itemList = offersList
        notifyDataSetChanged()
    }
}