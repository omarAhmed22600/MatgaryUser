package com.brandsin.user.ui.main.order.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeCartBinding
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.utils.SingleLiveEvent
import com.brandsin.user.utils.Utils

class CartsAdapter : RecyclerView.Adapter<CartsAdapter.CartsHolder>() {

    private var cartsList: ArrayList<CartItem> = ArrayList()

    var removeCartLiveData = SingleLiveEvent<CartItem>()
    var increaseLiveData = SingleLiveEvent<CartItem>()
    var decreaseLiveData = SingleLiveEvent<CartItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartsHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawHomeCartBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_cart, parent, false)
        return CartsHolder(binding)
    }

    // https://dev.brandsin.net/api/hajaty/search?search_for=store&keyword=alm

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartsHolder, position: Int) {
        val itemViewModel = ItemCartViewModel(cartsList[position])
        holder.binding.viewModel = itemViewModel

        if (itemViewModel.item.isOffer == true) {
            holder.binding.linCount.visibility = View.INVISIBLE
        } else {
            holder.binding.linCount.visibility = View.VISIBLE
        }

        holder.binding.ibCancel.setOnClickListener {
            removeCartLiveData.value = itemViewModel.item
        }

        itemViewModel.increaseCountLiveData.observeForever {
            increaseLiveData.value = it
        }

        itemViewModel.decreaseCountLiveData.observeForever {
            decreaseLiveData.value = it
        }

        itemViewModel.removeItemLiveData.observeForever {
            removeCartLiveData.value = it
        }

        if (itemViewModel.item.type == "variable") {
            holder.binding.txtSkuName.text =
                holder.itemView.context.getString(R.string.size) + " " + itemViewModel.item.skuName.toString()
        } else {
            holder.binding.txtSkuName.visibility = View.GONE
        }

        holder.binding.tvDescription.text =
            Utils.html2text(itemViewModel.item.productDescription.toString())
    }

    fun getItem(pos: Int): CartItem {
        return cartsList[pos]
    }

    override fun getItemCount(): Int {
        return cartsList.size
    }

    fun updateList(models: ArrayList<CartItem>) {
        cartsList = models
        notifyDataSetChanged()
    }

    inner class CartsHolder(val binding: RawHomeCartBinding) : RecyclerView.ViewHolder(binding.root)
}
