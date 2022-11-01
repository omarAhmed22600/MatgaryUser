package com.brandsin.user.ui.menu.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.RawHomeNotificationBinding
import com.brandsin.user.model.menu.notifications.NotificationItem
import com.brandsin.user.utils.SingleLiveEvent
import java.util.*

class NotificationsAdapter : RecyclerView.Adapter<NotificationsAdapter.NotificationHolder>()
{
    var notificationsList: ArrayList<NotificationItem> = ArrayList()
    var notificationsLiveData = SingleLiveEvent<NotificationItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder
    {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: RawHomeNotificationBinding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_home_notification, parent, false)
        return NotificationHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        val itemViewModel = ItemNotificationViewModel(notificationsList[position])
        holder.binding.viewModel = itemViewModel

        holder.binding.rawLayout.setOnClickListener {
            notificationsLiveData.value = itemViewModel.itemNotification
        }
    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }

    fun updateList(models: ArrayList<NotificationItem>) {
        notificationsList = models
        notifyDataSetChanged()
    }

    inner class NotificationHolder(val binding: RawHomeNotificationBinding) : RecyclerView.ViewHolder(binding.root)
}
