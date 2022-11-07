package com.brandsin.user.ui.main.order.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeCartBinding
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.utils.SingleLiveEvent
import org.jsoup.Jsoup
import java.util.*
import kotlin.collections.ArrayList

class CartsAdapter : RecyclerView.Adapter<CartsAdapter.CartsHolder>()
{
    var cartsList: ArrayList<CartItem> = ArrayList()
    var removeCartLiveData = SingleLiveEvent<CartItem>()
    var increaseLiveData = SingleLiveEvent<CartItem>()
    var decreaseLiveData = SingleLiveEvent<CartItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartsHolder
    {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawHomeCartBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_cart, parent, false)
        return CartsHolder(binding)
    }

    override fun onBindViewHolder(holder: CartsHolder, position: Int)
    {
        val itemViewModel = ItemCartViewModel(cartsList[position])
        holder.binding.viewModel = itemViewModel

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

        holder.binding.tvDescription.text=html2text(itemViewModel.item.productDescription.toString())
    }

    fun getItem(pos:Int):CartItem{
        return cartsList[pos]
    }
    fun html2text(html:String):String {
        try {
            return Jsoup.parse(html).text();
        } catch (ignored: Exception) {
            return "";
        }
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
