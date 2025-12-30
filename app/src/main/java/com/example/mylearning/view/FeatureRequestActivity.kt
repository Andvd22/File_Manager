package com.example.mylearning.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mylearning.R
import com.example.mylearning.adapter.FeatureRequestItemAdapter
import com.example.mylearning.databinding.ActivityFeatureRequestBinding
import com.example.mylearning.viewmodel.FeatureRequestViewModel

class FeatureRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeatureRequestBinding
    private val viewModel: FeatureRequestViewModel by viewModels()

    private val smartAdapter = FeatureRequestItemAdapter { viewModel.toggleTool(it.id) }
    private val aiAdapter = FeatureRequestItemAdapter{ toolItem ->
        viewModel.toggleTool(toolItem.id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeatureRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setupRecycler()
        observeViewModel()
        setupInput()
    }

    private fun setupRecycler(){
        binding.rvSmartTools.apply {
            layoutManager = GridLayoutManager(this@FeatureRequestActivity, 2)
            adapter = smartAdapter
        }
        binding.rvAiTools.apply {
            layoutManager = GridLayoutManager(this@FeatureRequestActivity, 2)
            adapter = aiAdapter
        }
    }

    private fun observeViewModel(){
        viewModel.smartTools.observe(this){ smartTools ->
            smartAdapter.submitList(smartTools)
        }
        viewModel.aiTools.observe(this){
            aiAdapter.submitList(it)
        }
        viewModel.isSubmitEnable.observe(this){
            binding.btnSubmit.isEnabled = it
        }
    }

    private fun setupInput(){
        binding.etDetail.addTextChangedListener {
            viewModel.setOtherText(it.toString())
        }

        binding.btnSubmit.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        val selectedTitles = viewModel.getSelectedToolTitles()
            .joinToString(", ") { getString(it) } +
        (if(!viewModel.getSelectedToolTitles().isEmpty()&&!viewModel.otherText.value.isNullOrBlank()) ", " else "") +
        (if(!viewModel.otherText.value.isNullOrBlank()) viewModel.otherText.value.toString() else "")

        Toast.makeText(this, selectedTitles, Toast.LENGTH_LONG).show()
    }
}