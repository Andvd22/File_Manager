package com.example.mylearning.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylearning.adapter.LanguageAdapter
import com.example.mylearning.databinding.ActivityLanguageBinding
import com.example.mylearning.viewmodel.LanguageViewModel

class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var adapter: LanguageAdapter
    private val viewModel: LanguageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setupRecyclerView()
        setupObservers()
        setupSearchListener()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = LanguageAdapter { viewModel.onLanguageClicked(it) }
        binding.rvLanguages.apply {
            layoutManager = LinearLayoutManager(this@LanguageActivity)
            setHasFixedSize(true)
            adapter = this@LanguageActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.languages.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun setupSearchListener() {
        binding.etSearch.doAfterTextChanged {
            viewModel.onSearchQueryChanged(it?.toString().orEmpty())
        }
    }

    private fun setupClickListeners() {
        binding.btnDone.setOnClickListener {
            finish()
        }
    }
}