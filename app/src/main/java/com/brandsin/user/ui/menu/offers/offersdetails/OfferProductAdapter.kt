package com.brandsin.user.ui.menu.offers.offersdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawOfferProductItemBinding
import com.brandsin.user.model.menu.offers.ProductsItem
import com.brandsin.user.utils.SingleLiveEvent

class OfferProductAdapter : RecyclerView.Adapter<OfferProductAdapter.OfferProductHolder>() {

    private var offersList: ArrayList<ProductsItem>? = ArrayList()
    private var offersLiveData = SingleLiveEvent<ProductsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferProductHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawOfferProductItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_offer_product_item, parent, false)
        return OfferProductHolder(binding)
    }

    override fun onBindViewHolder(holder: OfferProductHolder, position: Int) {
        val itemViewModel = ItemOfferProductViewModel(offersList!![position])
        holder.binding.viewModel = itemViewModel

        holder.binding.rawLayout.setOnClickListener {
            offersLiveData.value = itemViewModel.item
        }
    }

    override fun getItemCount(): Int {
        return offersList?.size ?: 0
    }

    fun updateList(models: ArrayList<ProductsItem>?) {
        offersList = models
        notifyDataSetChanged()
    }

    inner class OfferProductHolder(val binding: RawOfferProductItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
