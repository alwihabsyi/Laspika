package com.laspika.laspika.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.laspika.laspika.R
import com.laspika.laspika.databinding.FragmentHomeBinding
import com.laspika.laspika.presentation.laporan.LaporanActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRadioBtn()
        binding.btnReport.setOnClickListener {
            startActivity(
                Intent(context, LaporanActivity::class.java)
            )
        }
    }

    private fun setUpRadioBtn() {
        binding.apply {
            rbPengadaan.setOnClickListener {
                rbPengadaan.isChecked = false
                startActivity(
                    Intent(context, LaporanActivity::class.java).apply {
                        putExtra(LaporanActivity.LAPORAN, LaporanActivity.PENGADAAN)
                    }
                )
            }

            rbPelanggaran.setOnClickListener {
                rbPelanggaran.isChecked = false
                startActivity(
                    Intent(context, LaporanActivity::class.java).apply {
                        putExtra(LaporanActivity.LAPORAN, LaporanActivity.PELANGGARAN)
                    }
                )
            }

            rbPenyalahgunaan.setOnClickListener {
                rbPenyalahgunaan.isChecked = false
                startActivity(
                    Intent(context, LaporanActivity::class.java).apply {
                        putExtra(LaporanActivity.PELANGGARAN, LaporanActivity.PENYALAHGUNAAN)
                    }
                )
            }

            rbTindakanKorupsi.setOnClickListener {
                rbTindakanKorupsi.isChecked = false
                startActivity(
                    Intent(context, LaporanActivity::class.java).apply {
                        putExtra(LaporanActivity.PELANGGARAN, LaporanActivity.KORUPSI)
                    }
                )
            }

            rbPengelolaanKeuangan.setOnClickListener {
                rbPengelolaanKeuangan.isChecked = false
                startActivity(
                    Intent(context, LaporanActivity::class.java).apply {
                        putExtra(LaporanActivity.PELANGGARAN, LaporanActivity.KEUANGAN)
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}