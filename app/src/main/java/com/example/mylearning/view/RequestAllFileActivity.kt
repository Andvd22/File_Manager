package com.example.mylearning.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityRequestAllFileBinding

class RequestAllFileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestAllFileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestAllFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        styleTexts()
        setupClickListeners()
    }

    private fun setupClickListeners(){

    }

    private fun styleTexts(){
        val appName = getString(R.string.presentation_reader)

        val title = getString(R.string.request_allfile_desc, appName)

        val primaryColor = getColor(R.color.primaryColor)


        binding.tvDesc2.text = colorize(title, primaryColor, appName)
    }

    private fun colorize(text: String, color: Int, targetWord: String): SpannableString{
        val span = SpannableString(text)
        val start = text.indexOf(targetWord)
        val end = start + targetWord.length
        span.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }
}