package com.brandsin.user.ui.main.homenew.moresub

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemGiftBinding
import com.brandsin.user.model.order.homenew.GiftItem
import com.bumptech.glide.Glide

class GiftsAdapter(
    private val btnGiftItemClickCallBack: (offerItem: GiftItem) -> Unit,
) : RecyclerView.Adapter<GiftsAdapter.GiftsViewHolder>() {

    private var giftsList: List<GiftItem?>? = ArrayList()

    private lateinit var binding: ItemGiftBinding

    inner class GiftsViewHolder(val binding: ItemGiftBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftsViewHolder {
        binding = ItemGiftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GiftsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GiftsViewHolder, position: Int) {
        val gift = giftsList!![position]

        Glide.with(holder.itemView.context)
            .load(gift?.image)
            .error(R.drawable.app_logo)
            .into(binding.imgGift)

        binding.giftPrice.text =
            "${gift?.price}  ${holder.itemView.context.getString(R.string.currency)}"

        binding.root.setOnClickListener {
            if (gift != null) {
                btnGiftItemClickCallBack.invoke(gift)
            }
        }
    }

    override fun getItemCount(): Int {
        return giftsList?.size ?: 0
    }

    fun submitData(models: ArrayList<GiftItem?>?) {
        giftsList = models
        notifyDataSetChanged()
    }
}
