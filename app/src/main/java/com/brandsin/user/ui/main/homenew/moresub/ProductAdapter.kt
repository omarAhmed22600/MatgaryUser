package com.brandsin.user.ui.main.homenew.moresub

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemProductBinding
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.utils.Utils
import com.bumptech.glide.Glide

class ProductAdapter(
    private val btnProductItemClickCallBack: (productItem: StoreProductItem) -> Unit,
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var productItemList: List<StoreProductItem?>? = ArrayList()

    private lateinit var binding: ItemProductBinding

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productItem = productItemList!![position]

        Glide.with(holder.itemView.context)
            .load(productItem?.image)
            .error(R.drawable.app_logo)
            .into(binding.imgProduct)

        Glide.with(holder.itemView.context)
            .load(productItem?.store?.thumbnail)
            .error(R.drawable.app_logo)
            .into(binding.imgStore)

        binding.productName.text = productItem?.name
        binding.productDesc.text = Utils.html2text(productItem?.description.toString())
        binding.productPrice.text =
            "${productItem?.skus?.get(0)?.regularPrice}  ${holder.itemView.context.getString(R.string.currency)}"

        binding.root.setOnClickListener {
            if (productItem != null) {
                btnProductItemClickCallBack.invoke(productItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return productItemList?.size ?: 0
    }

    fun submitData(models: ArrayList<StoreProductItem?>?) {
        productItemList = models
        notifyDataSetChanged()
    }
}
