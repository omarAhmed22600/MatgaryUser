package com.brandsin.user.ui.main.home.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeCategoryBinding
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.utils.MyApp.Companion.context
import com.brandsin.user.utils.SingleLiveEvent

class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.CategoriesHolder>() {
    var categoriesList: MutableList<CategoriesItem> = ArrayList()
    var categoriesLiveData = SingleLiveEvent<CategoriesItem>()
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawHomeCategoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_category, parent, false)
        return CategoriesHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriesHolder, position: Int) {
        val itemViewModel = ItemCategoryViewModel(categoriesList[position])
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
                            categoriesLiveData.value = itemViewModel.itemCategory
                        }
                    }
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

    inner class CategoriesHolder(val binding: RawHomeCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setSelected() {
            when (selectedPosition) {
                adapterPosition -> {
                    binding.cvMain.strokeWidth = 0
                    binding.categoryItemView.visibility = View.VISIBLE
                    binding.categoryItemView.setBackgroundColor(context.getColor(R.color.color_primary))
                    binding.categoryItemName.setTextColor(context.getColor(R.color.color_primary))

                }

                else -> {
                    binding.cvMain.strokeWidth = 0
                    binding.categoryItemView.visibility = View.GONE
                    binding.categoryItemName.setTextColor(context.getColor(R.color.black))
                }
            }
        }
    }
}
