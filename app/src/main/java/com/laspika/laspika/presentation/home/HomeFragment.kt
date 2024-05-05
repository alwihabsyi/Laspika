package com.laspika.laspika.presentation.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.radiobutton.MaterialRadioButton
import com.laspika.laspika.R
import com.laspika.laspika.core.data.UserData
import com.laspika.laspika.core.utils.UiState
import com.laspika.laspika.core.utils.glide
import com.laspika.laspika.core.utils.toast
import com.laspika.laspika.databinding.FragmentHomeBinding
import com.laspika.laspika.presentation.laporan.LaporanActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val radioButtons = mutableListOf<MaterialRadioButton>()
    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        askPermissions()
        initializeRadioBtn()
        setActions()
        observer()
    }

    private fun askPermissions() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            requireActivity().requestPermissions(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ), 303
            )
        }
    }

    private fun observer() {
        viewModel.getUserData()
        viewModel.userState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    binding.tvHelloUser.text = getString(R.string.hai_dela_amaliya, state.data?.nama ?: state.data?.username)
                    state.data?.photoUrl?.let { binding.ivProfile.glide(it) }
                    state.data?.let { user -> saveUsername(user) }
                }

                is UiState.Error -> {
                    binding.tvHelloUser.text = getString(R.string.hai_dela_amaliya, "User")
                    toast(state.error!!)
                }

                else -> {}
            }
        }
    }

    private fun saveUsername(user: UserData) {
        val sharedPref = requireActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("username", user.username)
        editor.putString("email", user.email)
        editor.apply()
    }

    private fun initializeRadioBtn() {
        radioButtons.addAll(
            listOf(
                binding.rbPengadaan,
                binding.rbPelanggaran,
                binding.rbPenyalahgunaan,
                binding.rbPengelolaanKeuangan,
                binding.rbTindakanKorupsi
            )
        )
    }

    private fun setActions() {
        binding.apply {
            radioButtons.forEach { radioButton ->
                radioButton.setOnClickListener {
                    selectRb(radioButton)
                }
            }

            btnReport.setOnClickListener {
                startActivity(
                    Intent(context, LaporanActivity::class.java)
                )
            }
        }
    }

    private fun selectRb(radioButton: MaterialRadioButton) {
        radioButtons.forEach {
            if (it == radioButton) {
                it.isChecked = false
                startActivity(
                    Intent(context, LaporanActivity::class.java).apply {
                        putExtra(LaporanActivity.LAPORAN, it.text)
                    }
                )
            } else {
                it.isChecked = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}