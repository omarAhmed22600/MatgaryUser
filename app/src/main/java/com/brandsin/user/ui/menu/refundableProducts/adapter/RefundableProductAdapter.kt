package com.brandsin.user.ui.menu.refundableProducts.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemRefundableProductBinding
import com.brandsin.user.model.refundableProduct.RefundableProductItem
import com.bumptech.glide.Glide

class RefundableProductAdapter(
    private val onItemClickCallBack: (refundableProduct: RefundableProductItem) -> Unit,
) : RecyclerView.Adapter<RefundableProductAdapter.RefundableProductViewHolder>() {

    private var refundableProductItemList: List<RefundableProductItem?>? = ArrayList()

    private lateinit var binding: ItemRefundableProductBinding

    inner class RefundableProductViewHolder(itemView: ItemRefundableProductBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val imgProduct = itemView.imgProduct
        val productName = itemView.productName
        val orderNumber = itemView.orderNumber
        val productPrice = itemView.productPrice
        val orderDate = itemView.orderDate
        val statusOrder = itemView.statusOrder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefundableProductViewHolder {
        binding =
            ItemRefundableProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RefundableProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RefundableProductViewHolder, position: Int) {
        val favoriteProduct = refundableProductItemList!![position]

        // bind view
        bindData(holder, favoriteProduct!!)
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(
        holder: RefundableProductViewHolder,
        refundableProduct: RefundableProductItem
    ) {

        holder.productName.text = refundableProduct.name
        holder.productPrice.text =
            refundableProduct.skus?.get(0)?.price.toString() + " " +
                    holder.itemView.context.getString(R.string.currency)

        holder.orderNumber.text =
            holder.itemView.context.getString(R.string.order_number) + ": " + refundableProduct.orderNumber.toString()
        holder.orderDate.text =
            holder.itemView.context.getString(R.string.date) + ": " + refundableProduct.createdAt.toString()

        Glide.with(holder.itemView.context)
            .load(refundableProduct.image)
            .into(holder.imgProduct)

        checkRefundableStatus(holder, refundableProduct)
    }

    private fun checkRefundableStatus(
        holder: RefundableProductViewHolder,
        refundableProduct: RefundableProductItem
    ) {
        when {
            refundableProduct.refundableStatus?.contains("pending") == true ||
                    refundableProduct.refundableStatus?.contains("waiting") == true -> {
                holder.statusOrder.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, R.color.pending
                    )
                )
                holder.statusOrder.text = holder.itemView.context.getString(R.string.pending)
                holder.statusOrder.isClickable = false
            }

            refundableProduct.refundableStatus?.contains("rejected") == true -> {
                holder.statusOrder.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.order_rejected_color)
                )
                holder.statusOrder.text = holder.itemView.context.getString(R.string.rejected)
                holder.statusOrder.isClickable = false
            }

            refundableProduct.refundableStatus?.contains("approval") == true -> {
                holder.statusOrder.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, R.color.completed
                    )
                )
                holder.statusOrder.text = holder.itemView.context.getString(R.string.is_accepted)
                holder.statusOrder.isClickable = false
            }

            else -> {
                holder.statusOrder.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, R.color.color_primary
                    )
                )
                holder.statusOrder.text = holder.itemView.context.getString(R.string.return_product)
                holder.statusOrder.isClickable = true

                holder.statusOrder.setOnClickListener {
                    onItemClickCallBack.invoke(refundableProduct)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return refundableProductItemList?.size ?: 0
    }

    fun submitData(refundableProduct: List<RefundableProductItem?>?) {
        refundableProductItemList = refundableProduct
        notifyDataSetChanged()
    }
}