package com.brandsin.user.ui.menu.offers

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeOffersBinding
import com.brandsin.user.model.menu.offers.OffersItemDetails
import com.brandsin.user.utils.SingleLiveEvent

class OffersAdapter : RecyclerView.Adapter<OffersAdapter.OffersHolder>() {
    private var offersList: ArrayList<OffersItemDetails> = ArrayList()

    var offersLiveData = SingleLiveEvent<OffersItemDetails>()

    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawHomeOffersBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_offers, parent, false)
        return OffersHolder(binding)
    }

    override fun onBindViewHolder(holder: OffersHolder, position: Int) {
        val itemViewModel = ItemOffersViewModel(offersList[position])
        holder.binding.viewModel = itemViewModel

        holder.binding.oldPrice.paintFlags =
            holder.binding.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        holder.binding.rawLayout.setOnClickListener {
            offersLiveData.value = itemViewModel.item
        }
    }

    override fun getItemCount(): Int {
        return offersList.size
    }

    fun updateList(models: ArrayList<OffersItemDetails>) {
        offersList = models
        notifyDataSetChanged()
    }

    inner class OffersHolder(val binding: RawHomeOffersBinding) :
        RecyclerView.ViewHolder(binding.root)
}
