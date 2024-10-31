package com.brandsin.user.ui.main.homenew.moreslider

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemHomeMoreSliderBinding
import com.brandsin.user.model.order.homenew.SlidesItem
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import com.smarteist.autoimageslider.SliderViewAdapter
import java.util.ArrayList

class MoreSliderAdapter(var clickListener: (SlidesItem) -> Unit) : SliderViewAdapter<MoreSliderAdapter.SliderHolder>() {
    var itemsList: List<SlidesItem> = ArrayList()
    var moreSliderLiveData = SingleLiveEvent<SlidesItem>()

    override fun onCreateViewHolder(parent: ViewGroup?): SliderHolder {
        val context = parent!!.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: ItemHomeMoreSliderBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_home_more_slider, parent, false)
        return SliderHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderHolder, position: Int) {
        val itemViewModel = ItemMoreSliderViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel

        // Setting up click listener
        holder.binding.root.setOnClickListener {
            clickListener.invoke(itemViewModel.item)
        }

        if (PrefMethods.getLanguage() == "ar") {
            if (itemViewModel.item.content.isNullOrEmpty()) {
                Glide.with(MyApp.context).load(R.drawable.app_logo).error(R.drawable.app_logo)
                    .into(holder.binding.ivAutoImageSlider)
            } else {
                Glide.with(MyApp.context).load(
                    if (!(itemViewModel.item.content.toString()
                            .contains(MyApp.context.getString(R.string.app_url)))
                    )
                        itemViewModel.item.content else itemViewModel.item.content
                ).error(R.drawable.app_logo).into(holder.binding.ivAutoImageSlider)
            }
        } else if (PrefMethods.getLanguage() == "en") {
            if (itemViewModel.item.contentEn.isNullOrEmpty()) {
                Glide.with(MyApp.context).load(R.drawable.app_logo).error(R.drawable.app_logo)
                    .into(holder.binding.ivAutoImageSlider)
            } else {
                Glide.with(MyApp.context).load(
                    if (!(itemViewModel.item.contentEn.toString()
                            .contains(MyApp.context.getString(R.string.app_url)))
                    )
                        itemViewModel.item.contentEn else itemViewModel.item.contentEn
                ).error(R.drawable.app_logo).into(holder.binding.ivAutoImageSlider)
            }
        }

        itemViewModel.mutableLiveData.observeForever {
            when (it) {
                is String -> {
                    moreSliderLiveData.value = itemViewModel.item
                }
            }
        }
    }

    fun updateList(models: List<SlidesItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class SliderHolder(val binding: ItemHomeMoreSliderBinding) : ViewHolder(binding.root)

    override fun getCount(): Int {
        return itemsList.size
    }
}

