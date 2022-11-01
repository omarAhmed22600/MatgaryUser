package com.brandsin.user.ui.location.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawProfileAddressBinding
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.utils.SingleLiveEvent
import java.util.*

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressHolder>()
{
    var addressList: MutableList<AddressListItem> = ArrayList()
    var removeLiveData = SingleLiveEvent<AddressListItem>()
    var changeLiveData = SingleLiveEvent<AddressListItem>()
    var addressLiveData = SingleLiveEvent<AddressListItem>()
    var selectPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder
    {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawProfileAddressBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_profile_address, parent, false)
        return AddressHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val itemViewModel = ItemAddressViewModel(addressList[position])
        holder.binding.viewModel = itemViewModel

        if (position==selectPosition){
            holder.binding.btnDelete.visibility=View.INVISIBLE
            holder.binding.select.setImageResource(R.drawable.ic_size_check_box_24px)
        }else{
            holder.binding.btnDelete.visibility=View.VISIBLE
            holder.binding.select.setImageResource(R.drawable.ic_check_box_outline_blank_24px)
        }

        holder.binding.rawLayout.setOnClickListener {
            selectPosition = position
            notifyDataSetChanged()
//            holder.binding.select.setImageResource(R.drawable.ic_size_check_box_24px)
            addressLiveData.value = itemViewModel.item
        }

        holder.binding.btnDelete.setOnClickListener {
            removeLiveData.value = itemViewModel.item
        }

        holder.binding.btnChange.setOnClickListener {
            changeLiveData.value = itemViewModel.item
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun updateList(models: List<AddressListItem>) {
        addressList = models.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItem(item: AddressListItem) {
        addressList.remove(item)
        notifyDataSetChanged()
    }

    inner class AddressHolder(val binding: RawProfileAddressBinding) : RecyclerView.ViewHolder(binding.root)
}
