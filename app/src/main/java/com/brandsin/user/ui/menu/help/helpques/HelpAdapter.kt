package com.brandsin.user.ui.menu.help.helpques

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHelpQuesBinding
import com.brandsin.user.model.menu.help.HelpQuesItem
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import org.jsoup.Jsoup

class HelpAdapter : RecyclerView.Adapter<HelpAdapter.AboutHolder>() {

    private var helpList: ArrayList<HelpQuesItem> = ArrayList()

    var helpLiveData = SingleLiveEvent<HelpQuesItem>()
    private var selectedItemPosition: Int = -1
    var isExpanded: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawHelpQuesBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_help_ques, parent, false)
        return AboutHolder(binding)
    }

    override fun onBindViewHolder(holder: AboutHolder, position: Int) {
        val itemViewModel = ItemHelpViewModel(helpList[position])
        holder.binding.viewModel = itemViewModel

        if (itemViewModel.item.isExpanded) {
            holder.binding.answerScrollview.visibility = View.VISIBLE
        } else {
            holder.binding.answerScrollview.visibility = View.GONE
        }
        if (PrefMethods.getLanguage() == "ar") {
            holder.binding.imgArrow.setImageResource(R.drawable.arrow_next_ar)
        } else {
            holder.binding.imgArrow.setImageResource(R.drawable.arrow_next)
        }

        holder.binding.tvAnswer.text = html2text(itemViewModel.item.content.toString())

        holder.binding.root.setOnClickListener {
            // helpLiveData.value = itemViewModel.item
//            if(selectedItemPosition==position) {
//                isExpanded = !isExpanded
//            }else{
//                isExpanded=true
//            }
            itemViewModel.item.isExpanded = !itemViewModel.item.isExpanded
            selectedItemPosition = position

            notifyDataSetChanged()
        }
    }

    private fun html2text(html: String): String {
        return try {
            Jsoup.parse(html).text()
        } catch (ignored: Exception) {
            ""
        }
    }

    override fun getItemCount(): Int {
        return helpList.size
    }

    fun updateList(models: ArrayList<HelpQuesItem>) {
        helpList = models
        notifyDataSetChanged()
    }

    inner class AboutHolder(val binding: RawHelpQuesBinding) : RecyclerView.ViewHolder(binding.root)
}
