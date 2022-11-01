package com.brandsin.user.ui.main.search.stores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemSearchStoreBinding
import com.brandsin.user.model.order.homepage.ShopsItem
import com.brandsin.user.utils.SingleLiveEvent
import java.util.ArrayList

class StoresAdapter : RecyclerView.Adapter<StoresAdapter.StoresHolder>() {

    var StoresLiveData = SingleLiveEvent<ShopsItem>()
    var storesList: MutableList<ShopsItem> = ArrayList()
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoresHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: ItemSearchStoreBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_search_store, parent, false)
        return StoresHolder(binding)
    }

    override fun onBindViewHolder(holder: StoresHolder, position: Int) {
        val itemViewModel = ItemStoreViewModel(storesList[position])
        holder.binding.viewModel = itemViewModel

        itemViewModel.mutableLiveData.observeForever {
            when (it) {
                is String -> {
                    StoresLiveData.value = itemViewModel.item
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return storesList.size
    }

    fun updateList(models: MutableList<ShopsItem>) {
        storesList = models
        notifyDataSetChanged()
    }

    inner class StoresHolder(val binding: ItemSearchStoreBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}
