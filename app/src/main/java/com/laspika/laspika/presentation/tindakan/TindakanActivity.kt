package com.laspika.laspika.presentation.tindakan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.laspika.laspika.R
import com.laspika.laspika.core.data.ReportData
import com.laspika.laspika.core.utils.UiState
import com.laspika.laspika.core.utils.getFileName
import com.laspika.laspika.core.utils.getFileType
import com.laspika.laspika.core.utils.hide
import com.laspika.laspika.core.utils.reduceFileSize
import com.laspika.laspika.core.utils.show
import com.laspika.laspika.core.utils.showDatePicker
import com.laspika.laspika.core.utils.showTimePicker
import com.laspika.laspika.core.utils.toast
import com.laspika.laspika.core.utils.uriToFile
import com.laspika.laspika.databinding.ActivityTindakanBinding
import com.laspika.laspika.presentation.laporan.LaporanActivity
import com.laspika.laspika.presentation.laporan.LaporanResultActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class TindakanActivity : AppCompatActivity() {

    private var _binding: ActivityTindakanBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<TindakanViewModel>()

    private var fileType: String? = null
    private var file: File? = null
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Uri? = result.data?.data
                data?.let { uri ->
                    val type = getFileType(uri, this)
                    val name = getFileName(uri, this)
                    file = uriToFile(uri, this)
                    if (type != null && name != null) {
                        fileType = type
                        binding.tvInfoUnggah.text = name
                    } else {
                        toast("Gagal mendapat dokumen")
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityTindakanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pageSetup()
        setActions()
        observer()
    }

    private fun observer() {
        viewModel.sendReportState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }

                is UiState.Success -> {
                    startActivity(
                        Intent(this, LaporanResultActivity::class.java).apply {
                            putExtra(LaporanResultActivity.IS_SUCCESS, true)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }

                is UiState.Error -> {
                    startActivity(
                        Intent(this, LaporanResultActivity::class.java).apply {
                            putExtra(LaporanResultActivity.IS_SUCCESS, false)
                        }
                    )
                }
            }
        }
    }

    private fun setActions() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            btnUnggah.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                val mimeTypes = arrayOf("image/*", "video/mp4", "audio/*")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                getContent.launch(intent)
            }

            btnUnggahLaporan.setOnClickListener {
                if (validateInput()) {
                    toast("Harap isi semua input")
                    return@setOnClickListener
                }

                val laporanData = getLaporanData()

                if (fileType == "image") {
                    file = reduceFileSize(file!!)
                }
                viewModel.reportPelanggaran(laporanData, file!!)
            }

            etTanggal.setOnClickListener {
                showDatePicker(this@TindakanActivity) {
                    binding.etTanggal.setText(it)
                }
            }

            etWaktuPerkiraan.setOnClickListener {
                showTimePicker(this@TindakanActivity) {
                    binding.etWaktuPerkiraan.setText(it)
                }
            }
        }
    }

    private fun getLaporanData(): ReportData {
        binding.apply {
            return ReportData(
                kategoriPelanggaran = tvTindakan.text.toString(),
                namaTerduga = etNama.text.toString(),
                jabatan = etJabatan.text.toString(),
                tanggal = etTanggal.text.toString(),
                waktuPerkiraan = etWaktuPerkiraan.text.toString(),
                deskripsi = etDeskripsiPelanggaran.text.toString(),
                jenisDokumen = fileType,
                namaPelapor = if (cbAnonymous.isChecked) null else getUsername(),
                anonymous = cbAnonymous.isChecked
            )
        }
    }

    private fun getUsername(): String? {
        val sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE)
        return sharedPref.getString("username", "")
    }

    private fun validateInput(): Boolean {
        binding.apply {
            return file == null || etJabatan.text.toString().isEmpty() || etNama.text.toString()
                .isEmpty() ||
                    etTanggal.text.toString().isEmpty() || etWaktuPerkiraan.text.toString()
                .isEmpty() ||
                    etDeskripsiPelanggaran.text.toString().isEmpty()
        }
    }

    private fun pageSetup() {
        with(binding) {
            val pelanggaran = intent.getStringExtra(LaporanActivity.LAPORAN)

            pelanggaran?.let {
                tvTindakan.text = it
                tvLaporanInfo.text = getString(R.string.tindak_pidana, it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}