package com.brandsin.user.ui.main.homenew.moresub.brand

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemBrandBinding
import com.brandsin.user.model.order.homenew.BrandItem
import com.bumptech.glide.Glide

class BrandsAdapter(
    private val btnBrandItemClickCallBack: (brand: BrandItem) -> Unit,
) : RecyclerView.Adapter<BrandsAdapter.BrandsViewHolder>() {

    private var brandsList: List<BrandItem?>? = ArrayList()

    private lateinit var binding: ItemBrandBinding

    inner class BrandsViewHolder(val binding: ItemBrandBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandsViewHolder {
        binding = ItemBrandBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BrandsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandsViewHolder, position: Int) {
        val brand = brandsList!![position]

        Glide.with(holder.itemView.context)
            .load(brand?.image)
            .error(R.drawable.app_logo)
            .into(binding.imgBrand)

        holder.itemView.setOnClickListener {
            btnBrandItemClickCallBack.invoke(brand!!)
        }
    }

    override fun getItemCount(): Int {
        return brandsList?.size ?: 0
    }

    fun submitData(models: List<BrandItem?>?) {
        brandsList = models
        notifyDataSetChanged()
    }
}
