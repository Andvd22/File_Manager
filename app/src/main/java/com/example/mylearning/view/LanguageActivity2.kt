package com.example.mylearning.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityLanguage2Binding

class LanguageActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityLanguage2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLanguage2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}