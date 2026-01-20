package com.example.mylearning.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mylearning.database.AppDatabase
import com.example.mylearning.databinding.ActivitySplashBinding
import com.example.mylearning.repository.FileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var index = 0
    private val texts = listOf("Loading.\nhẹ", "Loading..\nhẹ hẹ", "Loading...\nhẹ hẹ hẹ")
    private lateinit var repository: FileRepository




    // SharedPreferences cho onboarding flow
    private val prefs by lazy { getSharedPreferences(PREFS_ONBOARDING, Context.MODE_PRIVATE) }

    // Launcher xin quyền notification (API 33+)
//    private val requestNotiPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        // Lưu kết quả: true nếu Allow, false nếu Deny
//        prefs.edit().putBoolean(KEY_NOTI_GRANTED_ON_SPLASH, isGranted).apply()
//        // Điều hướng tới LanguageActivity2
//        navigateToLanguage()
//    }

    private val requestNotiPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        prefs.edit().putBoolean(KEY_NOTI_GRANTED_ON_SPLASH, isGranted).apply()
        if(prefs.getBoolean(KEY_ONBOARDING_DONE, false)){
            navigateToMain()
        } else{
            prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
            navigateToLanguage()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = AppDatabase.getDatabase(applicationContext)
        repository = FileRepository(db.fileDao())

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        startLoadingText()
        checkOnboardingAndProceed()
        scanFiles()
    }

    private fun setupViews() {
        binding.lottieView.speed = 2f

        // LẤY TÊN APP
        val appName = applicationInfo.loadLabel(packageManager)
        binding.tvTitle.text = "Welcome to $appName"
    }

    private fun startLoadingText() {
        lifecycleScope.launch {
            while (isActive) {
                binding.tvLoad.text = texts[index % texts.size]
                index++
                delay(1000) // 1s
            }
        }
    }

    /**
     * Kiểm tra đã qua onboarding chưa:
     * - Nếu rồi → vào thẳng MainActivity
     * - Nếu chưa → chờ 3s rồi xin quyền noti (nếu API 33+) hoặc đi tiếp
     */
    private fun checkOnboardingAndProceed() {
        val onboardingDone = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
                if(onboardingDone){
                    navigateToMain()
                } else{
                    prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
                    navigateToLanguage()
                }
                return@launch
            }
            if(onboardingDone){
                if(ContextCompat.checkSelfPermission( this@SplashActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                    requestNotiPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else{
                    navigateToMain()
                }
            } else{
                requestNotiPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }



    private fun scanFiles(){
        lifecycleScope.launch {
            repository.refreshFiles()
            Toast.makeText(this@SplashActivity, "Đang quét file...", Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToLanguage() {
        val intent = Intent(this, LanguageActivity2::class.java)
        intent.putExtra(FROM_SPLASH, true)
        startActivity(intent)
        finish()

    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        const val PREFS_ONBOARDING = "onboarding_prefs"
        const val KEY_ONBOARDING_DONE = "onboarding_done"
        const val KEY_NOTI_GRANTED_ON_SPLASH = "noti_granted_on_splash"
        const val FROM_SPLASH = "from_splash"
    }
}
