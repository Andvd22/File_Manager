package com.example.mylearning.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityRequestNotiBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RequestNotiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestNotiBinding

    // SharedPreferences để set onboarding_done
    private val prefs by lazy { getSharedPreferences(SplashActivity.PREFS_ONBOARDING, Context.MODE_PRIVATE) }
    private var fromSplashDenyNoti = false
    private var isRedirectedToSettings = false

    // Launcher xin quyền notification (API 33+)
    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        goToMainAndFinish()
    }
    private val requestNotiPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Dù Allow hay Deny đều vào MainActivity
        goToMainAndFinish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRequestNotiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fromSplashDenyNoti = intent.getBooleanExtra(SplashActivity.PERMANENTLY_DENIED_NOTI, false)
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
                val notiEnabled =
                    NotificationManagerCompat.from(this@RequestNotiActivity)
                        .areNotificationsEnabled()
                if (notiEnabled) {
                    isRedirectedToSettings = false
                    goToMainAndFinish()
                    break
                }
                delay(500)
            }
        }
    }

    private fun setupClickListeners() {
        // Nút "Cho phép" → hiện dialog xin quyền noti của hệ thống
        binding.root.setOnClickListener {
            if(!fromSplashDenyNoti){
                requestNotificationPermission()
            }
            else {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                isRedirectedToSettings = true
                settingsLauncher.launch(intent)
            }
        }

        // Nút "Để sau" → vào Main luôn
        binding.btnLater.setOnClickListener {
            goToMainAndFinish()
        }
    }

    /**
     * Xin quyền notification:
     * - API 33+ → hiện dialog hệ thống
     * - API < 33 → không có runtime permission → đi thẳng Main
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Kiểm tra đã có quyền chưa
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // Đã có quyền → đi Main luôn
                goToMainAndFinish()
            } else {
                // Chưa có → xin quyền, callback sẽ xử lý điều hướng
                requestNotiPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // API < 33 → không xin được, đi Main luôn
            goToMainAndFinish()
        }
    }

    /**
     * Đánh dấu onboarding hoàn thành và vào MainActivity
     */
    private fun goToMainAndFinish() {
        prefs.edit().putBoolean(SplashActivity.KEY_ONBOARDING_DONE, true).apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun styleTexts() {
        val appName = getString(R.string.presentation_reader)
        val title = getString(R.string.request_noti_desc_1, appName)
        val primaryColor = getColor(R.color.primaryColor)
        binding.tvDesc1.text = colorize(title, primaryColor, appName)
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