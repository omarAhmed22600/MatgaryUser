package com.brandsin.user.ui.main.home.stores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeStoreBinding
import com.brandsin.user.databinding.RawHomeStoreV2Binding
import com.brandsin.user.model.order.homepage.ShopsItem
import com.brandsin.user.utils.SingleLiveEvent

class StoresAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var storesList: MutableList<ShopsItem> = ArrayList()
    var productsLiveData = SingleLiveEvent<ShopsItem>()
    var viewType: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RawHomeStoreBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_store, parent, false)
        val binding2: RawHomeStoreV2Binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_store_v2, parent, false)
        return when (viewType) {
            0 ->
                ProductsHolder(binding)

            else ->
                ProductsHolder2(binding2)
        }
    }

    fun setAdapterViewType(viewType: Int) {
        this.viewType = viewType
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewModel = ItemStoreViewModel(storesList[position])
        when (holder.itemViewType) {
            0 -> {
                val productsHolder = holder as ProductsHolder
                productsHolder.binding.viewModel = itemViewModel
                if (itemViewModel.item.hasDelivery == 0) {
                    productsHolder.binding.tvDeliveryPrice.visibility = View.GONE
                } else if (itemViewModel.item.hasDelivery == 1) {
                    productsHolder.binding.tvDeliveryPrice.visibility = View.VISIBLE
                }

                productsHolder.binding.rawLayout.setOnClickListener {
                    if (itemViewModel.item.isClosed == 0) {
                        productsLiveData.value = itemViewModel.item
                    }
                }
            }

            else -> {
                val productsHolder2 = holder as ProductsHolder2
                productsHolder2.binding.viewModel = itemViewModel

                if (itemViewModel.item.hasDelivery == 0) {
                    productsHolder2.binding.tvDeliveryPrice.visibility = View.GONE
                } else if (itemViewModel.item.hasDelivery == 1) {
                    productsHolder2.binding.tvDeliveryPrice.visibility = View.VISIBLE
                }

                productsHolder2.binding.rawLayout.setOnClickListener {
                    if (itemViewModel.item.isClosed == 0) {
                        productsLiveData.value = itemViewModel.item
                    }
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

    inner class ProductsHolder(val binding: RawHomeStoreBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ProductsHolder2(val binding: RawHomeStoreV2Binding) :
        RecyclerView.ViewHolder(binding.root)
}
