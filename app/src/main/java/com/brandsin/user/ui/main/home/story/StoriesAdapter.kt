package com.brandsin.user.ui.main.home.story

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemHomeStoryBinding
import com.brandsin.user.databinding.ItemStoreStoryBinding
import com.brandsin.user.databinding.ItemStoryStoreDetailsBinding
import com.brandsin.user.databinding.RawFavouritsStoryBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.utils.SingleLiveEvent
import com.brandsin.user.utils.storyviewer.utils.hide
import com.bumptech.glide.Glide

class StoriesAdapter(
    private val onStoryClickedListener: OnStoryClickedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var allStories: MutableList<ArrayList<StoriesItem>> = ArrayList()
    var itemsList: ArrayList<StoriesItem> = ArrayList()
    private var showLiveData = SingleLiveEvent<StoriesItem>()
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

            3 -> {
                val binding: ItemStoryStoreDetailsBinding = DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.item_story_store_details,
                    parent,
                    false
                )
                return StoryStoreDetailsViewHolder(binding)
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

                // set small image of store image circle
                if (itemViewModel.item.store?.thumbnail.isNullOrEmpty()) {
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.app_logo)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.storeImg)
                } else {
                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.store?.thumbnail)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.storeImg)
                }

                if (itemViewModel.item.mediaUrl?.endsWith(".mp4", true) == true) {
                    holder.binding.icVideo.visibility = View.VISIBLE
                    // holder.binding.consTxt.visibility = View.GONE

                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.mediaUrl)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.bg)

                } else if (itemViewModel.item.mediaUrl?.endsWith(".png", true) == true ||
                    itemViewModel.item.mediaUrl?.endsWith(".jpg", true) == true ||
                    itemViewModel.item.mediaUrl?.endsWith(".jpeg", true) == true
                ) {
                    holder.binding.icVideo.visibility = View.GONE
                    // holder.binding.consTxt.visibility = View.GONE

                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.mediaUrl)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.bg)
                } else if (itemViewModel.item.mediaUrl.isNullOrEmpty() && itemViewModel.item.text?.isNotEmpty() == true) {
                    holder.binding.bg.visibility = View.GONE
                    holder.binding.icVideo.visibility = View.GONE
                    // holder.binding.consTxt.visibility = View.VISIBLE

                    // holder.binding.storyText.text = itemViewModel.item.text.toString()

                } else if (itemViewModel.item.mediaUrl?.isNotEmpty() == true && itemViewModel.item.text?.isNotEmpty() == true) {
                    holder.binding.bg.visibility = View.VISIBLE
                    holder.binding.icVideo.visibility = View.GONE
                    // holder.binding.consTxt.visibility = View.VISIBLE

                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.mediaUrl)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.bg)

                    // holder.binding.storyText.text = itemViewModel.item.text.toString()

                } else {
                    holder.binding.bg.visibility = View.VISIBLE
                    holder.binding.icVideo.visibility = View.GONE
                    // holder.binding.consTxt.visibility = View.GONE

                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.mediaUrl)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.bg)
                }

                // holder.binding.storeName.text = itemViewModel.item.store!!.name.toString()

                holder.binding.root.setOnClickListener {
                    showLiveData.value = itemViewModel.item
                    onStoryClickedListener.onStoryClicked(position, allStories)
                }
            }

            2 -> {
                holder as StoryFavHolderStore

                holder.binding.viewModel = itemViewModel

                if (itemViewModel.item.id == itemViewModel.item.store!!.id) {
                    holder.binding.storeImg.hide()
                }

                if (itemViewModel.item.store!!.thumbnail.isNullOrEmpty()) {
                    Glide.with(holder.itemView.context).load(R.drawable.app_logo)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.storeImg)
                } else {
                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.store!!.thumbnail)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.storeImg)
                }

                Glide.with(holder.itemView.context)
                    .load(itemViewModel.item.mediaUrl)
                    .error(R.drawable.app_logo)
                    .into(holder.binding.bg)
                // holder.binding.storeName.text = itemViewModel.item.store!!.name.toString()

                holder.binding.root.setOnClickListener {
                    showLiveData.value = itemViewModel.item
                    onStoryClickedListener.onStoryClicked(position, allStories)
                }
            }

            3 -> {
                holder as StoryStoreDetailsViewHolder

                holder.binding.viewModel = itemViewModel

                if (itemViewModel.item.mediaUrl?.endsWith(".mp4", true) == true) {
                    holder.binding.bg.visibility = View.VISIBLE
                    holder.binding.icVideo.visibility = View.VISIBLE
                    holder.binding.bgTextStory.visibility = View.GONE
                    holder.binding.storyText.visibility = View.GONE

                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.mediaUrl)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.bg)

                } else if (itemViewModel.item.mediaUrl?.endsWith(".png", true) == true ||
                    itemViewModel.item.mediaUrl?.endsWith(".jpg", true) == true ||
                    itemViewModel.item.mediaUrl?.endsWith(".jpeg", true) == true
                ) {
                    holder.binding.bg.visibility = View.VISIBLE
                    holder.binding.icVideo.visibility = View.GONE
                    holder.binding.bgTextStory.visibility = View.GONE
                    holder.binding.storyText.visibility = View.GONE

                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.mediaUrl)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.bg)

                } else if (itemViewModel.item.mediaUrl.isNullOrEmpty() && itemViewModel.item.text?.isNotEmpty() == true) {
                    holder.binding.bg.visibility = View.GONE
                    holder.binding.icVideo.visibility = View.GONE
                    holder.binding.bgTextStory.visibility = View.VISIBLE
                    holder.binding.storyText.visibility = View.VISIBLE

                    holder.binding.storyText.text = itemViewModel.item.text.toString()

                } else if (itemViewModel.item.mediaUrl?.isNotEmpty() == true && itemViewModel.item.text?.isNotEmpty() == true) {
                    holder.binding.bg.visibility = View.VISIBLE
                    holder.binding.icVideo.visibility = View.GONE
                    holder.binding.bgTextStory.visibility = View.VISIBLE
                    holder.binding.storyText.visibility = View.VISIBLE

                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.mediaUrl)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.bg)

                    holder.binding.storyText.text = itemViewModel.item.text.toString()

                } else {
                    holder.binding.bg.visibility = View.VISIBLE
                    holder.binding.icVideo.visibility = View.GONE
                    holder.binding.bgTextStory.visibility = View.GONE
                    holder.binding.storyText.visibility = View.GONE

                    Glide.with(holder.itemView.context)
                        .load(itemViewModel.item.mediaUrl)
                        .error(R.drawable.app_logo)
                        .into(holder.binding.bg)
                }

                holder.binding.root.setOnClickListener {
                    showLiveData.value = itemViewModel.item
                    onStoryClickedListener.onStoryClicked(position, allStories)
                }
            }

            else -> {
                holder as StoryHolderStore
                Glide.with(holder.itemView.context)
                    .load(itemViewModel.item.mediaUrl)
                    .error(R.drawable.app_logo)
                    .into(holder.binding.storyStoreImg)

                holder.binding.root.setOnClickListener {
                    showLiveData.value = itemViewModel.item
                    onStoryClickedListener.onStoryClicked(position, allStories)
                }
                holder.binding.storeName.text =
                    if (itemViewModel.item.title != null) itemViewModel.item.title else itemViewModel.item.store!!.name
            }
        }

    }

    interface OnStoryClickedListener {
        fun onStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(allModels: MutableList<ArrayList<StoriesItem>>) {
        allStories = allModels
        itemsList = prepareStoriesList(allStories)
        notifyDataSetChanged()
    }

    private fun prepareStoriesList(stories: MutableList<ArrayList<StoriesItem>>): ArrayList<StoriesItem> {
        val newItemsList: ArrayList<StoriesItem> = ArrayList()
        stories.forEach {
            if (it.size > 0) {
                newItemsList.add(it[0])
            }
        }
        return newItemsList
    }

    inner class StoryHolder(val binding: ItemHomeStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class StoryHolderStore(val binding: ItemStoreStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class StoryFavHolderStore(val binding: RawFavouritsStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class StoryStoreDetailsViewHolder(val binding: ItemStoryStoreDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
