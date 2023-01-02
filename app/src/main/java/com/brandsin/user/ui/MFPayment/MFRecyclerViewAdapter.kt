package com.brandsin.user.ui.MFPayment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.utils.PrefMethods
import com.bumptech.glide.Glide
import com.myfatoorah.sdk.entity.initiatepayment.PaymentMethod

class MFRecyclerViewAdapter(
    private val mValues: java.util.ArrayList<PaymentMethod>,
    private val mListener: OnListFragmentInteractionListener?) :
    RecyclerView.Adapter<MFRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var listSelected = ArrayList<Boolean>()

    init {
        for (i in 0..mValues.size) {
            listSelected.add(false)
        }

        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as PaymentMethod
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(v.id, item)

            changeSelected(v.id)
        }
    }

    fun changeSelected(position: Int) {
        for (i in 0..mValues.size)
            listSelected[i] = i == position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        Glide.with(holder.mView.context)
            .load(item.imageUrl)
            .into(holder.mImage)

        if (listSelected[position]){
            holder.mcbSelected.visibility = View.VISIBLE
            holder.mcbSelected.setImageResource(R.drawable.ic_radio_button_checked)
            //holder.mcbSelected.isChecked=true
        }
        else{
            holder.mcbSelected.visibility = View.VISIBLE
            holder.mcbSelected.setImageResource(R.drawable.ic_radio_button_unchecked)
            //holder.mcbSelected.isChecked=false
        }

        holder.mpercentage.text=item.serviceCharge.toString()

        if (PrefMethods.getLanguage().equals("en")){
            holder.mtitle.text=item.paymentMethodEn.toString()
        }else{
            holder.mtitle.text=item.paymentMethodAr.toString()
        }


        with(holder.mView) {
            tag = item
            id = position
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mImage: ImageView = mView.findViewById(R.id.image)
        val mcbSelected: ImageView = mView.findViewById(R.id.cbSelected)
        val mtitle: TextView =mView.findViewById(R.id.paymentMethod_title)
        val mpercentage: TextView =mView.findViewById(R.id.paymentMethod_percentage)

    }
}