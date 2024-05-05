package com.laspika.laspika.core.data

import android.os.Parcelable
import com.laspika.laspika.core.utils.Constants
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ReportData (
    val id: String = UUID.randomUUID().toString(),
    val kategoriPelanggaran: String? = null,
    val namaTerduga: String? = null,
    val jabatan: String? = null,
    val tanggal: String? = null,
    val waktuPerkiraan: String? = null,
    val deskripsi: String? = null,
    val dokumenUrl: String? = null,
    val jenisDokumen: String? = null,
    val namaPelapor: String? = null,
    val anonymous: Boolean? = false,
    val status: String? = Constants.PENDING,
    val uid: String? = null
): Parcelable