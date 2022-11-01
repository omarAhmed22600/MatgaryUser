package com.brandsin.user.ui.main.home.subcategory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeSubcategoryBinding
import com.brandsin.user.model.order.homepage.TagsItem
import com.brandsin.user.utils.SingleLiveEvent
import java.util.*

class SubCategoriesAdapter : RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesHolder>() {
    var subCategoriesList: MutableList<TagsItem> = ArrayList()
    var subCatLiveData = SingleLiveEvent<TagsItem>()
    var selectedPosition = 0
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoriesHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawHomeSubcategoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_subcategory, parent, false)
        return SubCategoriesHolder(binding)
    }

    override fun onBindViewHolder(holder: SubCategoriesHolder, position: Int) {
        val itemViewModel = ItemSubCategoryViewModel(subCategoriesList[position])
        holder.binding.viewModel = itemViewModel

        if (selectedPosition == position) {
            holder.binding.cvMain.strokeWidth = 0
            holder.binding.categoryItemView.visibility=View.VISIBLE
            holder.binding.categoryItemView.setBackgroundColor(context.getColor(R.color.color_primary))
            holder.binding.categoryItemName.setTextColor(context.getColor(R.color.color_primary))

        } else {
            holder.binding.cvMain.strokeWidth = 0
            holder.binding.categoryItemView.visibility=View.GONE
            holder.binding.categoryItemName.setTextColor(context.getColor(R.color.black))
        }
        holder.binding.cvMain.strokeColor = context.getColor(R.color.color_primary)
        itemViewModel.mutableLiveData.observeForever {
            when (it) {
                is String -> {
                    when {
                        position != selectedPosition -> {
                            selectedPosition = position
                            notifyDataSetChanged()
                            subCatLiveData.value = itemViewModel.itemCategory
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return subCategoriesList.size
    }

    fun updateList(models: MutableList<TagsItem>) {
        subCategoriesList = models
        notifyDataSetChanged()
    }

    inner class SubCategoriesHolder(val binding: RawHomeSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}
