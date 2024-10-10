package com.brandsin.user.ui.menu.favourits.adapter

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
import timber.log.Timber

class FavoriteStoriesAdapter(
    private val onFavoriteStoryClickedListener: StoriesAdapter.OnStoryClickedListener,
) : RecyclerView.Adapter<FavoriteStoriesAdapter.StoryHolder>() {

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
        Timber.e("item is $item")

        if (item?.first()?.id == item?.first()?.store?.id) {
            holder.binding.storeImg.hide()
        }

        Glide.with(holder.itemView.context)
            .load(item?.first()?.store?.thumbnail)
            .error(R.drawable.app_logo)
            .into(holder.binding.storeImg)

        if (item?.first()?.mediaUrl?.isNotEmpty() == true && item.first()?.text?.isNotEmpty() == true) {
            holder.binding.textStory.show()
            holder.binding.bgTextStory.hide()
            holder.binding.bg.show()
            Glide.with(holder.itemView.context)
                .load(item.first()?.mediaUrl)
                .error(R.drawable.app_logo)
                .into(holder.binding.bg)

            // set text of story with image of story
            holder.binding.textStory.text = item.first()?.text.toString()

        } else if (item?.first()?.mediaUrl.isNullOrEmpty() && item?.first()?.text?.isNotEmpty() == true) {
            holder.binding.bg.hide()
            holder.binding.bgTextStory.show()
            holder.binding.textStory.show()
            // set text of story with image of story
            holder.binding.textStory.text = item?.first()?.text.toString()
        } else {
            holder.binding.textStory.hide()
            holder.binding.bgTextStory.hide()
            holder.binding.bg.show()
            Glide.with(holder.itemView.context)
                .load(item?.first()?.mediaUrl)
                .error(R.drawable.app_logo)
                .into(holder.binding.bg)
        }

        // holder.binding.storeName.text = itemViewModel.item.store!!.name.toString()

        holder.binding.root.setOnClickListener {
            onFavoriteStoryClickedListener.onStoryClicked(position,itemsList as MutableList<ArrayList<StoriesItem>>)
        }

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
