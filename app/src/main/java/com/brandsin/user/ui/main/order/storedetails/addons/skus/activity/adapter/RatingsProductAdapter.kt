package com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.adapter

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemRatingsStoreBinding
import com.brandsin.user.model.constants.Params
import com.brandsin.user.ui.main.order.reviewStore.model.RatingItem
import com.brandsin.user.utils.PrefMethods
import com.bumptech.glide.Glide

class RatingsProductAdapter(
    private val onItemClickCallBack: (ratingItem: RatingItem) -> Unit,
) : RecyclerView.Adapter<RatingsProductAdapter.RatingsProductViewHolder>() {

    private var ratingsStoreList: List<RatingItem?>? = ArrayList()

    private lateinit var binding: ItemRatingsStoreBinding

    inner class RatingsProductViewHolder(itemView: ItemRatingsStoreBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val imgUser = itemView.imgUser
        val userName = itemView.userName
        val time = itemView.time
        val ratingBar = itemView.ratingBar
        val title = itemView.title
        val content = itemView.content

        // val statusOrder = itemView.statusOrder
        val imagesRating = itemView.imagesRating
        val icVideo = itemView.icVideo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingsProductViewHolder {
        binding =
            ItemRatingsStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingsProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingsProductViewHolder, position: Int) {
        val ratingsStoreItem = ratingsStoreList!![position]
        // bind view
        bindData(holder, ratingsStoreItem!!)
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(holder: RatingsProductViewHolder, ratingsStoreItem: RatingItem) {
        holder.imagesRating.visibility = View.VISIBLE

        // holder.userName.text = ratingsStoreItem.author?.name.toString() + " " + ratingsStoreItem.author?.lastName.toString()

        if (PrefMethods.getLanguage(holder.itemView.context) == "ar") {
            binding.userName.gravity = Gravity.END
            binding.time.gravity = Gravity.START
        }

//        holder.userName.text = "${ratingsStoreItem.author?.name.toString() + " " + ratingsStoreItem.author?.lastName.toString()}"
        holder.time.text = ratingsStoreItem.createdAt.toString()

        holder.title.text = ratingsStoreItem.title
        holder.content.text = ratingsStoreItem.body

        holder.ratingBar.rating = ratingsStoreItem.rating?.toFloat() ?: 0.0f

        Glide.with(holder.itemView.context)
            // .load("${Params.BASE_URL.removeSuffix("/")}${ratingsStoreItem.author?.pictureThumb}")
            .load(R.drawable.app_logo)
            .error(R.drawable.app_logo)
            .centerCrop()
            .into(holder.imgUser)

        if (ratingsStoreItem.video?.isNotEmpty() == true) {
            holder.icVideo.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(ratingsStoreItem.video)
                .error(R.drawable.app_logo)
                .into(holder.imagesRating)
        } else if (ratingsStoreItem.image?.isNotEmpty() == true) {
            Glide.with(holder.itemView.context)
                .load(ratingsStoreItem.image)
                .error(R.drawable.app_logo)
                .into(holder.imagesRating)
        } else {
            holder.imagesRating.visibility = View.GONE
            holder.icVideo.visibility = View.GONE
        }

        holder.imagesRating.setOnClickListener {
            onItemClickCallBack.invoke(ratingsStoreItem)
        }

        holder.icVideo.setOnClickListener {
            onItemClickCallBack.invoke(ratingsStoreItem)
        }
    }

    override fun getItemCount(): Int {
        return ratingsStoreList?.size ?: 0
    }

    fun submitData(favoriteProducts: List<RatingItem?>?) {
        ratingsStoreList = favoriteProducts
        notifyDataSetChanged()
    }
}