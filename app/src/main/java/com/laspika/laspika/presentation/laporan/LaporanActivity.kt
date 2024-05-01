package com.laspika.laspika.presentation.laporan

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.laspika.laspika.R

class LaporanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laporan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        const val LAPORAN = "laporan"
        const val KORUPSI = "Tindakan Korupsi"
        const val KEUANGAN = "Pengelolaan Keuangan"
        const val PENGADAAN = "Pengadaan Barang dan Jasa"
        const val PENYALAHGUNAAN = "Penyalahgunaan Wewenang"
        const val PELANGGARAN = "Pelanggaran Kepegawaian"
    }
}