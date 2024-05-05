package com.laspika.laspika.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.laspika.laspika.core.data.UserData
import com.laspika.laspika.core.notification.database.NotificationDao
import com.laspika.laspika.core.utils.Constants
import com.laspika.laspika.core.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val notificationDao: NotificationDao
): ViewModel() {
    private val _userState = MutableStateFlow<UiState<UserData>>(UiState.Loading(false))
    val userState = _userState.asLiveData()

    init {
        subscribeToTopic()
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("notification")
    }

    fun getUserData() {
        _userState.value = UiState.Loading(true)
        firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!).addSnapshotListener { value, error ->
            if (error != null) {
                error.printStackTrace()
                _userState.value = UiState.Loading(false)
                _userState.value = UiState.Error("Terjadi kesalahan saat mengambil data user")
            }

            value?.toObject(UserData::class.java)?.let {
                _userState.value = UiState.Loading(false)
                _userState.value = UiState.Success(it)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            notificationDao.deleteAllNotification()
            auth.signOut()
        }
    }
}