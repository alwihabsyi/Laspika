package com.laspika.laspika.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.laspika.laspika.R
import com.laspika.laspika.core.utils.UiState
import com.laspika.laspika.core.utils.hide
import com.laspika.laspika.core.utils.show
import com.laspika.laspika.core.utils.toast
import com.laspika.laspika.databinding.FragmentLoginBinding
import com.laspika.laspika.presentation.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        observer()
    }

    private fun observer() {
        viewModel.signInState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    startActivity(
                        Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
            }
        }
    }

    private fun setActions() {
        binding.apply {
            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    toast("Harap isi email dan password")
                    return@setOnClickListener
                }

                viewModel.signIn(email, password)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}