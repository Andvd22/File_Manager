package com.example.mylearning.view

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.mylearning.R
import com.example.mylearning.adapter.FileAdapter
import com.example.mylearning.databinding.ActivityMainBinding
import com.example.mylearning.databinding.ActivitySecondBinding
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.util.PermissionHelper
import com.example.mylearning.viewmodel.FileViewModel
import kotlin.getValue

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var fileAdapter: FileAdapter
    private val viewModel: FileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionHelper = PermissionHelper(this)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners(){
        binding.etSearch.addTextChangedListener { text ->
            viewModel.updateFilterParams(query= text?.toString().orEmpty())
//            viewModel.updateQuery(text?.toString().orEmpty())
        }
        binding.goToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun observeViewModel(){
        viewModel.getFileByTypeAndQuery().observe(this) { files ->
            fileAdapter.submitList(files)
                when {
                    files.isEmpty() -> showEmptyState()
                    else -> showMainUI()
                }
        }
        viewModel.isLoading.observe(this) { loading ->
            if (loading) showLoadingState() else showMainUI()
        }
    }


    private fun setupRecyclerView(){
        fileAdapter = FileAdapter(
            onItemClick = { file -> handleFileClick(file)},
            onMoreClick = { file -> showFileOptions(file) }
        )
        binding.recyclerView.adapter = fileAdapter
    }

    private fun handleFileClick(file: FileModel) {
        if (file.isDirectory) {
            Toast.makeText(this, "Đây là folder: ${file.name}", Toast.LENGTH_SHORT).show()
        } else {
            openFile(file)
        }
    }
    private fun openFile(file: FileModel){
        try{
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file.file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getMimeType(file))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(intent)
        }catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_app_to_open, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getMimeType(file: FileModel): String {
        return when (file.extension) {
            "pdf" -> "application/pdf"
            "doc", "docx" -> "application/msword"
            "txt" -> "text/plain"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "mp4" -> "video/mp4"
            "mp3" -> "audio/mpeg"
            else -> "*/*"
        }
    }

    private fun showFileOptions(file: FileModel) {
        val options = arrayOf(
            getString(R.string.open),
            getString(R.string.share),
            getString(R.string.details),
            getString(R.string.delete)
        )

        AlertDialog.Builder(this)
            .setTitle(file.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openFile(file)
                    1 -> "shareFile(file)"
                    2 -> "showFileDetails(file)"
                    3 -> "confirmDeleteFile(file)"
                }
            }
            .show()
    }

    private fun showMainUI() {
//        binding.permissionLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
//        binding.fabScan.show()
    }

    private fun showEmptyState() {
//        binding.permissionLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.loadingLayout.visibility = View.GONE
//        binding.fabScan.show()
    }

    private fun showLoadingState() {
//        binding.permissionLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE
//        binding.fabScan.hide()
    }
}