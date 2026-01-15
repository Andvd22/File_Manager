package com.example.mylearning.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.mylearning.databinding.ActivityResultUninstallBinding
import com.example.mylearning.databinding.ItemResultUninstallBinding

class ResultUninstallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultUninstallBinding
    private var listSelect: List<ItemResultUninstallBinding> = emptyList()

    // STATE
    private var currentIndex = -1
    private var hasMinChars = false
    private var selectReason = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultUninstallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupListeners()
        updateUI()
    }

    private fun setupViews() {
        binding.item1.tvContent.text = getString(com.example.mylearning.R.string.result_uninstall_reason1) // idx 0
        binding.item2.tvContent.text = getString(com.example.mylearning.R.string.result_uninstall_reason2) // idx 1
        binding.item3.tvContent.text = getString(com.example.mylearning.R.string.result_uninstall_reason3) // idx 2
        binding.item4.tvContent.text = getString(com.example.mylearning.R.string.result_uninstall_reason4) // idx 3

        listSelect = listOf(binding.item1, binding.item2, binding.item3, binding.item4)
    }

    private fun setupListeners() {
        binding.etDetail.doAfterTextChanged {
            hasMinChars = (binding.etDetail.text?.length ?: 0) >= 6
        }

        binding.btnCancel.setOnClickListener { finish() }
        binding.ivBack.setOnClickListener { finish() }

        listSelect.forEachIndexed { index, itemBinding ->
            itemBinding.root.setOnClickListener {
                // reset chọn
                listSelect.forEach {
                    it.ivNormal.visibility = View.VISIBLE
                    it.ivSelected.visibility = View.GONE
                    it.root.isSelected = false
                }
                currentIndex = index
                itemBinding.ivNormal.visibility = View.GONE
                itemBinding.ivSelected.visibility = View.VISIBLE
                itemBinding.root.isSelected = true
                selectReason = itemBinding.tvContent.text.toString()
                updateUI()
            }
        }

        binding.btnUnInstall.setOnClickListener {
            when (currentIndex) {
                2 -> { // lựa chọn thứ 3, không cần minChar
                    goToSettings()
                }
                0, 1, 3 -> { // cần >=6 ký tự
                    if (hasMinChars) {
                        goToSettings()
                    } else {
                        Toast.makeText(this, "Vui lòng nhập lý do ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    Toast.makeText(this, "Vui lòng chọn lý do", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI() {
        val enabled = currentIndex != -1
        binding.btnUnInstall.isEnabled = enabled
        binding.btnUnInstall.alpha = if (enabled) 1f else 0.5f
    }

    private fun goToSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
        Toast.makeText(this, selectReason, Toast.LENGTH_SHORT).show()
    }
}