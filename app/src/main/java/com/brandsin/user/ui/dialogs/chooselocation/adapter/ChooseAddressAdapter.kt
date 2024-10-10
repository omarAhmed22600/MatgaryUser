package com.brandsin.user.ui.dialogs.chooselocation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.databinding.ItemAddressBinding
import com.brandsin.user.model.location.addresslist.AddressListItem

class ChooseAddressAdapter(
    private val onItemClickCallBack: (address: AddressListItem) -> Unit,
) : RecyclerView.Adapter<ChooseAddressAdapter.ChooseAddressViewHolder>() {

    private var addressesList: List<AddressListItem?>? = ArrayList()

    private lateinit var binding: ItemAddressBinding

    inner class ChooseAddressViewHolder(itemView: ItemAddressBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val address = itemView.address
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseAddressViewHolder {
        binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseAddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseAddressViewHolder, position: Int) {
        val address = addressesList!![position]
        // bind view
        bindData(holder, address!!)
    }

    private fun bindData(holder: ChooseAddressViewHolder, address: AddressListItem) {

        holder.address.text = address.streetName

        holder.itemView.rootView.setOnClickListener {
            onItemClickCallBack.invoke(address)
        }
    }

    override fun getItemCount(): Int {
        return addressesList?.size ?: 0
    }

    fun submitData(addresses: List<AddressListItem?>?) {
        addressesList = addresses
        notifyDataSetChanged()
    }
}