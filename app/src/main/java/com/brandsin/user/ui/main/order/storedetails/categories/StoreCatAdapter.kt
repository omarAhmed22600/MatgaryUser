package com.brandsin.user.ui.main.order.storedetails.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeStoreCatBinding
import com.brandsin.user.model.order.storedetails.StoreCategoryItem
import com.brandsin.user.utils.SingleLiveEvent
import java.util.*

class StoreCatAdapter : RecyclerView.Adapter<StoreCatAdapter.SubCategoriesHolder>()
{
    var subCategoriesList: MutableList<StoreCategoryItem> = ArrayList()
    var subCategoriesLiveData = SingleLiveEvent<StoreCategoryItem>()
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoriesHolder
    {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawHomeStoreCatBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_store_cat, parent, false)
        return SubCategoriesHolder(binding)
    }

    override fun onBindViewHolder(holder: SubCategoriesHolder, position: Int)
    {
        val itemViewModel = ItemStoreCatViewModel(subCategoriesList[position])
        holder.binding.viewModel = itemViewModel
        holder.setSelected()
        itemViewModel.mutableLiveData.observeForever {
            when (it) {
                is String -> {
                    when {
                        position != selectedPosition -> {
                            notifyItemChanged(position)
                            notifyItemChanged(selectedPosition)
                            selectedPosition = position
                            subCategoriesLiveData.value = itemViewModel.itemCategory
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return subCategoriesList.size
    }

    fun updateList(models: MutableList<StoreCategoryItem>) {
        subCategoriesList = models
        notifyDataSetChanged()
    }

    inner class SubCategoriesHolder(val binding: RawHomeStoreCatBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun setSelected()
        {
            when (selectedPosition) {
                adapterPosition -> {
                    //binding.cvMain.strokeWidth = 5
                    binding.categoryName.setTextColor(binding.root.context.getColor(R.color.color_primary))
                    binding.rawLayout.background=binding.root.context.getDrawable(R.drawable.store_category_bg)
                }
                else -> {
                  //  binding.cvMain.strokeWidth = 0
                    binding.categoryName.setTextColor(binding.root.context.getColor(R.color.color_dark))
                    binding.rawLayout.background=binding.root.context.getDrawable(R.drawable.store_category_bg_not_active)
                }
            }
        }
    }
}
