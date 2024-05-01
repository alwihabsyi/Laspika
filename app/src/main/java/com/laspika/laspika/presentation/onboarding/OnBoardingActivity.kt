package com.laspika.laspika.presentation.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.laspika.laspika.R
import com.laspika.laspika.presentation.onboarding.adapter.OnBoardingAdapter
import com.laspika.laspika.presentation.onboarding.adapter.OnBoardingItem
import com.laspika.laspika.databinding.ActivityOnBoardingBinding
import com.laspika.laspika.presentation.auth.AuthActivity

class OnBoardingActivity : AppCompatActivity() {

    private var _binding: ActivityOnBoardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var onBoardingAdapter: OnBoardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupOnBoardingItems()
        binding.viewPager2.adapter = onBoardingAdapter

        setupOnBoardingItems()
        setupBoardingIndicators()
        setActiveIndicators(0)

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setActiveIndicators(position)
            }
        })

        binding.btnLewati.setOnClickListener {
            val intent = Intent(this@OnBoardingActivity, AuthActivity::class.java)
            startActivity(intent)
            onBoardingFinished()
            finish()
        }

        binding.btnLanjut.setOnClickListener {
            if (binding.viewPager2.currentItem + 1 < onBoardingAdapter.itemCount) {
                binding.viewPager2.currentItem += 1
            } else {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                onBoardingFinished()
                finish()
            }
        }
    }

    private fun setupOnBoardingItems() {
        val onboardingItems: MutableList<OnBoardingItem> = ArrayList()

        val onboarding1 = OnBoardingItem(
            resources.getString(R.string.onboarding_1),
            resources.getString(R.string.onboarding_desc_1),
            R.drawable.onboarding_1
        )
        val onboarding2 = OnBoardingItem(
            resources.getString(R.string.onboarding_2),
            resources.getString(R.string.onboarding_desc_2),
            R.drawable.onboarding_2
        )
        val onboarding3 = OnBoardingItem(
            resources.getString(R.string.onboarding_3),
            resources.getString(R.string.onboarding_desc_3),
            R.drawable.onboarding_3
        )
        val onboarding4 = OnBoardingItem(
            resources.getString(R.string.onboarding_4),
            resources.getString(R.string.onboarding_desc_4),
            R.drawable.onboarding_4
        )
        onboardingItems.add(onboarding1)
        onboardingItems.add(onboarding2)
        onboardingItems.add(onboarding3)
        onboardingItems.add(onboarding4)
        onBoardingAdapter = OnBoardingAdapter()
        onBoardingAdapter.setNewItem(onboardingItems)
    }

    private fun setupBoardingIndicators() {
        val indicators = arrayOfNulls<ImageView>(onBoardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.viewpagerIndicator.addView(indicators[i])
        }
    }

    private fun setActiveIndicators(index: Int) {
        val childCount = binding.viewpagerIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = binding.viewpagerIndicator[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_indicator_inactive
                    )
                )
            }
        }
    }

    private fun onBoardingFinished() {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("OnBoarding", true)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}