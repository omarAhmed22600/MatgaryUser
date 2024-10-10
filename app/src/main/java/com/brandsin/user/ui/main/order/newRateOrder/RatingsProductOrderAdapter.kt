package com.brandsin.user.ui.main.order.newRateOrder

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.databinding.ItemRateProductOrderBinding
import com.brandsin.user.model.order.details.OrderItem
import com.bumptech.glide.Glide

class RatingsProductOrderAdapter(
    private val btnAddPhotoClickCallBack: (ratingItem: OrderItem, position: Int) -> Unit
) : RecyclerView.Adapter<RatingsProductOrderAdapter.RatingsProductOrderViewHolder>() {

    private var orderItemsList: List<OrderItem?>? = ArrayList()

    private lateinit var binding: ItemRateProductOrderBinding

    inner class RatingsProductOrderViewHolder(itemView: ItemRateProductOrderBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val productName = itemView.productName
        val productRatingbar = itemView.productRatingbar
        val etAddressEvaluateProduct = itemView.etAddressEvaluateProduct
        val etWhatDidYouLikeInProduct = itemView.etWhatDidYouLikeInProduct
        val btnAddPhotoAndVideoProduct = itemView.btnAddPhotoAndVideoProduct
        val imgAddPhotoAndVideo = itemView.imgAddPhotoAndVideo
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RatingsProductOrderViewHolder {
        binding =
            ItemRateProductOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingsProductOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingsProductOrderViewHolder, position: Int) {
        val orderItem = orderItemsList!![position]
        // bind view
        bindData(holder, orderItem!!, position)
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(holder: RatingsProductOrderViewHolder, order: OrderItem, position: Int) {
        holder.productName.text = order.productName.toString()

        if (order.pickImage != null) {
            holder.btnAddPhotoAndVideoProduct.visibility = View.GONE
            holder.imgAddPhotoAndVideo.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(order.pickImage)
                .into(holder.imgAddPhotoAndVideo)
        } else if (order.video != null) {
            holder.btnAddPhotoAndVideoProduct.visibility = View.GONE
            holder.imgAddPhotoAndVideo.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(order.video)
                .into(holder.imgAddPhotoAndVideo)
        } else {
            holder.btnAddPhotoAndVideoProduct.visibility = View.VISIBLE
            holder.imgAddPhotoAndVideo.visibility = View.GONE
        }

        holder.etAddressEvaluateProduct.doAfterTextChanged {
            order.title = it.toString().trim()
        }

        holder.etWhatDidYouLikeInProduct.doAfterTextChanged {
            order.body = it.toString().trim()
        }

        holder.productRatingbar.setOnRatingBarChangeListener { _, rating, _ ->
            order.rate = rating.toInt()
        }

        holder.btnAddPhotoAndVideoProduct.setOnClickListener {
            btnAddPhotoClickCallBack.invoke(order, position)
        }

        holder.imgAddPhotoAndVideo.setOnClickListener {
            btnAddPhotoClickCallBack.invoke(order, position)
        }
    }

    fun getOrderList(): List<OrderItem?>? {
        return orderItemsList
    }

    override fun getItemCount(): Int {
        return orderItemsList?.size ?: 0
    }

    fun submitData(orderList: ArrayList<OrderItem?>?) {
        orderItemsList = orderList
        notifyDataSetChanged()
    }
}