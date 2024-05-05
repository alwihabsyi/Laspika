package com.laspika.laspika.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.laspika.laspika.R
import com.laspika.laspika.core.notification.database.NotificationDatabase
import com.laspika.laspika.core.notification.database.NotificationEntity
import com.laspika.laspika.core.utils.Constants.APPROVED
import com.laspika.laspika.core.utils.getDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Random

class MessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val id = message.data["uid"]
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            if (id == it) {
                postNotification(message)
            }
        }
    }

    private fun postNotification(message: RemoteMessage) {
        CoroutineScope(Dispatchers.IO).launch {
            val title = message.data["title"]
            val msg = message.data["body"]
            val status = message.data["status"]

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notifikasi = NotificationEntity(
                title = title ?: " ",
                message = msg ?: " ",
                status = status ?: "pending",
                date = getDate()["date"] ?: " ",
                time = getDate()["time"] ?: " "
            )
            NotificationDatabase.getInstance(applicationContext).notificationDao.insert(notifikasi)
            val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(
                    if (status == APPROVED) R.drawable.ic_notification_blue
                    else R.drawable.ic_notification_red
                )
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                builder.setChannelId(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(channel)
            }

            val notification = builder.build()
            val id = Random().nextInt()
            notificationManager.notify(id, notification)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseMessaging.getInstance().subscribeToTopic("notification")
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notification-channel"
        const val CHANNEL_NAME = "channel-imunisasi"
    }
}