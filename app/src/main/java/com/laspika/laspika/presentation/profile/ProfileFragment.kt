package com.laspika.laspika.presentation.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.laspika.laspika.core.data.UserData
import com.laspika.laspika.core.utils.UiState
import com.laspika.laspika.core.utils.glide
import com.laspika.laspika.core.utils.setupLogoutDialog
import com.laspika.laspika.core.utils.toast
import com.laspika.laspika.databinding.FragmentProfileBinding
import com.laspika.laspika.presentation.home.HomeViewModel
import com.laspika.laspika.presentation.onboarding.OnBoardingActivity
import com.laspika.laspika.presentation.profile.ProfileDetailActivity.Companion.USER_DATA
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<HomeViewModel>()
    private var userData: UserData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        pageSetup()
    }

    private fun observer() {
        viewModel.getUserData()
        viewModel.userState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    state.data?.let {
                        userData = it
                        binding.tvUsername.text = it.nama ?: it.username
                        if (it.photoUrl != null) binding.ivProfile.glide(it.photoUrl)
                    }
                }
                is UiState.Error -> {
                    toast(state.error!!)
                }
            }
        }
    }

    private fun pageSetup() {
        binding.apply {
            tvDetail.setOnClickListener {
                startActivity(
                    Intent(requireContext(), ProfileDetailActivity::class.java).apply {
                        putExtra(USER_DATA, userData)
                    }
                )
            }

            btnLogout.setOnClickListener {
                requireActivity().setupLogoutDialog(
                    "Log Out",
                    "Anda akan keluar dari akun",
                    "Ya"
                ) {
                    deletePrefs()
                    viewModel.logout()
                    startActivity(
                        Intent(requireContext(), OnBoardingActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }
            }
        }
    }

    private fun deletePrefs() {
        val sharedPref = requireActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove("username")
        editor.remove("email")
        editor.apply()

        val onBoardingPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        onBoardingPref.edit().remove("OnBoarding").apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}