package com.laspika.laspika.core.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData (
    val username: String? = null,
    val nama: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val photoUrl: String? = null,
    val admin: Boolean? = false
): Parcelable