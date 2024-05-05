package com.laspika.laspika.presentation.tindakan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.laspika.laspika.core.data.ReportData
import com.laspika.laspika.core.utils.Constants.REPORT_COLLECTION
import com.laspika.laspika.core.utils.Constants.USER_COLLECTION
import com.laspika.laspika.core.utils.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class TindakanViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
) : ViewModel() {
    private val _sendReportState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val sendReportState = _sendReportState.asLiveData()

    fun reportPelanggaran(reportData: ReportData, dokumen: File) {
        _sendReportState.value = UiState.Loading(true)
        var document = ""
        viewModelScope.launch {
            try {
                val docByteArray = dokumen.readBytes()
                async {
                    launch {
                        val imageStorage =
                            storage.reference.child("report/${reportData.jenisDokumen}/${reportData.id}")
                        val result = imageStorage.putBytes(docByteArray).await()
                        val downloadUrl = result.storage.downloadUrl.await().toString()
                        document = downloadUrl
                    }
                }.await()
            } catch (e: Exception) {
                Log.e("ProfilViewModel", e.message ?: "Terjadi Kesalahan")
                _sendReportState.value =
                    UiState.Error("Terjadi kesalahan saat upload dokumen bukti ke server")
            }

            val reportDataUpdated = reportData.copy(
                dokumenUrl = document,
                uid = auth.currentUser!!.uid
            )
            sendReport(reportDataUpdated)
        }
    }

    private fun sendReport(reportData: ReportData) {
        firestore.runBatch {
            firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(
                REPORT_COLLECTION
            ).document(reportData.id).set(reportData)
            firestore.collection(REPORT_COLLECTION).document(reportData.id).set(reportData)
        }.addOnSuccessListener {
            _sendReportState.value = UiState.Success("Laporan berhasil dibuat")
        }.addOnFailureListener {
            it.printStackTrace()
            _sendReportState.value = UiState.Error("Terjadi Kesalahan")
        }
    }
}