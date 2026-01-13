package com.example.mylearning.view

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityClearDefaultGuideBinding

class ClearDefaultGuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClearDefaultGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityClearDefaultGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListener()
        styleTexts()
    }

    private fun setupListener(){
        binding.tvBtnSend.setOnClickListener {
            finish()
        }
    }

    private fun styleTexts(){
        val setDefault = getString(R.string.set_as_default_label)
        val clearDefault = getString(R.string.clear_defaults_label)
        val backSymbol = getString(R.string.back_symbol)

        val step1 = getString(R.string.clear_default_step1, setDefault)
        val step2 = getString(R.string.clear_default_step2, clearDefault)
        val step3 = getString(R.string.clear_default_step3, backSymbol)

        val primaryColor = getColor(R.color.primaryColor)


        binding.tvStep1.text = colorize(step1, primaryColor, setDefault)
        binding.tvStep2.text = colorize(step2, primaryColor, clearDefault)
        binding.tvStep3.text = colorize(step3, primaryColor, backSymbol)
    }

    private fun colorize(text: String, color: Int, targetWord: String): SpannableString{
        val span = SpannableString(text)
        val start = text.indexOf(targetWord)
        val end = start + targetWord.length
        span.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }
}