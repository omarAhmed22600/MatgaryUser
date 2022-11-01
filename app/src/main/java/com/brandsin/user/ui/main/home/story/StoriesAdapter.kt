package com.brandsin.user.ui.main.home.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemHomeStoryBinding
import com.brandsin.user.databinding.ItemStoreStoryBinding
import com.brandsin.user.databinding.RawFavouritsStoryBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.SingleLiveEvent


class StoriesAdapter(val onStoryClickedListner: OnStoryClickedListner) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var allStories: MutableList<ArrayList<StoriesItem>> = ArrayList()
    var itemsList: ArrayList<StoriesItem> = ArrayList()
    var showLiveData = SingleLiveEvent<StoriesItem>()
    var viewType: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        when (viewType) {
            0 -> {
                val binding: ItemHomeStoryBinding = DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.item_home_story,
                    parent,
                    false
                )
                return StoryHolder(binding)
            }
            2 -> {
                val binding: RawFavouritsStoryBinding = DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.raw_favourits_story,
                    parent,
                    false
                )
                return StoryFavHolderStore(binding)
            }
            else -> {
                val binding: ItemStoreStoryBinding = DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.item_store_story,
                    parent,
                    false
                )
                return StoryHolderStore(binding)
            }
        }

    }

    fun setStoryViewType(viewType: Int) {
        this.viewType = viewType
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewModel = ItemStoryViewModel(itemsList[position])
        when (holder.itemViewType) {
            0 -> {
                holder as StoryHolder

                holder.binding.viewModel = itemViewModel

                if (itemViewModel.item.store!!.thumbnail.isNullOrEmpty()) {
                    Glide.with(MyApp.context).load(R.drawable.app_logo)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.storeImg)
                } else {
                    Glide.with(MyApp.context)
                        .load(itemViewModel.item.store!!.thumbnail)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.storeImg)
                }

                Glide.with(MyApp.context)
                    .load(itemViewModel.item.mediaUrl)
                    .error(R.drawable.app_logo)
                    .into(holder.binding.bg)
                // holder.binding.storeName.text = itemViewModel.item.store!!.name.toString()

                holder.binding.root.setOnClickListener {
                    showLiveData.value = itemViewModel.item
                    onStoryClickedListner.onStoryClicked(position, allStories)
                }

            }

            2 -> {
                holder as StoryFavHolderStore

                holder.binding.viewModel = itemViewModel

                if (itemViewModel.item.store!!.thumbnail.isNullOrEmpty()) {
                    Glide.with(MyApp.context).load(R.drawable.app_logo)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.storeImg)
                } else {
                    Glide.with(MyApp.context)
                        .load(itemViewModel.item.store!!.thumbnail)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.storeImg)
                }

                Glide.with(MyApp.context)
                    .load(itemViewModel.item.mediaUrl)
                    .error(R.drawable.app_logo)
                    .into(holder.binding.bg)
                // holder.binding.storeName.text = itemViewModel.item.store!!.name.toString()

                holder.binding.root.setOnClickListener {
                    showLiveData.value = itemViewModel.item
                    onStoryClickedListner.onStoryClicked(position, allStories)
                }

            }
            else -> {
                holder as StoryHolderStore
                Glide.with(MyApp.context)
                    .load(itemViewModel.item.mediaUrl)
                    .error(R.drawable.app_logo)
                    .into(holder.binding.storyStoreImg)
                holder.binding.root.setOnClickListener {
                    showLiveData.value = itemViewModel.item
                    onStoryClickedListner.onStoryClicked(position, allStories)
                }
                holder.binding.storeName.text =if( itemViewModel.item.title!=null) itemViewModel.item.title else itemViewModel.item.store!!.name
            }
        }

    }

    interface OnStoryClickedListner {
        fun onStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(allmodels: MutableList<ArrayList<StoriesItem>>) {
        allStories = allmodels
        itemsList = prepareStoriesList(allStories)
        notifyDataSetChanged()
    }

    fun prepareStoriesList(stories: MutableList<ArrayList<StoriesItem>>): ArrayList<StoriesItem> {
        var newitemsList: ArrayList<StoriesItem> = ArrayList()
        stories.forEach {
            if (it.size > 0) {
                newitemsList.add(it.get(0))
            }
        }
        return newitemsList
    }

    inner class StoryHolder(val binding: ItemHomeStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class StoryHolderStore(val binding: ItemStoreStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class StoryFavHolderStore(val binding: RawFavouritsStoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
