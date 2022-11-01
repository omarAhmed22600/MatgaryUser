package com.brandsin.user.ui.main.homenew.more

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemHomeMoreBinding
import com.brandsin.user.model.order.homenew.SectionsItem
import com.brandsin.user.model.order.homenew.SlidesItem
import com.brandsin.user.model.order.homenew.StoresDataItem
import com.brandsin.user.utils.SingleLiveEvent
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class MoreAdapter : RecyclerView.Adapter<MoreAdapter.MoreHolder>() {

    var moreList  = mutableListOf<SectionsItem>()
    var moreLiveData = SingleLiveEvent<SectionsItem>()
    var moresubLiveData = SingleLiveEvent<StoresDataItem>()
    var moreSliderLiveData = SingleLiveEvent<SlidesItem>()
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: ItemHomeMoreBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_home_more, parent, false)
        return MoreHolder(binding)
    }

    override fun onBindViewHolder(holder: MoreHolder, position: Int) {
        val itemViewModel = ItemMoreViewModel(moreList[position])
        holder.binding.viewModel = itemViewModel

        if (!itemViewModel.itemMore.storesData.isNullOrEmpty()){
            itemViewModel.moreSubList = itemViewModel.itemMore.storesData as MutableList<StoresDataItem>
            itemViewModel.moreSubAdapter.updateList(itemViewModel.moreSubList)

            holder.binding.rvMoreSub.visibility = View.VISIBLE
            holder.binding.sliderItem.visibility = View.GONE

        }else if (itemViewModel.itemMore.slider !=null){
            itemViewModel.moreSliderList = itemViewModel.itemMore.slider!!.slides as MutableList<SlidesItem>
            itemViewModel.moreSliderAdapter.updateList(itemViewModel.moreSliderList)

            holder.binding.bannerSlider.setSliderAdapter(itemViewModel.moreSliderAdapter)
            holder.binding.bannerSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
            holder.binding.bannerSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            holder.binding.bannerSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            holder.binding.bannerSlider.indicatorSelectedColor = Color.WHITE
            holder.binding.bannerSlider.indicatorUnselectedColor = Color.GRAY
            holder.binding.bannerSlider.scrollTimeInSec = 3
            holder.binding.bannerSlider.isAutoCycle = true
            holder.binding.bannerSlider.startAutoCycle()

            holder.binding.rvMoreSub.visibility = View.GONE
            holder.binding.sliderItem.visibility = View.VISIBLE
        }

        itemViewModel.moreSubAdapter.moresubLiveData.observeForever{
            when (it) {
                is StoresDataItem -> {
                    moresubLiveData.value = it
                }
            }
        }

        itemViewModel.moreSliderAdapter.moreSliderLiveData.observeForever{
            when (it) {
                is SlidesItem -> {
                    moreSliderLiveData.value = it
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return moreList.size
    }

    fun updateList(models: MutableList<SectionsItem>) {
        moreList = models
        notifyDataSetChanged()
    }

    inner class MoreHolder(val binding: ItemHomeMoreBinding) : RecyclerView.ViewHolder(binding.root)
}
