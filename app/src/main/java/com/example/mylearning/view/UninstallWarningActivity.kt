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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityUninstallWarningBinding

class UninstallWarningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUninstallWarningBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUninstallWarningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        styleTexts()
        setupListener()
    }

    private fun setupListener(){
        binding.btnConfirm.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnUnInstall.setOnClickListener {
            startActivity(Intent(this, ResultUninstallActivity::class.java))
        }
    }

    private fun styleTexts(){
        val textHighlights = getString(R.string.uninstall_warning_loss_highlight)
        val primaryColor = ContextCompat.getColor(this,R.color.primaryColor)
        val text = getString(R.string.uninstall_warning_title)
        var span = SpannableString(text)
        val start = text.indexOf(textHighlights)
        if(start != -1){
            val end = start + textHighlights.length
            span.setSpan(ForegroundColorSpan(primaryColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.tvTitle.text = span
    }
}