package com.brandsin.user.ui.menu.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawFavouritsStoryBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.utils.storyviewer.utils.hide
import com.brandsin.user.utils.storyviewer.utils.show
import com.bumptech.glide.Glide

class DiscoverStoriesAdapter(
    private val onFavoriteStoryClickedListener: StoriesAdapter.OnStoryClickedListener,
) : RecyclerView.Adapter<DiscoverStoriesAdapter.StoryHolder>() {

    var itemsList: List<List<StoriesItem?>?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawFavouritsStoryBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.raw_favourits_story,
            parent,
            false
        )
        return StoryHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryHolder, position: Int) {
        val item = itemsList[position]
        // val itemViewModel = ItemStoryViewModel(itemsList[position])
        // holder.binding.viewModel = itemViewModel

        if (item.orEmpty().first()!!.id == item.orEmpty().first()!!.store?.id) {
            holder.binding.storeImg.hide()
        }

        if (item.orEmpty().first()!!.store?.thumbnail.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(holder.binding.storeImg)
        } else {
            Glide.with(holder.itemView.context)
                .load(item.orEmpty().first()!!.store!!.thumbnail)
                .error(R.drawable.app_logo)
                .into(holder.binding.storeImg)
        }

        if (item.orEmpty().first()!!.mediaUrl?.isNotEmpty() == true && item.orEmpty().first()!!.text?.isNotEmpty() == true) {
            holder.binding.textStory.show()
            holder.binding.bgTextStory.hide()
            holder.binding.bg.show()
            Glide.with(holder.itemView.context)
                .load(item.orEmpty().first()!!.mediaUrl)
                .error(R.drawable.app_logo)
                .into(holder.binding.bg)

            // set text of story with image of story
            holder.binding.textStory.text = item.orEmpty().first()!!.text.toString()

        } else if (item.orEmpty().first()!!.mediaUrl.isNullOrEmpty() && item.orEmpty().first()!!.text?.isNotEmpty() == true) {
            holder.binding.bg.hide()
            holder.binding.bgTextStory.show()
            holder.binding.textStory.show()
            // set text of story with image of story
            holder.binding.textStory.text = item.orEmpty().first()!!.text.toString()
        } else {
            holder.binding.textStory.hide()
            holder.binding.bgTextStory.hide()
            holder.binding.bg.show()
            Glide.with(holder.itemView.context)
                .load(item.orEmpty().first()!!.mediaUrl)
                .error(R.drawable.app_logo)
                .into(holder.binding.bg)
        }

        // holder.binding.storeName.text = itemViewModel.item.orEmpty().first().store!!.name.toString()

        holder.binding.root.setOnClickListener {
            onFavoriteStoryClickedListener.onStoryClicked(position, itemsList as MutableList<ArrayList<StoriesItem>>)
        }
    }

    private fun getFileExtension(url: String): String {
        val lastDotIndex = url.lastIndexOf(".")
        return if (lastDotIndex != -1) {
            url.substring(lastDotIndex + 1)
        } else {
            // Default extension or handle the case when there's no extension
            ""
        }
    }

    interface OnFavoriteStoryClickedListener {
        fun onFavoriteStoryClicked(position: Int, stories: MutableList<ArrayList<StoriesItem>>)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(models: List<List<StoriesItem?>?>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class StoryHolder(val binding: RawFavouritsStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

}
