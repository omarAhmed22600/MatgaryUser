package com.brandsin.user.ui.main.home.addedstories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.brandsin.user.R
import com.brandsin.user.databinding.RawAddedStoryBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.SingleLiveEvent

class StoryAdapter: RecyclerView.Adapter<StoryAdapter.StoryHolder>() {
    var itemsList: ArrayList<StoriesItem> = ArrayList()
    var deleteLiveData = SingleLiveEvent<StoriesItem>()
    var showLiveData = SingleLiveEvent<StoriesItem>()
    var allitemsList= SingleLiveEvent<ArrayList<StoriesItem>>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
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

        if (itemViewModel.item.media!!.isEmpty()){
            holder.binding.cvTxt.visibility = View.VISIBLE
            holder.binding.cvImg.visibility = View.GONE
            holder.binding.cvVideo.visibility = View.GONE

            holder.binding.tvTxt.text = itemViewModel.item.text.toString()

        }else if (itemViewModel.item.media!![0]!!.mimeType!!.contains("image")){
            holder.binding.cvTxt.visibility = View.GONE
            holder.binding.cvImg.visibility = View.VISIBLE
            holder.binding.cvVideo.visibility = View.GONE

            if (itemViewModel.item.mediaUrl.isNullOrEmpty()) {
                Glide.with(MyApp.context).load(R.drawable.app_background)
                    .error(R.drawable.app_background)
                    .into(holder.binding.ivImg)
            }else{
                Glide.with(MyApp.context).load(itemViewModel.item.mediaUrl).error(R.drawable.app_background).into(holder.binding.ivImg)
            }

        }else if (itemViewModel.item.media!![0]!!.mimeType!!.contains("video")){
            holder.binding.cvTxt.visibility = View.GONE
            holder.binding.cvImg.visibility = View.GONE
            holder.binding.cvVideo.visibility = View.VISIBLE

            if (itemViewModel.item.mediaUrl.isNullOrEmpty()) {
                holder.binding.ivVideo.setImageResource(R.drawable.app_background)
            }else{
                Glide.with(MyApp.context).load("")
                    .thumbnail(Glide.with(MyApp.context).load(itemViewModel.item.mediaUrl))
                    .error(R.drawable.app_background)
                    .into(holder.binding.ivVideo)
            }

        }

        holder.binding.consTxt.setOnClickListener {
            //showLiveData.value = itemViewModel.item
            allitemsList.value=itemsList
        }
        holder.binding.consImg.setOnClickListener {
          //  showLiveData.value = itemViewModel.item
            allitemsList.value=itemsList
        }
        holder.binding.consVideo.setOnClickListener {
            showLiveData.value = itemViewModel.item
            allitemsList.value=itemsList
        }

    }


    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(models: ArrayList<StoriesItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class StoryHolder(val binding: RawAddedStoryBinding) : RecyclerView.ViewHolder(binding.root)

}
