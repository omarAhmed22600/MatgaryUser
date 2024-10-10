package com.brandsin.user.utils.databinding

import android.graphics.Color
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.brandsin.user.R
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import de.hdodenhof.circleimageview.CircleImageView

class OtherViewsBinding {
    companion object {

        @JvmStatic
        @BindingAdapter("isLogin")
        fun isLogin(view: View, state: Boolean?)
        {
            when (state) {
                true -> {
                    view.visibility = View.GONE
                }
                else -> {
                    view.visibility = View.VISIBLE
                }
            }
        }

        /* Showing Shimmer Layouts while requesting API calls */
        @JvmStatic
        @BindingAdapter("isLoading")
        fun isLoading(view: View, value: Boolean?) {
            when (value) {
                false -> {
                    view.visibility = View.GONE
                }
                else -> {
                    view.visibility = View.VISIBLE
                }
            }
        }

        /* This Method for showing RecyclerView If passed Size Not equal zero */
        @JvmStatic
        @BindingAdapter("isFull")
        fun isFull(view: View, state: Boolean?) {
            when (state) {
                true -> {
                    view.visibility = View.VISIBLE
                }
                else -> {
                    view.visibility = View.GONE
                }
            }
        }

        /* This Method for showing Empty layouts If passed Size equal zero */
        @JvmStatic
        @BindingAdapter("isEmpty")
        fun isEmpty(view: View, state: Boolean?) {
            when (state) {
                true -> {
                    view.visibility = View.VISIBLE
                }
                else -> {
                    view.visibility = View.GONE
                }
            }
        }

        @JvmStatic
        @BindingAdapter("isOfferOrIsNew")
        fun isOfferOrIsNew(view: ImageView, flag: Int?) {
            when (flag) {
                0 -> {
                    view.visibility = View.GONE
                }
                else -> {
                    view.visibility = View.VISIBLE
                }
            }
        }

        @JvmStatic
        @BindingAdapter("isBusy")
        fun isBusy(view: TextView, flag: Int?) {
            when (flag) {
                0 -> {
                    view.visibility = View.GONE
                }
                else -> {
                    view.visibility = View.VISIBLE
                }
            }
        }

        @JvmStatic
        @BindingAdapter("isClosed")
        fun isClosed(view: TextView, flag: Int?) {
            when (flag) {
                0 -> {
                    view.visibility = View.GONE
                }
                else -> {
                    view.visibility = View.VISIBLE
                }
            }
        }

        @JvmStatic
        @BindingAdapter("storeImg")
        fun storeCategoryImg(view: ImageView, url: String?) {
            when {
                url.isNullOrEmpty() -> {
                    Glide.with(view.context).load(R.drawable.app_logo).error(R.drawable.app_logo).into(view)
                }
                else -> {
                    Glide.with(view.context).load(url).error(R.drawable.app_logo).into(view)
                }
            }
        }
        @JvmStatic
        @BindingAdapter("setDeliveryTime")
        fun setDeliveryTime(view: TextView, time: String?) {
            when (time) {
                "0000-00-00 00:00:00" -> {
                    view.visibility = View.GONE
                    view.text = ""
                }
                else -> {
                    view.visibility = View.VISIBLE
                    view.text = time
                }
            }
        }
        @JvmStatic
        @BindingAdapter("setStatus")
        fun setStatus(view: TextView, status: String?) {
            when (status) {
                "pending" -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_prepared_color))
                    view.text = view.context.getString(R.string.pending)
                }
                "accepted" -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_prepared_color))
                    view.text = view.context.getString(R.string.accepted)
                }

                "accepted_by_driver" -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_prepared_color))
                    view.text = view.context.getString(R.string.accepted)
                }

                "accepted_with_delivery" -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_prepared_color))
                    view.text = view.context.getString(R.string.accepted)
                }
                "shipping" -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_accepted_color))
                    view.text = view.context.getString(R.string.on_way)
                }
                "shipped" -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_accepted_color))
                    view.text = view.context.getString(R.string.on_way)
                }
                "completed" -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_completed_color))
                    view.text = view.context.getString(R.string.completed)
                }
                "cancelled" -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_rejected_color))
                    view.text = view.context.getString(R.string.cancelled_by_user)
                }
                "rejected_by_store" -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_rejected_color))
                    view.text = view.context.getString(R.string.cancelled_by_store)
                }
                else -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.order_rejected_color))
                    view.text = view.context.getString(R.string.cancelled)
                }
            }
        }
        @JvmStatic
        @BindingAdapter("storeImg")
        fun storeImg(view: ImageView, url: String?) {
            when {
                url.isNullOrEmpty() -> {
                    Glide.with(view.context).load(R.drawable.app_logo).error(R.drawable.app_logo).into(view)
                }
                else -> {
                    Glide.with(view.context).load(url).error(R.drawable.app_logo).into(view)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("offerImg")
        fun offerImg(view: ImageView, url: String?) {
            when {
                url.isNullOrEmpty() -> {
                    Glide.with(view.context).load(R.drawable.app_logo).error(R.drawable.app_logo).into(view)
                }
                else -> {
                    Glide.with(view.context).load(url).error(R.drawable.app_logo).into(view)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("paymentImg")
        fun paymentImg(view: ImageView, url: Int?) {
            when (url) {
                1 -> {
                    Glide.with(view.context).load(R.drawable.ic_visa).error(R.drawable.ic_visa).into(view)
                }
                2 -> {
                    Glide.with(view.context).load(R.drawable.ic_cash).error(R.drawable.ic_cash).into(view)
                }
                3 -> {
                    Glide.with(view.context).load(R.drawable.ic_qr_code).error(R.drawable.ic_qr_code).into(view)
                }
                4 -> {
                    Glide.with(view.context).load(R.drawable.ic_wallet).error(R.drawable.ic_wallet).into(view)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("categoryImg")
        fun categoryImg(view: ImageView, url: String?) {
            if (url != null && url.isNotEmpty()) {
                Glide.with(view.context).load(url).error(R.drawable.app_logo).into(view)
            } else {
                view.setImageResource(R.drawable.app_logo)
            }
        }


        @JvmStatic
        @BindingAdapter("isVisible")
        fun isVisible(view: View, value: Boolean?) {
            if (value == false) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }

        /* Which shows the address on MAP activity ... Urgent */
        @JvmStatic
        @BindingAdapter("showAddressLayout")
        fun showAddressLayout(view: ConstraintLayout, address: String?) {
            if (address == null) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }

        @JvmStatic
        @BindingAdapter("offersDesc")
        fun offersDesc(view: TextView, state: String?) {
            if (state == null) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
                view.text = state
            }
        }

        @JvmStatic
        @BindingAdapter("notifyRead")
        fun notifyRead(view: CardView, state: String?) {
            if (state != null) {
                view.setBackgroundColor(ContextCompat.getColor(MyApp.getInstance(), R.color.white))
            } else {
                when {
                    PrefMethods.getLanguage() == "en" -> {
                        view.setBackgroundResource(R.drawable.notify_unread_bg_ar)

                    }
                    else -> {
                        view.setBackgroundResource(R.drawable.notify_unread_bg_en)
                    }
                }
                view.radius = 8F
            }
        }

        @JvmStatic
        @BindingAdapter("notifyImg")
        fun notifyImg(view: CardView, state: String?) {
            when (state) {
                null -> {
                    view.setBackgroundColor(ContextCompat.getColor(MyApp.context, R.color.white))
                    view.cardElevation = 0f
                }
            }
        }

        @JvmStatic
        @BindingAdapter("headerBackg")
        fun headerBackg(view: ConstraintLayout, state: Boolean?) {
            if (PrefMethods.getLanguage() == "en") {
                view.setBackgroundResource(R.drawable.notify_unread_bg_ar)
            } else {
                view.setBackgroundResource(R.drawable.notify_unread_bg_en)
            }
        }



        @JvmStatic
        @BindingAdapter("showSubCategories")
        fun showSubCategories(view: View, state: Int?) {
            if (state == -1) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }

        @JvmStatic
        @BindingAdapter("visibleGone")
        fun visibleGone(view: View, state: Boolean?) {
            if (state == true) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

        @JvmStatic
        @BindingAdapter("isCollapse")
        fun isCollapse(view: ImageButton, state: Boolean?) {
            if (state == true) {
                Glide.with(view.context).load(R.drawable.ic_sow_prices).error(R.drawable.ic_sow_prices).into(view)
            } else {
                Glide.with(view.context).load(R.drawable.ic_arrow_up).error(R.drawable.ic_arrow_up).into(view)
            }
        }

        @JvmStatic
        @BindingAdapter("showLogoutBtn")
        fun showLogoutBtn(view: View, state: Boolean?)
        {
            if (PrefMethods.getUserData() != null)
            {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

        @JvmStatic
        @BindingAdapter("userImg")
        fun userImg(view: ImageView, isLogin: Boolean?) {
            if (isLogin == false) {
                Glide.with(view.context).load(R.drawable.user_default_img).error(R.drawable.user_default_img).into(view)
            } else {
                if (PrefMethods.getUserData()!!.picture!=null && PrefMethods.getUserData()!!.picture!!.isNotEmpty()) {
                    Glide.with(view.context).load(PrefMethods.getUserData()!!.picture).error(R.drawable.user_default_img).into(view)
                }else{
                    Glide.with(view.context).load(R.drawable.user_default_img).error(R.drawable.user_default_img).into(view)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("setBorder")
        fun isEmpty(view: MaterialCardView, show: Boolean?) {
            if (show == true) {
                view.strokeWidth = 1
            } else {
                view.strokeWidth = 0
            }
        }

        @JvmStatic
        @BindingAdapter("showPass")
        fun showPass(view : EditText, isShown: Boolean?)
        {
            if(isShown == true)
            {
                view.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else
            {
                view.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        @JvmStatic
        @BindingAdapter("showEyeIcon")
        fun showEyeIcon(view : ImageButton, isShown: Boolean?)
        {
            if(isShown == true)
            {
                view.setImageResource(R.drawable.ic_eye)
            }
            else
            {
                view.setImageResource(R.drawable.ic_visibility_off_24px)
            }
        }



        //put more here
    }
}