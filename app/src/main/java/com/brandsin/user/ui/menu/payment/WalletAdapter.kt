package com.brandsin.user.ui.menu.payment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemWalletBinding
import com.brandsin.user.utils.storyviewer.utils.hide
import com.brandsin.user.utils.storyviewer.utils.show

class WalletAdapter : RecyclerView.Adapter<WalletAdapter.ChooseAddressViewHolder>() {

    private var addressesList: List<TransactionsItem?>? = ArrayList()

    private lateinit var binding: ItemWalletBinding

    inner class ChooseAddressViewHolder(itemView: ItemWalletBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val icAddOrMinus = itemView.icAddOrMinus
        val balance = itemView.balance
        val note = itemView.note
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseAddressViewHolder {
        binding = ItemWalletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseAddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseAddressViewHolder, position: Int) {
        val transactionsItem = addressesList!![position]
        // bind view
        bindData(holder, transactionsItem!!)
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(holder: ChooseAddressViewHolder, transactionsItem: TransactionsItem) {

        if (transactionsItem.increase == null || transactionsItem.increase == 0.0) {
            holder.icAddOrMinus.setImageResource(R.drawable.ic_red_minus)
            holder.balance.text =
                transactionsItem.decrease.toString() + " " + holder.itemView.context.getString(R.string.currency)
        } else if (transactionsItem.decrease == null || transactionsItem.decrease == 0.0) {
            holder.icAddOrMinus.setImageResource(R.drawable.ic_green_plus)
            holder.balance.text =
                transactionsItem.increase.toString() + " " + holder.itemView.context.getString(R.string.currency)
        }

        if (transactionsItem.note?.isNotEmpty() == true || transactionsItem.note != null) {
            holder.note.show()
            holder.note.text = transactionsItem.note.toString()
        } else {
            holder.note.hide()
        }
    }

    override fun getItemCount(): Int {
        return addressesList?.size ?: 0
    }

    fun submitData(addresses: List<TransactionsItem?>?) {
        addressesList = addresses
        notifyDataSetChanged()
    }
}