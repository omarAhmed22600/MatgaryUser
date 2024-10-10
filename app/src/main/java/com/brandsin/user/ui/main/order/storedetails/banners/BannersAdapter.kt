package com.brandsin.user.ui.main.order.storedetails.banners

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.brandsin.user.R
import com.brandsin.user.databinding.RawSliderItemBinding
import com.brandsin.user.model.order.storedetails.CoversItem
import com.smarteist.autoimageslider.SliderViewAdapter

class BannersAdapter(
    private val onBannerClickedListener: OnBannerClickedListener
) : SliderViewAdapter<BannersAdapter.SliderHolder>() {

    var itemsList: List<CoversItem> = ArrayList()

    interface OnBannerClickedListener {
        fun onBannerClicked(position: Int, coversItem: List<CoversItem>)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderHolder {
        val context = parent!!.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawSliderItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_slider_item, parent, false)
        return SliderHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderHolder, position: Int) {
        val itemViewModel = ItemBannerViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel

        holder.binding.root.setOnClickListener {
            onBannerClickedListener.onBannerClicked(position, itemsList)
        }
    }

    fun updateList(models: List<CoversItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class SliderHolder(val binding: RawSliderItemBinding) :
        ViewHolder(binding.root)

    override fun getCount(): Int {
        return itemsList.size
    }

}
