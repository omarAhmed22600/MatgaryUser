package com.brandsin.user.ui.dialogs.homepopup.slider

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemPopupSliderHomeBinding
import com.brandsin.user.model.order.homenew.PopupsItem
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.SingleLiveEvent
import com.smarteist.autoimageslider.SliderViewAdapter
import java.util.ArrayList

class PopupSliderAdapter: SliderViewAdapter<PopupSliderAdapter.SliderHolder>()
{
    var itemsList: List<PopupsItem> = ArrayList()
    var popupSliderLiveData = SingleLiveEvent<PopupsItem>()

    override fun onCreateViewHolder(parent: ViewGroup?): SliderHolder {
        val context = parent!!.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: ItemPopupSliderHomeBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_popup_slider_home, parent, false)
        return SliderHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderHolder, position: Int) {
        val itemViewModel = ItemPopupSliderViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel

//        if (PrefMethods.getLanguage()=="ar") {
            if (itemViewModel.item.content.isNullOrEmpty()) {
                Glide.with(MyApp.context).load(R.drawable.app_logo).error(R.drawable.app_logo)
                    .into(holder.binding.ivAutoImageSlider)
            } else {
                Glide.with(MyApp.context).load(itemViewModel.item.content)
                    .error(R.drawable.app_logo).into(holder.binding.ivAutoImageSlider)
            }
//        }else if (PrefMethods.getLanguage()=="en") {
//            if (itemViewModel.item.contentEn.isNullOrEmpty()) {
//                Glide.with(MyApp.context).load(R.drawable.app_logo).error(R.drawable.app_logo)
//                    .into(holder.binding.ivAutoImageSlider)
//            } else {
//                Glide.with(MyApp.context).load(itemViewModel.item.contentEn)
//                    .error(R.drawable.app_logo).into(holder.binding.ivAutoImageSlider)
//            }
//        }

        itemViewModel.mutableLiveData.observeForever {
            when (it) {
                is String -> {
                    popupSliderLiveData.value = itemViewModel.item
                }
            }
        }
    }

    fun updateList(models: List<PopupsItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class SliderHolder(val binding: ItemPopupSliderHomeBinding) : SliderViewAdapter.ViewHolder(binding.root)

    override fun getCount(): Int {
        return itemsList.size
    }

}
