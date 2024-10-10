package com.brandsin.user.ui.main.order.myorders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeMyorderBinding
import com.brandsin.user.model.order.myorders.MyOrderItem
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.SingleLiveEvent

class MyOrdersAdapter : RecyclerView.Adapter<MyOrdersAdapter.ReOrderHolder>() {
    private var reorderList: ArrayList<MyOrderItem> = ArrayList()

    var orderItemLiveData = SingleLiveEvent<MyOrderItem>()
    var detailsLiveData = SingleLiveEvent<MyOrderItem>()
    var assessmentLiveData = SingleLiveEvent<MyOrderItem>()
    val statusLiveData = SingleLiveEvent<MyOrderItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReOrderHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RawHomeMyorderBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_myorder, parent, false)
        return ReOrderHolder(binding)
    }

    override fun onBindViewHolder(holder: ReOrderHolder, position: Int) {
        val itemViewModel = ItemMyOrderViewModel(reorderList[position])
        holder.binding.viewModel = itemViewModel

        /*
        *  PENDING  >> Details /  Cancel
        *  ACCEPTED  >> Details /  Status
        *  COMPLETED  >> Details / ( Rate / Re-Order)
        *  SHIPPING  >> Details /  Status
        *  CANCELLED  >> Details /  Re-Order
        * */

        when {
            itemViewModel.item.status!!.contains("pending") -> {
                holder.binding.statusLayout.visibility = View.VISIBLE
                holder.binding.assessmentLayout.visibility = View.GONE
                holder.binding.btnDetails.text =
                    MyApp.getInstance().getString(R.string.order_Details)
                holder.binding.btnStatus.text = MyApp.getInstance().getString(R.string.cancel)
            }

            itemViewModel.item.status!!.contains("accepted") -> {
                holder.binding.btnDetails.text =
                    MyApp.getInstance().getString(R.string.order_Details)
                holder.binding.btnStatus.text = MyApp.getInstance().getString(R.string.order_status)
                holder.binding.statusLayout.visibility = View.GONE
                holder.binding.btnSeperator.visibility = View.GONE
                holder.binding.assessmentLayout.visibility = View.GONE
            }

            itemViewModel.item.status!!.contains("completed") -> {
                holder.binding.btnDetails.text =
                    MyApp.getInstance().getString(R.string.order_Details)
                // check here if order is rated or not > is rated > Re-order   else >> Rate
                holder.binding.statusLayout.visibility = View.GONE
                holder.binding.btnSeperator.visibility = View.VISIBLE
                holder.binding.assessmentLayout.visibility = View.VISIBLE

                holder.binding.assessmentLayout.setOnClickListener {
                    assessmentLiveData.value = getItem(position)
                }

                /*if (itemViewModel.item.isRated != null && itemViewModel.item.isRated!!) {
                    // holder.binding.btnStatus.text = MyApp.getInstance().getString(R.string.reorder)
                    holder.binding.statusLayout.visibility = View.GONE
                    holder.binding.assessmentLayout.visibility = View.VISIBLE

                    holder.binding.assessmentLayout.setOnClickListener {
                        assessmentLiveData.value = getItem(position)
                    }
                }

                else {
                    holder.binding.btnStatus.text = MyApp.getInstance().getString(R.string.rate_order)
                }*/
            }

            itemViewModel.item.status!!.contains("shipping") -> {
                holder.binding.btnDetails.text =
                    MyApp.getInstance().getString(R.string.order_Details)
                holder.binding.btnStatus.text = MyApp.getInstance().getString(R.string.order_status)
                holder.binding.statusLayout.visibility = View.GONE
                holder.binding.btnSeperator.visibility = View.GONE
                holder.binding.assessmentLayout.visibility = View.GONE
            }

            itemViewModel.item.status!!.contains("shipped") -> {
                holder.binding.btnDetails.text =
                    MyApp.getInstance().getString(R.string.order_Details)
                holder.binding.btnStatus.text = MyApp.getInstance().getString(R.string.order_status)
                holder.binding.statusLayout.visibility = View.GONE
                holder.binding.btnSeperator.visibility = View.GONE
                holder.binding.assessmentLayout.visibility = View.GONE
            }

            itemViewModel.item.status!!.contains("canceled") || itemViewModel.item.status!!.contains(
                "rejected"
            ) -> {
                holder.binding.btnDetails.text =
                    MyApp.getInstance().getString(R.string.order_Details)
                holder.binding.statusLayout.visibility = View.GONE
                holder.binding.btnSeperator.visibility = View.GONE
                holder.binding.assessmentLayout.visibility = View.GONE
            }
        }

        holder.binding.detailsLayout.setOnClickListener {
            detailsLiveData.value = getItem(position)
        }

        holder.binding.statusLayout.setOnClickListener {
            statusLiveData.value = getItem(position)
            // detailsLiveData.value = getItem(position)
        }

        holder.binding.rawLayout.setOnClickListener {
            // orderItemLiveData.value = getItem(position)
            detailsLiveData.value = getItem(position)
        }
    }

    private fun getItem(pos: Int): MyOrderItem {
        return reorderList[pos]
    }

    override fun getItemCount(): Int {
        return reorderList.size
    }

    fun updateList(models: ArrayList<MyOrderItem>) {
        reorderList = models
        notifyDataSetChanged()
    }

    inner class ReOrderHolder(val binding: RawHomeMyorderBinding) :
        RecyclerView.ViewHolder(binding.root)
}
