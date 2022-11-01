package com.brandsin.user.ui.main.homenew.moresub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemHomeMoreSubBinding
import com.brandsin.user.model.order.homenew.StoresDataItem
import com.brandsin.user.utils.SingleLiveEvent

class MoreSubAdapter : RecyclerView.Adapter<MoreSubAdapter.MoreSubHolder>() {

    var moreSubList  = mutableListOf<StoresDataItem>()
    var moresubLiveData = SingleLiveEvent<StoresDataItem>()
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreSubHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: ItemHomeMoreSubBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_home_more_sub, parent, false)
        return MoreSubHolder(binding)
    }

    override fun onBindViewHolder(holder: MoreSubHolder, position: Int) {
        val itemViewModel = ItemMoreSubViewModel(moreSubList[position])
        holder.binding.viewModel = itemViewModel

        if (itemViewModel.itemMoreSub.hasDelivery == 0){
            holder.binding.textView11.visibility = View.GONE
            holder.binding.textView12.visibility = View.GONE
        }else if (itemViewModel.itemMoreSub.hasDelivery == 1){
            holder.binding.textView11.visibility = View.VISIBLE
            holder.binding.textView12.visibility = View.VISIBLE
        }

        itemViewModel.mutableLiveData.observeForever {
            when (it) {
                is String -> {
                    if (itemViewModel.itemMoreSub.isClosed==0) {
                        moresubLiveData.value = itemViewModel.itemMoreSub
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return moreSubList.size
    }

    fun updateList(models: MutableList<StoresDataItem>) {
        moreSubList = models
        notifyDataSetChanged()
    }

    inner class MoreSubHolder(val binding: ItemHomeMoreSubBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}
