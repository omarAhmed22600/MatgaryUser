package com.brandsin.user.ui.main.order.storedetails.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.DialogProgressBinding
import com.brandsin.user.databinding.RawStoreProductBinding
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.utils.SingleLiveEvent
import com.brandsin.user.utils.Utils
import org.jsoup.Jsoup

class StoreProductsAdapter_V2 : RecyclerView.Adapter<BaseViewHolder>() {

    var productsList: MutableList<StoreProductItem> = ArrayList()
    var productLiveData2 = SingleLiveEvent<String>()
    var productLiveData = SingleLiveEvent<StoreProductItem>()

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    var lastScrollPosition = -1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val binding: RawStoreProductBinding = DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.raw_store_product,
                    parent,
                    false
                )
                ViewHolder(binding)
            }

            VIEW_TYPE_LOADING -> {
                val binding: DialogProgressBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.dialog_progress, parent, false)
                ProgressHolder(binding)
            }

            else -> {
                val binding: RawStoreProductBinding = DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.raw_store_product,
                    parent,
                    false
                )
                ViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    /*override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager=recyclerView.layoutManager
        if (manager is LinearLayoutManager && itemCount>0){
            val llm=manager
            recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val visiblePosition=llm.findFirstVisibleItemPosition()
                    Log.d("visiblePosition", visiblePosition.toString())
                    val categoryId=productsList[visiblePosition].category_id
                    if (lastScrollPosition!=categoryId!!){
                        productLiveData2.value=categoryId.toString()
                    }
                    lastScrollPosition=categoryId
                }
            })
        }
    }*/

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == productsList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    fun addItems(listItems: MutableList<StoreProductItem>) {
        productsList.addAll(listItems)
        notifyDataSetChanged()
    }

    fun updateList(models: ArrayList<StoreProductItem>) {
        productsList = models
        notifyDataSetChanged()
    }

    fun notifyItemSelected(item: StoreProductItem) {
        productsList.forEach {
            when (it.id) {
                item.id -> {
                    val itemSelected: StoreProductItem = productsList[productsList.indexOf(it)]
                    itemSelected.isSelected = true
                    notifyItemChanged(productsList.indexOf(it))
                }
            }
        }
    }

    fun clear() {
        productsList.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): StoreProductItem {
        return productsList[position]
    }

    inner class ViewHolder internal constructor(val binding: RawStoreProductBinding) :
        BaseViewHolder(binding.root) {
        override fun clear() {}

        override fun onBind(position: Int) {
            super.onBind(position)
            val itemViewModel = ItemStoreProductViewModel(productsList[position])
            binding.viewModel = itemViewModel

            // binding.tvPrice.paintFlags = binding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            if (itemViewModel.item.description == null || itemViewModel.item.description == "null") {
                binding.tvDescription.text = ""
            } else {
                binding.tvDescription.text = Utils.html2text(itemViewModel.item.description.toString())
            }

            // setSelected(position)
            // binding.btnAdd.setOnClickListener {
            // productLiveData.value = itemViewModel.item
            // }

            binding.rawLayout.setOnClickListener {
                productLiveData.value = itemViewModel.item
            }

            itemView.setOnClickListener {
//            if(itemViewModel.isClosed==0) {
//            moreSubLiveData.value = itemViewModel.itemMoreSub
//            }
                productLiveData.value = itemViewModel.item
            }
        }

        /*fun setSelected(position: Int) {
            when (productsList[position].isSelected) {
                true -> {
                    binding.btnAdd.setBackgroundResource(R.drawable.btn_add_selected)
                    binding.btnAdd.setTextColor(ContextCompat.getColor(MyApp.context, R.color.white))
                    binding.btnAdd.text = MyApp.context.getString(R.string.addedd)
                }
                else -> {
                    binding.btnAdd.setBackgroundResource(R.drawable.btn_add_unselected)
                    binding.btnAdd.setTextColor(ContextCompat.getColor(MyApp.context, R.color.black))
                    binding.btnAdd.text = MyApp.context.getString(R.string.add)
                }
            }
        }*/

    }

    class ProgressHolder internal constructor(val binding: DialogProgressBinding) :
        BaseViewHolder(binding.root) {
        override fun clear() {}
    }
}