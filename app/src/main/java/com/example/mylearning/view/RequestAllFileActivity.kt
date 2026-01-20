package com.example.mylearning.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityRequestAllFileBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RequestAllFileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestAllFileBinding

    // SharedPreferences để đọc noti_granted_on_splash và set onboarding_done
    private val prefs by lazy { getSharedPreferences(SplashActivity.PREFS_ONBOARDING, Context.MODE_PRIVATE) }
    private var isRedirectedToSettings = false
    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { isGranted ->
        navigateNext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRequestAllFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        styleTexts()
        setupClickListeners()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            val maxDuration = 15000L // 15 giây
            while (System.currentTimeMillis() - startTime < maxDuration) {
                if (Environment.isExternalStorageManager()) {
                    isRedirectedToSettings = false
                    navigateNext()
                    break
                }
                delay(500)
            }
        }
    }

    private fun setupClickListeners() {
        // Nút "Cho phép" → mở Settings All Files Access
        binding.btnAllow.setOnClickListener {
            openAllFilesAccessSettings()
        }

        // Nút "Để sau" → điều hướng theo logic
        binding.btnLater.setOnClickListener {
            navigateNext()
        }
    }

    /**
     * Mở Settings để user tự bật "All files access" (API 30+)
     */
    private fun openAllFilesAccessSettings() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if(!Environment.isExternalStorageManager()){
                isRedirectedToSettings = true
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    settingsLauncher.launch(intent)
                } catch (e: Exception){
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    settingsLauncher.launch(intent)
                }
            } else{
                navigateNext()
            }
        } else{
            navigateNext()
        }
    }


    private fun navigateNext() {
        val notiGrantedOnSplash = prefs.getBoolean(SplashActivity.KEY_NOTI_GRANTED_ON_SPLASH, false)

        if (!notiGrantedOnSplash) {
            startActivity(Intent(this, RequestNotiActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }


    private fun styleTexts() {
        val appName = getString(R.string.presentation_reader)
        val title = getString(R.string.request_allfile_desc, appName)
        val primaryColor = getColor(R.color.primaryColor)
        binding.tvDesc2.text = colorize(title, primaryColor, appName)
    }

    private fun colorize(text: String, color: Int, targetWord: String): SpannableString {
        val span = SpannableString(text)
        val start = text.indexOf(targetWord)
        if (start >= 0) {
            val end = start + targetWord.length
            span.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return span
    }
}