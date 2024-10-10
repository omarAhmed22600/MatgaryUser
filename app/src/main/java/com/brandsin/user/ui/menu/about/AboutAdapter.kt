package com.brandsin.user.ui.menu.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeAboutBinding
import com.brandsin.user.model.menu.about.AboutItem
import com.brandsin.user.utils.SingleLiveEvent

class AboutAdapter : RecyclerView.Adapter<AboutAdapter.AboutHolder>() {

    private var aboutList: ArrayList<AboutItem> = ArrayList()
    var aboutLiveData = SingleLiveEvent<AboutItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawHomeAboutBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_about, parent, false)
        return AboutHolder(binding)
    }

    override fun onBindViewHolder(holder: AboutHolder, position: Int) {
        val itemViewModel = ItemAboutViewModel(aboutList[position])
        holder.binding.viewModel = itemViewModel

        holder.binding.rawLayout.setOnClickListener {
            aboutLiveData.value = itemViewModel.item
        }
    }

    override fun getItemCount(): Int {
        return aboutList.size
    }

    fun updateList(models: ArrayList<AboutItem>) {
        aboutList = models
        notifyDataSetChanged()
    }

    inner class AboutHolder(val binding: RawHomeAboutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
