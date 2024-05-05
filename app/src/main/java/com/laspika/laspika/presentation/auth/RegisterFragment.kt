package com.laspika.laspika.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.laspika.laspika.core.data.UserData
import com.laspika.laspika.core.utils.UiState
import com.laspika.laspika.core.utils.hide
import com.laspika.laspika.core.utils.show
import com.laspika.laspika.core.utils.toast
import com.laspika.laspika.databinding.FragmentRegisterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        observer()
    }

    private fun observer() {
        viewModel.signUpState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
                    findNavController().navigateUp()
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
            }
        }
    }

    private fun setActions() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            tvLogin.setOnClickListener {
                findNavController().navigateUp()
            }

            btnRegister.setOnClickListener {
                if (!validateInput()) {
                    toast("Harap isi semua bagian")
                    return@setOnClickListener
                }

                if (etPassword.text.toString() != etConfirmPassword.text.toString()) {
                    etConfirmPassword.error = "Password tidak sesuai"
                    return@setOnClickListener
                }

                viewModel.signUp(getAllInputs(), etPassword.text.toString())
            }
        }
    }

    private fun validateInput(): Boolean {
        binding.apply {
            return etUsername.text.isNotEmpty() && etEmail.text.isNotEmpty() &&
                    etPhone.text.isNotEmpty() && etPassword.text.isNotEmpty() &&
                    etConfirmPassword.text.isNotEmpty()
        }
    }

    private fun getAllInputs(): UserData {
        binding.apply {
            return UserData(
                username = etUsername.text.toString(),
                email = etEmail.text.toString(),
                phone = etPhone.text.toString(),
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}