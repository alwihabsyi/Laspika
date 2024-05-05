package com.laspika.laspika.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.laspika.laspika.core.notification.database.NotificationDao
import com.laspika.laspika.core.notification.database.NotificationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationDao: NotificationDao
): ViewModel() {
    private val _notificationState = MutableStateFlow<List<NotificationEntity>>(listOf())
    val notificationState = _notificationState.asLiveData()

    fun getAllNotifications() {
        viewModelScope.launch {
            notificationDao.getAllNotifications().collect {
                _notificationState.value = it
            }
        }
    }
}