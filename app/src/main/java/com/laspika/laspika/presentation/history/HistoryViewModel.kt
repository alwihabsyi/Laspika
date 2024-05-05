package com.laspika.laspika.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.laspika.laspika.core.data.ReportData
import com.laspika.laspika.core.utils.Constants.REPORT_COLLECTION
import com.laspika.laspika.core.utils.Constants.USER_COLLECTION
import com.laspika.laspika.core.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow

class HistoryViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    private val _historyState = MutableStateFlow<UiState<List<ReportData>>>(UiState.Loading(false))
    val historyState = _historyState.asLiveData()

    fun getHistory() {
        _historyState.value = UiState.Loading(true)
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(REPORT_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    error.printStackTrace()
                    _historyState.value = UiState.Loading(false)
                    _historyState.value = UiState.Error("Terjadi kesalahan pada server")
                }

                value?.let {
                    val reports = value.toObjects(ReportData::class.java)
                    _historyState.value = UiState.Success(reports)
                    _historyState.value = UiState.Loading(false)
                }

            }
    }
}