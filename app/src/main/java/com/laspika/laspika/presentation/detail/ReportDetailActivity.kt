package com.laspika.laspika.presentation.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.laspika.laspika.R
import com.laspika.laspika.core.data.ReportData
import com.laspika.laspika.core.utils.Constants
import com.laspika.laspika.core.utils.UiState
import com.laspika.laspika.core.utils.hide
import com.laspika.laspika.core.utils.show
import com.laspika.laspika.core.utils.toast
import com.laspika.laspika.databinding.ActivityReportDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportDetailActivity : AppCompatActivity() {

    private var _binding: ActivityReportDetailBinding? = null
    private val binding get() = _binding!!
    private var reportData: ReportData? = null
    private val viewModel by viewModel<ReportDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityReportDetailBinding.inflate(layoutInflater)
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
        viewModel.cancelState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
                    finish()
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
            }
        }
    }

    private fun setActions() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            btnView.setOnClickListener {
                reportData?.dokumenUrl?.let { url -> openFile(url) }
            }

            btnCancelLaporan.setOnClickListener {
                if (reportData == null) {
                    toast("Gagal mendapatkan data laporan, silahkan coba kembali")
                    return@setOnClickListener
                }

                val reportCancelled = reportData!!.copy( status = Constants.CANCELLED )
                viewModel.cancelReport(reportCancelled)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun pageSetup() {
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(REPORT_DETAIL, ReportData::class.java)
        } else {
            intent.getParcelableExtra(REPORT_DETAIL)
        }

        data?.let { report ->
            reportData = report
            binding.apply {
                tvLaporanInfo.text = getString(R.string.history_title, report.kategoriPelanggaran)
                etNama.setText(report.namaTerduga)
                etJabatan.setText(report.jabatan)
                etTanggal.setText(report.tanggal)
                etWaktuPerkiraan.setText(report.waktuPerkiraan)
                etDeskripsiPelanggaran.setText(report.deskripsi)
                cbAnonymous.isChecked = report.anonymous == true

                if (report.status == Constants.CANCELLED) {
                    binding.btnCancelLaporan.isEnabled = false
                    binding.btnCancelLaporan.setBackgroundColor(getColor(R.color.light_gray))
                }
            }
        }
    }

    private fun openFile(dokumenUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(dokumenUrl)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            toast("No application found to open this file")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val REPORT_DETAIL = "report_detail"
    }
}