package com.laspika.laspika.presentation.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.laspika.laspika.core.data.ReportData
import com.laspika.laspika.core.notification.ApiUtils
import com.laspika.laspika.core.notification.NotificationData
import com.laspika.laspika.core.notification.NotificationResponse
import com.laspika.laspika.core.notification.PushNotification
import com.laspika.laspika.core.utils.Constants.REPORT_COLLECTION
import com.laspika.laspika.core.utils.Constants.USER_COLLECTION
import com.laspika.laspika.core.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportDetailViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ViewModel() {
    private val _cancelState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val cancelState = _cancelState.asLiveData()

    fun cancelReport(reportData: ReportData) {
        _cancelState.value = UiState.Loading(true)
        firestore.runBatch {
            firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(REPORT_COLLECTION)
                .document(reportData.id).set(reportData)
            firestore.collection(REPORT_COLLECTION).document(reportData.id).set(reportData)
        }.addOnSuccessListener {
            sendNotification(reportData.status!!)
            _cancelState.value = UiState.Loading(false)
            _cancelState.value = UiState.Success("Berhasil batalkan data")
        }.addOnFailureListener {
            it.printStackTrace()
            _cancelState.value = UiState.Loading(false)
            _cancelState.value = UiState.Success("Terjadi kesalahan pada server")
        }
    }

    private fun sendNotification(status: String) {
        val notificationData = NotificationData(
            "Laporan kamu telah dibatalkan!",
            "Laporan yang kamu buat sudah dibatalkan. \nYuk Buat laporan pelanggaran kembali...",
            status,
            auth.uid
        )
        try {
            ApiUtils.getClients().sendNotification(PushNotification(notificationData))
                .enqueue(object : Callback<NotificationResponse> {
                    override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                        if (!response.isSuccessful) {
                            Log.e("SendNotif", response.toString())
                        }
                    }

                    override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TindakanViewModel", e.message ?: "Error sending notification")
        }
    }
}