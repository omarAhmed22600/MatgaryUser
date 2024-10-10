package com.brandsin.user.ui.main.homenew.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemHomeCategoryBinding
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.utils.SingleLiveEvent

class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.CategoriesHolder>() {
    var categoriesList = mutableListOf<CategoriesItem>()
    var categoriesLiveData = SingleLiveEvent<CategoriesItem>()
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: ItemHomeCategoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_home_category, parent, false)
        return CategoriesHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriesHolder, position: Int) {
        val itemViewModel = ItemCategoryViewModel(categoriesList[position])
        holder.binding.viewModel = itemViewModel

        itemViewModel.mutableLiveData.observeForever {
            when (it) {
                is String -> {
                    categoriesLiveData.value = itemViewModel.itemCategory
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    fun updateList(models: MutableList<CategoriesItem>) {
        categoriesList = models
        notifyDataSetChanged()
    }

    inner class CategoriesHolder(val binding: ItemHomeCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
