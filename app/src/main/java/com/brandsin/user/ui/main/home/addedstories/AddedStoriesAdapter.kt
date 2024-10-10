package com.brandsin.user.ui.main.home.addedstories

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawAddedStoriesBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.homepage.addedstories.liststories.DataItem
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddedStoriesAdapter : RecyclerView.Adapter<AddedStoriesAdapter.AddedStoriesHolder>() {
     var allStories = SingleLiveEvent<ArrayList<StoriesItem>>()

    var itemsList: ArrayList<DataItem> = ArrayList()

    private var deleteStoryData = SingleLiveEvent<StoriesItem>()
    private var showStoryData = SingleLiveEvent<StoriesItem>()
    var positionClicked = SingleLiveEvent<Int>()

    private var convertDate = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedStoriesHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RawAddedStoriesBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_added_stories, parent, false)
        return AddedStoriesHolder(binding)
    }

    override fun onBindViewHolder(holder: AddedStoriesHolder, position: Int) {
        val itemViewModel = ItemAddedStoriesViewModel(itemsList[position])
        holder.binding.viewModel = itemViewModel

        convertDate(itemViewModel.item.date.toString())

        holder.binding.tvTime.text = convertDate

        itemViewModel.storyAdapter.deleteLiveData.observeForever {
            when (it) {
                is StoriesItem -> {
                    deleteStoryData.value = it
                }
            }
        }

        itemViewModel.storyAdapter.showLiveData.observeForever {
            when (it) {
                is StoriesItem -> {
                    showStoryData.value = it
                }
            }
        }

        itemViewModel.storyAdapter.allItemsList.observeForever {
            this.allStories.value = it!!
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateList(models: ArrayList<DataItem>) {
        itemsList = models
        notifyDataSetChanged()
    }

    inner class AddedStoriesHolder(val binding: RawAddedStoriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun convertDate(dateString: String): String {

        var date: Date? = null
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val temp = dateString
        try {
            date = formatter.parse(temp)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val f2: DateFormat = SimpleDateFormat("EEEE, dd-MM-yyyy", Locale(PrefMethods.getLanguage()))
        convertDate = f2.format(date)
        return convertDate
    }
}
