package com.example.mylearning.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivitySettingBinding

private lateinit var binding: ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        window.insetsController?.let {
            it.hide(WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setupItems()
        setupListeners()
    }

    private fun setupItems() {
        binding.itemChangeLanguage.ivIcon.setImageResource(R.drawable.icon1)
        binding.itemChangeLanguage.tvTitle.setText("Change Language")
        binding.itemAddWidget.ivIcon.setImageResource(R.drawable.icon2)
        binding.itemAddWidget.tvTitle.setText("Add Widget")
        binding.itemSetDefaultApp.ivIcon.setImageResource(R.drawable.icon3)
        binding.itemSetDefaultApp.tvTitle.setText("Set as Default App")

        binding.itemFeatureRequest.ivIcon.setImageResource(R.drawable.icon4)
        binding.itemFeatureRequest.tvTitle.setText("Feature Request")
        binding.itemMoreFiles.ivIcon.setImageResource(R.drawable.icon5)
        binding.itemMoreFiles.tvTitle.setText("Browse More Files")
        binding.itemShare.ivIcon.setImageResource(R.drawable.icon6)
        binding.itemShare.tvTitle.setText("Share this App")
        binding.itemSendFeedback.ivIcon.setImageResource(R.drawable.icon7)
        binding.itemSendFeedback.tvTitle.setText("Send Feedback")

        binding.itemKeepScreenOn.ivIcon.setImageResource(R.drawable.icon8)
        binding.itemKeepScreenOn.tvTitle.setText("Keep Screen On")
        binding.itemKeepScreenOn.ivArrow.setImageResource(R.drawable.iconoff)
        binding.itemNightMode.ivIcon.setImageResource(R.drawable.icon9)
        binding.itemNightMode.tvTitle.setText("Night Mode (Beta)")
        binding.itemNightMode.ivArrow.setImageResource(R.drawable.iconon)

        binding.itemPrivacyPolicy.ivIcon.setImageResource(R.drawable.icon10)
        binding.itemPrivacyPolicy.tvTitle.setText("Privacy Policy")
        binding.itemTerms.ivIcon.setImageResource(R.drawable.icon11)
        binding.itemTerms.tvTitle.setText("Terms and Conditions")
        binding.itemAboutUs.ivIcon.setImageResource(R.drawable.icon12)
        binding.itemAboutUs.tvTitle.setText("About Us")
//        binding nav
        binding.bottomNavigation.itemHomeNav.ivIcon.setImageResource(R.drawable.home)
        binding.bottomNavigation.itemFavouriteNav.ivIcon.setImageResource(R.drawable.favourite)
        binding.bottomNavigation.itemToolNav.ivIcon.setImageResource(R.drawable.tool)
        binding.bottomNavigation.itemSettingNav.ivIcon.setImageResource(R.drawable.setting)
        binding.bottomNavigation.itemSettingNav.tvTitle.setTextColor(Color.parseColor("#FF5C01"))
    }

    private fun setupListeners(){
        binding.itemChangeLanguage.root.setOnClickListener {
            val intent
        }

    }

}