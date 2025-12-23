package com.example.mylearning.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivitySettingBinding
import kotlin.getValue




class SettingActivity : AppCompatActivity() {
    private val prefs by lazy {
        getSharedPreferences("app_settings", MODE_PRIVATE)
    }
    companion object {
        private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
        private const val KEY_NIGHT_MODE = "night_mode"
    }

    private lateinit var binding: ActivitySettingBinding
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
        setupToggles()
    }

    private fun setupItems() {
        binding.itemChangeLanguage.ivIcon.setImageResource(R.drawable.setting_activity_icon1)
        binding.itemChangeLanguage.tvTitle.setText(R.string.change_language)
        binding.itemAddWidget.ivIcon.setImageResource(R.drawable.setting_activity_icon2)
        binding.itemAddWidget.tvTitle.setText(R.string.add_widget)
        binding.itemSetDefaultApp.ivIcon.setImageResource(R.drawable.setting_activity_icon3)
        binding.itemSetDefaultApp.tvTitle.setText(R.string.set_default_app)

        binding.itemFeatureRequest.ivIcon.setImageResource(R.drawable.setting_activity_icon4)
        binding.itemFeatureRequest.tvTitle.setText(R.string.feature_request)
        binding.itemMoreFiles.ivIcon.setImageResource(R.drawable.setting_activity_icon5)
        binding.itemMoreFiles.tvTitle.setText(R.string.browse_more_files)
        binding.itemShare.ivIcon.setImageResource(R.drawable.setting_activity_icon6)
        binding.itemShare.tvTitle.setText(R.string.share_this_app)
        binding.itemSendFeedback.ivIcon.setImageResource(R.drawable.setting_activity_icon7)
        binding.itemSendFeedback.tvTitle.setText(R.string.send_feedback)

        binding.itemKeepScreenOn.ivIcon.setImageResource(R.drawable.setting_activity_icon8)
        binding.itemKeepScreenOn.tvTitle.setText(R.string.keep_screen_on)
        binding.itemKeepScreenOn.ivArrow.setImageResource(R.drawable.setting_activity_iconoff)
        binding.itemNightMode.ivIcon.setImageResource(R.drawable.setting_activity_icon9)
        binding.itemNightMode.tvTitle.setText(R.string.night_mode)
        binding.itemNightMode.ivArrow.setImageResource(R.drawable.setting_activity_iconoff)

        binding.itemPrivacyPolicy.ivIcon.setImageResource(R.drawable.setting_activity_icon10)
        binding.itemPrivacyPolicy.tvTitle.setText(R.string.privacy_policy)
        binding.itemTerms.ivIcon.setImageResource(R.drawable.setting_activity_icon11)
        binding.itemTerms.tvTitle.setText(R.string.terms_and_conditions)
        binding.itemAboutUs.ivIcon.setImageResource(R.drawable.setting_activity_icon12)
        binding.itemAboutUs.tvTitle.setText(R.string.about_us)
//        binding nav
        binding.bottomNavigation.itemHomeNav.ivIcon.setImageResource(R.drawable.setting_activity_home)
        binding.bottomNavigation.itemHomeNav.tvTitle.setText(R.string.home)
        binding.bottomNavigation.itemFavouriteNav.ivIcon.setImageResource(R.drawable.setting_activity_favourite)
        binding.bottomNavigation.itemFavouriteNav.tvTitle.setText(R.string.favorite)
        binding.bottomNavigation.itemToolNav.ivIcon.setImageResource(R.drawable.setting_activity_tool)
        binding.bottomNavigation.itemToolNav.tvTitle.setText(R.string.tools)
        binding.bottomNavigation.itemSettingNav.ivIcon.setImageResource(R.drawable.setting_activity_setting)
        binding.bottomNavigation.itemSettingNav.tvTitle.setText(R.string.settings)
        binding.bottomNavigation.itemSettingNav.tvTitle.setTextColor(Color.parseColor("#FF5C01"))
    }

    private fun setupListeners(){
        binding.itemChangeLanguage.root.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigation.itemHomeNav.root.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoPremium.setOnClickListener {
            val intent = Intent(this, PremiumActivity::class.java)
            startActivity(intent)
        }

        binding.itemSendFeedback.root.setOnClickListener {
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setupToggles(){
        applyKeepScreenOn(prefs.getBoolean(KEY_KEEP_SCREEN_ON,false))
        binding.itemKeepScreenOn.root.setOnClickListener {
            val next = !prefs.getBoolean(KEY_KEEP_SCREEN_ON,false)
            applyKeepScreenOn(next)
            prefs.edit().putBoolean(KEY_KEEP_SCREEN_ON, next).apply()
        }
        applyNightMode(prefs.getBoolean(KEY_NIGHT_MODE,false))
        binding.itemNightMode.root.setOnClickListener {
            val next = !prefs.getBoolean(KEY_NIGHT_MODE, false)
            applyNightMode(next)
            prefs.edit().putBoolean(KEY_NIGHT_MODE, next).apply()
        }
    }

    private fun applyKeepScreenOn(enabled: Boolean){
        if(enabled){
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            binding.itemKeepScreenOn.ivArrow.setImageResource(R.drawable.setting_activity_iconon)
        }else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            binding.itemKeepScreenOn.ivArrow.setImageResource(R.drawable.setting_activity_iconoff)
        }
    }
    private fun applyNightMode(enabled: Boolean){
        if(enabled){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.itemNightMode.ivArrow.setImageResource(R.drawable.setting_activity_iconon)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.itemNightMode.ivArrow.setImageResource(R.drawable.setting_activity_iconoff)
        }
        delegate.applyDayNight()
    }

}