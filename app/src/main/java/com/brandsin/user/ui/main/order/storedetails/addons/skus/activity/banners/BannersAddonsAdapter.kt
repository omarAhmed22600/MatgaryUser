package com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.banners

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.brandsin.user.R
import com.brandsin.user.databinding.RawSliderProductItemBinding
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.smarteist.autoimageslider.SliderViewAdapter

class BannersAddonsAdapter(
    private val onBannerClickedListener: OnBannerAddonsClickedListener
) : SliderViewAdapter<BannersAddonsAdapter.SliderHolder>() {

    var itemsList: ArrayList<ImagesIdsItem> = ArrayList()

    interface OnBannerAddonsClickedListener {
        fun onBannerAddonsClicked(position: Int, imagesIdsItem: List<ImagesIdsItem?>?)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val binding: RawSliderProductItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_slider_product_item, parent, false)
        return SliderHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderHolder, position: Int) {
        val itemViewModel = ItemBannerViewModel(itemsList!![position]!!)
        holder.binding.viewModel = itemViewModel

        holder.binding.root.setOnClickListener {
            onBannerClickedListener.onBannerAddonsClicked(position, itemsList)
        }
    }

    fun updateList(models: ArrayList<ImagesIdsItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    fun clearList() {
        itemsList.clear()
    }

    inner class SliderHolder(val binding: RawSliderProductItemBinding) :
        ViewHolder(binding.root)

    override fun getCount(): Int {
        return itemsList?.size ?: 0
    }

}
