package com.brandsin.user.ui.main.home.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.brandsin.user.R
import com.brandsin.user.databinding.RawStoryBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.SingleLiveEvent


class StoryAdapter: RecyclerView.Adapter<StoryAdapter.StoryHolder>() {
    var itemsList: ArrayList<StoriesItem> = ArrayList()
    var showLiveData = SingleLiveEvent<StoriesItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawStoryBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.raw_story,
            parent,
            false
        )
        return StoryHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryHolder, position: Int) {
        val itemViewModel = ItemStoryViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel

        if (itemViewModel.item.store!!.thumbnail.isNullOrEmpty()) {
            Glide.with(MyApp.context).load(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(holder.binding.storeImg)
        }else{
            Glide.with(MyApp.context)
                .load(itemViewModel.item.store!!.thumbnail)
                .error(R.drawable.app_logo)
                .into(holder.binding.storeImg)
        }
        holder.binding.storeName.text = itemViewModel.item.store!!.name.toString()

        holder.binding.item.setOnClickListener {
            showLiveData.value = itemViewModel.item
        }

    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(models: ArrayList<StoriesItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class StoryHolder(val binding: RawStoryBinding) : RecyclerView.ViewHolder(binding.root)

}
