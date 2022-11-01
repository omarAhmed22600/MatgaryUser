package com.brandsin.user.ui.main.order.storedetails.banners

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.brandsin.user.R
import com.brandsin.user.databinding.RawSliderItemBinding
import com.brandsin.user.model.order.storedetails.CoversItem
import com.smarteist.autoimageslider.SliderViewAdapter
import java.util.*

class BannersAdapter : SliderViewAdapter<BannersAdapter.SliderHolder>()
{
    var itemsList: List<CoversItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?): SliderHolder {
        val context = parent!!.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawSliderItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_slider_item, parent, false)
        return SliderHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderHolder, position: Int) {
        val itemViewModel = ItemBannerViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel
    }

    fun updateList(models: List<CoversItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class SliderHolder(val binding: RawSliderItemBinding) : SliderViewAdapter.ViewHolder(binding.root)

    override fun getCount(): Int {
        return itemsList.size
    }

}
