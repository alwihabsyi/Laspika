package com.laspika.laspika.presentation.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.laspika.laspika.R
import com.laspika.laspika.core.notification.database.NotificationEntity
import com.laspika.laspika.core.utils.Constants
import com.laspika.laspika.databinding.ItemNotificationBinding

class NotificationAdapter: Adapter<NotificationAdapter.NotificationViewHolder>() {
    inner class NotificationViewHolder(private val binding: ItemNotificationBinding): ViewHolder(binding.root) {
        fun bind(notification: NotificationEntity) {
            binding.apply {
                notificationTitle.text = notification.title
                notificationBody.text = notification.message
                notificationDate.text = itemView.context.getString(R.string.notification_date, notification.date, notification.time)

                notificationIndicator.setImageResource(
                    if (notification.status == Constants.APPROVED) R.drawable.ic_notification_blue
                    else R.drawable.ic_notification_red
                )
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<NotificationEntity>() {
        override fun areItemsTheSame(
            oldItem: NotificationEntity,
            newItem: NotificationEntity,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: NotificationEntity,
            newItem: NotificationEntity,
        ): Boolean = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }
}