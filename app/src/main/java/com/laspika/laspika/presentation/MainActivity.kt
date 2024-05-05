package com.laspika.laspika.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.laspika.laspika.R
import com.laspika.laspika.databinding.ActivityMainBinding
import com.laspika.laspika.presentation.laporan.LaporanActivity

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.background = null
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentLayout.id) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.selectedItemId = R.id.homeFragment

        intent.getStringExtra(FRAGMENT)?.let {
            when (it) {
                "history" -> {
                    binding.bottomNavigationView.selectedItemId = R.id.historyFragment
                    intent.removeExtra(FRAGMENT)
                }
            }
        }

        findViewById<View>(R.id.toLaporan).setOnClickListener {
            startActivity(Intent(this, LaporanActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val FRAGMENT = "fragment"
    }
}