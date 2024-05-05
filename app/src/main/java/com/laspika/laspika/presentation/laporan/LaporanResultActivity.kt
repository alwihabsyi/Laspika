package com.laspika.laspika.presentation.laporan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.laspika.laspika.R
import com.laspika.laspika.core.utils.hide
import com.laspika.laspika.core.utils.show
import com.laspika.laspika.databinding.ActivityLaporanResultBinding
import com.laspika.laspika.presentation.MainActivity

class LaporanResultActivity : AppCompatActivity() {

    private var _binding: ActivityLaporanResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLaporanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pageSetup()
    }

    private fun pageSetup() {
        binding.apply {
            val isSuccess = intent.getBooleanExtra(IS_SUCCESS, false)
            if (isSuccess) {
                tvMessageResult.text = getString(R.string.message_success)
                tvErrorInfo.hide()
                btnToHistory.show()
            } else {
                tvMessageResult.text = getString(R.string.message_failed)
                tvErrorInfo.show()
                btnToHistory.hide()
            }

            btnBack.setOnClickListener {
                if (isSuccess) {
                    startActivity(Intent(this@LaporanResultActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                } else {
                    finish()
                }
            }

            btnToHistory.setOnClickListener {
                startActivity(Intent(this@LaporanResultActivity, MainActivity::class.java).apply {
                    putExtra(MainActivity.FRAGMENT, "history")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val IS_SUCCESS = "result"
    }
}