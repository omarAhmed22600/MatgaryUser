package com.brandsin.user.ui.main.order.reviewStore.adapter

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

class ImagesRatingsStoreAdapter(
    private val onItemClickCallBack: (ratingItem: RatingItem) -> Unit,
) : RecyclerView.Adapter<ImagesRatingsStoreAdapter.RatingsStoreViewHolder>() {

    private var ratingsStoreList: List<RatingItem?>? = ArrayList()

    private lateinit var binding: ItemRatingsStoreBinding

    inner class RatingsStoreViewHolder(itemView: ItemRatingsStoreBinding) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingsStoreViewHolder {
        binding =
            ItemRatingsStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingsStoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingsStoreViewHolder, position: Int) {
        val ratingsStoreItem = ratingsStoreList!![position]
        // bind view
        bindData(holder, ratingsStoreItem!!)
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(holder: RatingsStoreViewHolder, ratingItem: RatingItem) {
        holder.imagesRating.visibility = View.VISIBLE

        if (PrefMethods.getLanguage(holder.itemView.context) == "ar") {
            binding.userName.gravity = Gravity.END
            binding.time.gravity = Gravity.START
        }

        holder.userName.text =
            "${ratingItem.author?.name.toString() + " " + ratingItem.author?.lastName.toString()}"
        holder.time.text = ratingItem.createdAt.toString()

        holder.title.text = ratingItem.title
        holder.content.text = ratingItem.body

        holder.ratingBar.rating = ratingItem.rating?.toFloat() ?: 0.0f

        Glide.with(holder.itemView.context)
            .load("${Params.BASE_URL}${ratingItem.author?.pictureThumb}")
            .error(R.drawable.app_logo)
            .centerCrop()
            .into(holder.imgUser)

        if (ratingItem.video?.isNotEmpty() == true) {
            holder.icVideo.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(ratingItem.video)
                .error(R.drawable.app_logo)
                .into(holder.imagesRating)
        } else if (ratingItem.image?.isNotEmpty() == true) {
            Glide.with(holder.itemView.context)
                .load(ratingItem.image)
                .error(R.drawable.app_logo)
                .into(holder.imagesRating)
        }

        holder.imagesRating.setOnClickListener {
            onItemClickCallBack.invoke(ratingItem)
        }

        holder.icVideo.setOnClickListener {
            onItemClickCallBack.invoke(ratingItem)
        }
    }

    override fun getItemCount(): Int {
        return ratingsStoreList?.size ?: 0
    }

    fun submitData(ratingsList: List<RatingItem?>?) {
        ratingsStoreList = ratingsList
        notifyDataSetChanged()
    }
}