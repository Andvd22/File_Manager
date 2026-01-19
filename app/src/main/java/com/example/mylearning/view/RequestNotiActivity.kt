package com.example.mylearning.view

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityRequestAllFileBinding
import com.example.mylearning.databinding.ActivityRequestNotiBinding

class RequestNotiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestNotiBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestNotiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        styleTexts()
        setupClickListeners()
    }

    private fun setupClickListeners(){

    }

    private fun styleTexts(){
        val appName = getString(R.string.presentation_reader)

        val title = getString(R.string.request_noti_desc_1, appName)

        val primaryColor = getColor(R.color.primaryColor)


        binding.tvDesc1.text = colorize(title, primaryColor, appName)
    }

    private fun colorize(text: String, color: Int, targetWord: String): SpannableString{
        val span = SpannableString(text)
        val start = text.indexOf(targetWord)
        val end = start + targetWord.length
        span.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }
}