package com.brandsin.user.ui.profile.favoriteProduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.databinding.ItemFavoriteProductBinding
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.bumptech.glide.Glide
import org.jsoup.Jsoup

class FavoriteProductAdapter(
    private val onItemClickCallBack: (favoriteProduct: StoreProductItem) -> Unit,
    private val onRemoveFavoriteClickCallBack: (favoriteProduct: StoreProductItem) -> Unit,
) : RecyclerView.Adapter<FavoriteProductAdapter.FavoriteProductViewHolder>() {

    private var favoriteProductList: List<StoreProductItem?>? = ArrayList()

    private lateinit var binding: ItemFavoriteProductBinding

    inner class FavoriteProductViewHolder(itemView: ItemFavoriteProductBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val imgProduct = itemView.imgProduct
        val imgProductFavorite = itemView.imgProductFavorite
        val productName = itemView.productName
        val productType = itemView.productType
        val productPrice = itemView.productPrice
        val icPLusProduct = itemView.icPLusProduct
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteProductViewHolder {
        binding =
            ItemFavoriteProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteProductViewHolder, position: Int) {
        val favoriteProduct = favoriteProductList!![position]
        // bind view
        bindData(holder, favoriteProduct!!)
    }

    private fun bindData(holder: FavoriteProductViewHolder, favoriteProduct: StoreProductItem) {

        holder.productName.text = favoriteProduct.name
        holder.productType.text = html2text(favoriteProduct.description.toString())
        holder.productPrice.text = favoriteProduct.skus!![0]?.price.toString()
        Glide.with(holder.itemView.context)
            .load(favoriteProduct.image)
            .into(holder.imgProduct)

        holder.itemView.rootView.setOnClickListener {
            onItemClickCallBack.invoke(favoriteProduct)
        }

        holder.icPLusProduct.setOnClickListener {
            onItemClickCallBack.invoke(favoriteProduct)
        }

        holder.imgProductFavorite.setOnClickListener {
            onRemoveFavoriteClickCallBack.invoke(favoriteProduct)
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
        return favoriteProductList?.size ?: 0
    }

    fun submitData(favoriteProducts: List<StoreProductItem?>?) {
        favoriteProductList = favoriteProducts
        notifyDataSetChanged()
    }
}