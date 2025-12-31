package com.example.mylearning.view

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mylearning.R
import com.example.mylearning.adapter.FeatureRequestItemAdapter
import com.example.mylearning.databinding.ActivityFeatureRequestBinding
import com.example.mylearning.databinding.DialogScreenshotOptionsBinding
import com.example.mylearning.databinding.FeatureRequestBottomDialogBinding
import com.example.mylearning.viewmodel.FeatureRequestViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class FeatureRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeatureRequestBinding
    private val viewModel: FeatureRequestViewModel by viewModels()

    private val smartAdapter = FeatureRequestItemAdapter { viewModel.toggleTool(it.id) }
    private val aiAdapter = FeatureRequestItemAdapter{ toolItem ->
        viewModel.toggleTool(toolItem.id) }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeatureRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setupRecycler()
        observeViewModel()
        setupInput()
        setupListener()

        window.insetsController?.let {
            it.hide(WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
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
        binding.ivIconBack.setOnClickListener {
            finish()
        }
    }

    private fun submit() {
        val selectedTitles = viewModel.getSelectedToolTitles()
            .joinToString(", ") { getString(it) } +
        (if(!viewModel.getSelectedToolTitles().isEmpty()&&!viewModel.otherText.value.isNullOrBlank()) ", " else "") +
        (if(!viewModel.otherText.value.isNullOrBlank()) viewModel.otherText.value.toString() else "")

        Toast.makeText(this, selectedTitles, Toast.LENGTH_LONG).show()
        showOptionsBottomSheet()
    }

    private fun setupListener(){
    }

    private fun showOptionsBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val bindingDialog = FeatureRequestBottomDialogBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        bindingDialog.btnSubmit.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}