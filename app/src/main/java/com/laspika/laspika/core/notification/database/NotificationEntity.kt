package com.laspika.laspika.core.notification.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("notification")
data class NotificationEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val message: String,
    val status: String,
    val date: String,
    val time: String
)