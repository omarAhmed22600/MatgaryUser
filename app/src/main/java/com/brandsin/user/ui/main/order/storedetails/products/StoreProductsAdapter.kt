package com.brandsin.user.ui.main.order.storedetails.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawStoreProductBinding
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.utils.SingleLiveEvent
import kotlin.collections.ArrayList

class StoreProductsAdapter : RecyclerView.Adapter<StoreProductsAdapter.MealsHolder>()
{
    var productsList: ArrayList<StoreProductItem> = ArrayList()
    var productLiveData = SingleLiveEvent<StoreProductItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawStoreProductBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_store_product, parent, false)
        return MealsHolder(binding)
    }

    override fun onBindViewHolder(holder: MealsHolder, position: Int) {
        val itemViewModel = ItemStoreProductViewModel(productsList[position])
        holder.binding.viewModel = itemViewModel

        holder.setSelected(position)
        holder.binding.root.setOnClickListener{
            productLiveData.value = itemViewModel.item
        }
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    fun updateList(models: ArrayList<StoreProductItem>) {
        productsList = models
        notifyDataSetChanged()
    }

    fun notifyItemSelected(item : StoreProductItem)
    {
        productsList.forEach {
            when (it.id) {
                item.id -> {
                    val itemSelected : StoreProductItem = productsList[productsList.indexOf(it)]
                    itemSelected.isSelected = true
                    notifyItemChanged(productsList.indexOf(it))
                }
            }
        }
    }

    inner class MealsHolder(val binding: RawStoreProductBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun setSelected(position: Int)
        {
//            when (productsList[position].isSelected) {
//                true -> {
//                    binding.btnAdd.setBackgroundResource(R.drawable.btn_add_selected)
//                    binding.btnAdd.setTextColor(ContextCompat.getColor(MyApp.context, R.color.white))
//                    binding.btnAdd.text = MyApp.context.getString(R.string.addedd)
//                }
//                else -> {
//                    binding.btnAdd.setBackgroundResource(R.drawable.btn_add_unselected)
//                    binding.btnAdd.setTextColor(ContextCompat.getColor(MyApp.context, R.color.black))
//                    binding.btnAdd.text = MyApp.context.getString(R.string.add)
//                }
           // }
        }
    }
}
