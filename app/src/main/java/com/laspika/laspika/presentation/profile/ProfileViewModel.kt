package com.laspika.laspika.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.laspika.laspika.core.data.UserData
import com.laspika.laspika.core.utils.Constants.USER_COLLECTION
import com.laspika.laspika.core.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import java.io.File

class ProfileViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
): ViewModel() {
    private val _editState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val editState = _editState.asLiveData()

    suspend fun editProfile(userData: UserData, withImage: Boolean, imageFile: File?, password: String?) {
        _editState.value = UiState.Loading(true)
        if (withImage) {
            val imageUrl = uploadImage(imageFile)
            val updatedUser = userData.copy(photoUrl = imageUrl)
            firestore.collection(USER_COLLECTION).document(auth.uid!!)
                .set(updatedUser)
                .addOnSuccessListener {
                    if (password != null) {
                        updatePassword(password)
                        return@addOnSuccessListener
                    }
                    uploadSuccess()
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    uploadError()
                }
        } else {
            firestore.collection(USER_COLLECTION).document(auth.uid!!)
                .set(userData)
                .addOnSuccessListener {
                    if (password != null) {
                        updatePassword(password)
                        return@addOnSuccessListener
                    }
                    uploadSuccess()
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    uploadError()
                }
        }
    }

    private fun updatePassword(password: String) {
        auth.currentUser!!.updatePassword(password)
            .addOnSuccessListener {
                uploadSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                uploadError("Gagal mengubah password, silahkan login ulang dan coba lagi")
            }
    }

    private fun uploadSuccess() {
        _editState.value = UiState.Loading(false)
        _editState.value = UiState.Success("Data berhasil diperbarui")
    }

    private fun uploadError(error: String? = null) {
        _editState.value = UiState.Loading(false)
        _editState.value = UiState.Error(error ?: "Terjadi kesalahan pada server")
    }

    private suspend fun uploadImage(imageFile: File?): String? {
        return try {
            val docByteArray = imageFile!!.readBytes()
            val imageStorage = storage.reference.child("user/${auth.uid}")
            val result = imageStorage.putBytes(docByteArray).await()
            val downloadUrl = result.storage.downloadUrl.await().toString()
            downloadUrl
        } catch (e: Exception) {
            Log.e("ProfilViewModel", e.message ?: "Terjadi Kesalahan")
            _editState.value = UiState.Loading(false)
            _editState.value = UiState.Error("Terjadi kesalahan saat upload dokumen bukti ke server")
            null
        }
    }
}