package com.laspika.laspika.core.notification

import com.google.gson.annotations.SerializedName
import com.laspika.laspika.core.notification.NotificationConstants.TO

data class NotificationData(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("body")
    val body: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("uid")
    val uid: String? = null,
)

data class PushNotification(
    @SerializedName("data")
    val notificationData: NotificationData,
    @SerializedName("to")
    val to: String = TO
)