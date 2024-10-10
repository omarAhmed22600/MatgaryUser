package com.brandsin.user.ui.main.home.addedstories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawAddedStoryBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.utils.SingleLiveEvent
import com.bumptech.glide.Glide

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryHolder>() {
    var itemsList: ArrayList<StoriesItem> = ArrayList()
    var deleteLiveData = SingleLiveEvent<StoriesItem>()
    var showLiveData = SingleLiveEvent<StoriesItem>()
    var allItemsList = SingleLiveEvent<ArrayList<StoriesItem>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RawAddedStoryBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.raw_added_story,
            parent,
            false
        )
        return StoryHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryHolder, position: Int) {
        val itemViewModel = ItemStoryViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel

        // if (itemViewModel.item.media!!.isEmpty()) {
        if (itemViewModel.item.mediaUrl.isNullOrEmpty()) {
            holder.binding.cvTxt.visibility = View.VISIBLE
            holder.binding.cvImg.visibility = View.GONE
            holder.binding.cvVideo.visibility = View.GONE

            holder.binding.tvTxt.text = itemViewModel.item.text ?: itemViewModel.item.title

            // } else if (itemViewModel.item.media!![0].mimeType!!.contains("image")) {
        } else if (getFileExtension(itemViewModel.item.mediaUrl.toString()) == "jpeg" ||
            getFileExtension(itemViewModel.item.mediaUrl.toString()) == "png" ||
            getFileExtension(itemViewModel.item.mediaUrl.toString()) == "jpg"
        ) {
            holder.binding.cvTxt.visibility = View.GONE
            holder.binding.cvImg.visibility = View.VISIBLE
            holder.binding.cvVideo.visibility = View.GONE

            if (itemViewModel.item.mediaUrl.isNullOrEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(R.drawable.app_background)
                    .error(R.drawable.app_background)
                    .into(holder.binding.ivImg)
            } else {
                Glide.with(holder.itemView.context)
                    .load(itemViewModel.item.mediaUrl)
                    .error(R.drawable.app_background)
                    .into(holder.binding.ivImg)
            }

            // } else if (itemViewModel.item.media!![0].mimeType!!.contains("video")) {
        } else if (getFileExtension(itemViewModel.item.mediaUrl.toString()) == "mp4") {
            holder.binding.cvTxt.visibility = View.GONE
            holder.binding.cvImg.visibility = View.GONE
            holder.binding.cvVideo.visibility = View.VISIBLE

            if (itemViewModel.item.mediaUrl.isNullOrEmpty()) {
                holder.binding.ivVideo.setImageResource(R.drawable.app_background)
            } else {
                Glide.with(holder.itemView.context).load("")
                    .thumbnail(
                        Glide.with(holder.itemView.context).load(itemViewModel.item.mediaUrl)
                    )
                    .error(R.drawable.app_background)
                    .into(holder.binding.ivVideo)
            }
        }

        holder.binding.consTxt.setOnClickListener {
            //showLiveData.value = itemViewModel.item
            allItemsList.value = itemsList
        }
        holder.binding.consImg.setOnClickListener {
            //  showLiveData.value = itemViewModel.item
            allItemsList.value = itemsList
        }
        holder.binding.consVideo.setOnClickListener {
            showLiveData.value = itemViewModel.item
            allItemsList.value = itemsList
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


    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(models: ArrayList<StoriesItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class StoryHolder(val binding: RawAddedStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

}
