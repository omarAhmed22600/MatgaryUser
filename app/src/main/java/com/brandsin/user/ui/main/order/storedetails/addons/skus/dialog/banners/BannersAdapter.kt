package com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.banners

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.brandsin.user.R
import com.brandsin.user.databinding.RawSliderProductItemBinding
import com.brandsin.user.model.order.storedetails.ImagesIdsItem
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.banners.ItemBannerViewModel
import com.smarteist.autoimageslider.SliderViewAdapter

class BannersAdapter(
    private val onBannerClickedListener: OnBannersClickedListener
) : SliderViewAdapter<BannersAdapter.SliderHolder>() {
    var itemsList: List<ImagesIdsItem?>? = ArrayList()

    interface OnBannersClickedListener {
        fun onBannersClicked(position: Int, imagesIdsItem: List<ImagesIdsItem?>?)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderHolder {
        val context = parent!!.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawSliderProductItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_slider_product_item, parent, false)
        return SliderHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderHolder, position: Int) {
        val itemViewModel = ItemBannerViewModel(itemsList!![position]!!)
        holder.binding.viewModel = itemViewModel

        holder.binding.root.setOnClickListener {
            onBannerClickedListener.onBannersClicked(position, itemsList)
        }
    }

    fun updateList(models: List<ImagesIdsItem?>?) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class SliderHolder(val binding: RawSliderProductItemBinding) :
        ViewHolder(binding.root)

    override fun getCount(): Int {
        return itemsList?.size ?: 0
    }
}
