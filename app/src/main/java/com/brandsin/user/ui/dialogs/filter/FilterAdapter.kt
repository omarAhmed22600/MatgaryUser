package com.brandsin.user.ui.dialogs.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemFilterBinding
import com.brandsin.user.model.order.homepage.TagsItem
import com.brandsin.user.utils.SingleLiveEvent
import java.util.ArrayList

class FilterAdapter : RecyclerView.Adapter<FilterAdapter.FilterHolder>() {


    var FilterLiveData = SingleLiveEvent<TagsItem>()
    var tagsList: MutableList<TagsItem> = ArrayList()
    var tagsId  = ArrayList<Int>()
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: ItemFilterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_filter, parent, false)
        return FilterHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterHolder, position: Int) {
        val itemViewModel = ItemFilterViewModel(tagsList[position])
        holder.binding.viewModel = itemViewModel

        itemViewModel.mutableLiveData.observeForever {
            when (it) {
                is String -> {
                    FilterLiveData.value = itemViewModel.itemTags
                }
            }
        }

        if (tagsId.size > 0 && itemViewModel.itemTags.id != null) {
            if (tagsId.contains(itemViewModel.itemTags.id!!.toInt())) {
                holder.binding.checkBox.isChecked = true
            }
        }else{
            if (itemViewModel.itemTags.id == null) {
                holder.binding.checkBox.isChecked = true
            }
        }

    }

    override fun getItemCount(): Int {
        return tagsList.size
    }

    fun updateList(models: MutableList<TagsItem>, tagsID: ArrayList<Int>) {
        tagsList = models
        tagsId = tagsID
        notifyDataSetChanged()
    }

    inner class FilterHolder(val binding: ItemFilterBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}
