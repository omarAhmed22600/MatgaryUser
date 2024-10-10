package com.brandsin.user.ui.main.homenew.ads

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemHomeAdsBinding
import com.brandsin.user.utils.SingleLiveEvent

class AdsAdapter : RecyclerView.Adapter<AdsAdapter.AdsHolder>() {

    var adsLiveData = SingleLiveEvent<String>()
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: ItemHomeAdsBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_home_ads, parent, false)
        return AdsHolder(binding)
    }

    override fun onBindViewHolder(holder: AdsHolder, position: Int) {
        val itemViewModel = ItemAdsViewModel()
        holder.binding.viewModel = itemViewModel

        itemViewModel.mutableLiveData.observeForever {
            when (it) {
                is String -> {
                    when {
                        position != selectedPosition -> {
                            notifyItemChanged(position)
                            notifyItemChanged(selectedPosition)
                            selectedPosition = position
                            adsLiveData.value = "a"
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    fun updateList() {
        notifyDataSetChanged()
    }

    inner class AdsHolder(val binding: ItemHomeAdsBinding) : RecyclerView.ViewHolder(binding.root)
}
