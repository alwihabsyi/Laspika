package com.laspika.laspika.presentation.onboarding

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.laspika.laspika.R
import com.laspika.laspika.databinding.ActivitySplashScreenBinding
import com.laspika.laspika.presentation.auth.AuthActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpSplash()
    }

    private fun setUpSplash() {
        binding.ivLogo.animate().apply {
            duration = 1500
            alpha(1f)
        }.withEndAction {
            val destination =
                if (onBoardingFinished()) AuthActivity::class.java else OnBoardingActivity::class.java
            startActivity(
                Intent(this@SplashScreenActivity, destination)
            )
            finish()
        }
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}