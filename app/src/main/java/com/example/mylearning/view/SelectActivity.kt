package com.example.mylearning.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivitySelectBinding
import com.example.mylearning.model.FileModel
import com.example.mylearning.viewmodel.FileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.getValue

class SelectActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectBinding

    override val viewModel: FileViewModel by viewModels()

    override val recyclerView: RecyclerView get() = binding.recyclerView
    override val emptyState: View get() = binding.emptyStateLayout
    override val loadingState: View get() = binding.loadingLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        setupListeners()
        setupBaseUI()
    }

    override fun handleFileClick(file: FileModel) {
        fileAdapter.toggleSelection(file)

        hideShowActionBar()
    }

    private fun hideShowActionBar(){
        val count = fileAdapter.selectedFiles.size
        if(count > 0){
            binding.layoutActions.visibility = View.VISIBLE
            binding.tvTitle.text = "Đã chọn ${count} mục"
        }else {
            binding.layoutActions.visibility = View.GONE
            binding.tvTitle.text = "Chọn tài liệu"
        }
    }

    private fun setupListeners(){
        binding.btnClose.setOnClickListener { finish() }
        binding.goToMain.setOnClickListener { finish() }
        binding.btnShareMultiple.setOnClickListener {
            shareSelectedFiles()
        }
        binding.btnDeleteMultiple.setOnClickListener {
            val count = fileAdapter.selectedFiles.size
            if(count==0) return@setOnClickListener

            MaterialAlertDialogBuilder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa $count mục đã chọn?")
                .setPositiveButton("Xóa"){_, _ ->
                    deleteSelectedFiles()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    private fun deleteSelectedFiles(){
        val filesToDelete = fileAdapter.selectedFiles.toList()
        if(filesToDelete.isEmpty()) return
        filesToDelete.forEach { file ->
            viewModel.deleteFile(file)
        }

        fileAdapter.clearSelection()
        hideShowActionBar()

        Toast.makeText(this, "Đã xóa ${filesToDelete.size} tệp", Toast.LENGTH_SHORT ).show()
    }

    private fun shareSelectedFiles(){
        val selectedList = fileAdapter.selectedFiles.toList()
        if(selectedList.isEmpty()) return
        try {
            val uris = ArrayList<Uri>()
            selectedList.forEach { file ->
                val uri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    file.file
                )
                uris.add(uri)
            }

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "*/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, "Chia sẻ các tệp đã chọn"))
        }catch (e: Exception){
            Toast.makeText(this, "Lỗi khi chia sẻ tệp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}