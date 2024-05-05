package com.laspika.laspika.presentation.profile

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.laspika.laspika.R
import com.laspika.laspika.core.data.UserData
import com.laspika.laspika.core.utils.UiState
import com.laspika.laspika.core.utils.glide
import com.laspika.laspika.core.utils.hide
import com.laspika.laspika.core.utils.reduceFileSize
import com.laspika.laspika.core.utils.show
import com.laspika.laspika.core.utils.toast
import com.laspika.laspika.core.utils.uriToFile
import com.laspika.laspika.databinding.ActivityProfileDetailBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ProfileDetailActivity : AppCompatActivity() {

    private var _binding: ActivityProfileDetailBinding? = null
    private val binding get() = _binding!!
    private var userData: UserData? = null
    private val viewModel by viewModel<ProfileViewModel>()
    private var file: File? = null
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Uri? = result.data?.data
                data?.let { uri ->
                    file = uriToFile(uri, this)
                    binding.ivProfile.setImageURI(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityProfileDetailBinding.inflate(layoutInflater)
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

    @Suppress("DEPRECATION")
    private fun pageSetup() {
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(USER_DATA, UserData::class.java)
        else
            intent.getParcelableExtra(USER_DATA)

        data?.let {
            userData = it
            binding.apply {
                etUsername.setText(it.username)
                etEmail.setText(it.email)
                etName.setText(it.nama)
                etPhone.setText(it.phone)

                if (it.photoUrl != null) ivProfile.glide(it.photoUrl)
            }
        }
    }

    private fun setActions() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            ivProfile.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                getContent.launch(intent)
            }

            btnSimpan.setOnClickListener {
                if (validateInput()) {
                    toast("Harap isi semua bagian")
                    return@setOnClickListener
                }

                val password = if(etPassword.text.toString() == getString(R.string.pw)) null else etPassword.text.toString()

                lifecycleScope.launch {
                    if(file != null) {
                        viewModel.editProfile(getUser(), true, reduceFileSize(file!!), password)
                    } else {
                        viewModel.editProfile(getUser(), false, null, password)
                    }
                }
            }
        }
    }

    private fun observer() {
        viewModel.editState.observe(this) {
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

    private fun getUser(): UserData {
        binding.apply {
            return UserData(
                username = etUsername.text.toString(),
                nama = etName.text.toString(),
                email = etEmail.text.toString(),
                phone = etPhone.text.toString(),
                photoUrl = userData?.photoUrl
            )
        }
    }

    private fun validateInput(): Boolean {
        binding.apply {
            return etUsername.text.toString().isEmpty() || etName.text.toString().isEmpty() ||
                    etPhone.text.toString().isEmpty() || etEmail.text.toString().isEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val USER_DATA = "user_data"
    }
}