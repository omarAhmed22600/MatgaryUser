package com.brandsin.user.ui.main.homenew.moresub

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemProductBinding
import com.brandsin.user.model.menu.offers.OffersItemDetails
import com.brandsin.user.utils.Utils
import com.bumptech.glide.Glide

class OfferAdapter(
    private val btnOfferItemClickCallBack: (offerItem: OffersItemDetails) -> Unit,
) : RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {

    private var offerItemList: List<OffersItemDetails?>? = ArrayList()

    private lateinit var binding: ItemProductBinding

    inner class OfferViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfferViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val offerItem = offerItemList!![position]

        Glide.with(holder.itemView.context)
            .load(offerItem?.image)
            .error(R.drawable.app_logo)
            .into(binding.imgProduct)

        Glide.with(holder.itemView.context)
            .load(offerItem?.store?.thumbnail)
            .error(R.drawable.app_logo)
            .into(binding.imgStore)

        binding.productName.text = offerItem?.name
        binding.productDesc.text = Utils.html2text(offerItem?.description.toString())

        binding.root.setOnClickListener {
            if (offerItem != null) {
                btnOfferItemClickCallBack.invoke(offerItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return offerItemList?.size ?: 0
    }

    fun submitData(models: ArrayList<OffersItemDetails?>?) {
        offerItemList = models
        notifyDataSetChanged()
    }
}
