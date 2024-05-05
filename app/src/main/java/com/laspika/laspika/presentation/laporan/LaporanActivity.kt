package com.laspika.laspika.presentation.laporan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.laspika.laspika.R
import com.laspika.laspika.core.utils.toast
import com.laspika.laspika.databinding.ActivityLaporanBinding
import com.laspika.laspika.presentation.tindakan.TindakanActivity

class LaporanActivity : AppCompatActivity() {

    private var _binding: ActivityLaporanBinding? = null
    private val binding get() = _binding!!
    private val buttons = mutableListOf<MaterialButton>()
    private var jenisPelanggaran: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeButtons()
        getLaporanIntent()
        setActions()
    }

    private fun setActions() {
        buttons.forEach { laporanBtn ->
            laporanBtn.setOnClickListener {
                selectButton(laporanBtn)
            }
        }

        binding.btnDaftarLaporan.setOnClickListener {
            if (jenisPelanggaran.isNullOrBlank()) {
                toast("Harap pilih jenis pelanggaran")
                return@setOnClickListener
            }

            startActivity(
                Intent(this, TindakanActivity::class.java).apply {
                    putExtra(LAPORAN, jenisPelanggaran)
                }
            )
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initializeButtons() {
        buttons.addAll(
            listOf(
                binding.btnTindakanKorupsi,
                binding.btnPengelolaanKeuangan,
                binding.btnPengadaan,
                binding.btnPelanggaranKepegawaian,
                binding.btnPenyalahgunaanWewenang
            )
        )
    }

    private fun getLaporanIntent() {
        binding.apply {
            val selected = intent.getStringExtra(LAPORAN)
            selected?.let {
                when (it) {
                    KORUPSI -> {
                        selectButton(btnTindakanKorupsi)
                    }

                    KEUANGAN -> {
                        selectButton(btnPengelolaanKeuangan)
                    }

                    PENGADAAN -> {
                        selectButton(btnPengadaan)
                    }

                    PENYALAHGUNAAN -> {
                        selectButton(btnPenyalahgunaanWewenang)
                    }

                    PELANGGARAN -> {
                        selectButton(btnPelanggaranKepegawaian)
                    }
                }
            }
        }
    }

    private fun selectButton(button: MaterialButton) {
        buttons.forEach {
            if (it != button) {
                it.setBackgroundColor(getColor(R.color.light_gray))
                it.setTextColor(getColor(R.color.black))
            } else {
                it.setBackgroundColor(getColor(R.color.blue))
                it.setTextColor(getColor(R.color.white))
                jenisPelanggaran = it.text.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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