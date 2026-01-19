package com.example.mylearning.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mylearning.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var index = 0
    private val texts = listOf("Loading.\nhẹ", "Loading..\nhẹ hẹ", "Loading...\nhẹ hẹ hẹ")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        startLoadingText()
        goNextScreen()
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

    private fun goNextScreen() {
        lifecycleScope.launch {
            delay(3000) // thời gian splash
            startActivity(Intent(this@SplashActivity, LanguageActivity2::class.java))
            finish()
        }
    }
}
