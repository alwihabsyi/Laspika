package com.laspika.laspika.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.laspika.laspika.core.data.UserData
import com.laspika.laspika.core.utils.Constants.USER_COLLECTION
import com.laspika.laspika.core.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ViewModel() {
    private val _signInState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val signInState = _signInState.asLiveData()

    private val _signUpState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val signUpState = _signUpState.asLiveData()

    private val _resetPasswordState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val resetPasswordState = _resetPasswordState.asLiveData()

    fun signIn(email: String, password: String) {
        _signInState.value = UiState.Loading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                getUserData()
            }
            .addOnFailureListener {
                it.printStackTrace()
                _signInState.value = UiState.Loading(false)
                _signInState.value = UiState.Error("Username dan password salah atau user tidak terdaftar")
            }
    }

    private fun getUserData() {
        firestore.collection(USER_COLLECTION).document(auth.uid!!).get()
            .addOnSuccessListener { snapshot ->
                snapshot?.toObject(UserData::class.java)?.let {
                    if (it.admin == true) {
                        auth.signOut()
                        _signInState.value = UiState.Loading(false)
                        _signInState.value = UiState.Error("Bukan akun user")
                        return@addOnSuccessListener
                    }

                    _signInState.value = UiState.Loading(false)
                    _signInState.value = UiState.Success(auth.uid!!)
                }
            }
    }

    fun signUp(userData: UserData, password: String) {
        _signUpState.value = UiState.Loading(true)
        auth.createUserWithEmailAndPassword(userData.email!!, password)
            .addOnSuccessListener {
                if (it.user != null) {
                    saveUserInfo(userData, it.user!!.uid)
                } else {
                    throw Exception("Terjadi kesalahan, silahkan hubungi tim IT")
                }
            }
            .addOnFailureListener {
                _signUpState.value = UiState.Loading(false)
                _signUpState.value = UiState.Error(it.message ?: "Terjadi Kesalahan")
            }
    }

    private fun saveUserInfo(userData: UserData, uid: String) {
        firestore.collection(USER_COLLECTION).document(uid).set(userData)
            .addOnSuccessListener {
                auth.signOut()
                _signUpState.value = UiState.Loading(false)
                _signUpState.value = UiState.Success("Berhasil mendaftar, silahkan masuk")
            }
            .addOnFailureListener {
                _signUpState.value = UiState.Loading(false)
                _signUpState.value = UiState.Error(it.message ?: "Terjadi Kesalahan")
            }
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
    
    fun resetPassword(email: String) {
        _resetPasswordState.value = UiState.Loading(true)
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _resetPasswordState.value = UiState.Loading(false)
                _resetPasswordState.value = UiState.Success("Link reset password telah dikirim ke email anda")
            }
            .addOnFailureListener {
                it.printStackTrace()
                _resetPasswordState.value = UiState.Loading(false)
                _resetPasswordState.value = UiState.Error("Terjadi kesalahan saat mengirim email")
            }
    }
}