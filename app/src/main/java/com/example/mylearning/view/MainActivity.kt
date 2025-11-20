package com.example.mylearning.view

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.room.util.query
import com.example.mylearning.adapter.FileAdapter
import com.example.mylearning.util.PermissionHelper
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityMainBinding
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.viewmodel.FileViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var fileAdapter: FileAdapter
    private val viewModel: FileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionHelper = PermissionHelper(this)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        checkPermissionAndProceed()
    }

    private fun setupRecyclerView(){
        fileAdapter = FileAdapter(
            onItemClick = { file -> handleFileClick(file)},
            onMoreClick = { file -> showFileOptions(file) }
        )
        binding.recyclerView.adapter = fileAdapter
    }

    private fun setupListeners(){
        binding.btnGrantPermission.setOnClickListener { requestPermission() }

        binding.btnLater.setOnClickListener {
            permissionHelper.setUserSaidLater(true)
            showPermissionUI()
        }

        binding.fabScan.setOnClickListener {
            if(permissionHelper.hasStoragePermission()){
                scanFiles()
            } else {
                checkPermissionAndProceed()
            }
        }

        binding.goToSecond.setOnClickListener {
            if(permissionHelper.hasStoragePermission())
            {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)}
            else {
                showPermissionUI()
                Toast.makeText(this, "Cần cấp quyền để sang màn 2", Toast.LENGTH_SHORT).show()
            }
        }

        binding.chipAll.setOnClickListener { viewModel.updateFilterParams(FileType.ALL)}
        binding.chipDocument.setOnClickListener {viewModel.updateFilterParams(FileType.DOCUMENT) }
        binding.chipImage.setOnClickListener {viewModel.updateFilterParams(FileType.IMAGE) }
        binding.chipVideo.setOnClickListener {viewModel.updateFilterParams(FileType.VIDEO) }
        binding.chipAudio.setOnClickListener {viewModel.updateFilterParams(FileType.AUDIO)}

        binding.etSearch.setOnClickListener {
//            viewModel.updateFilterParams(query= text?.toString().orEmpty())
            if(permissionHelper.hasStoragePermission())
            {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)}
            else {
                showPermissionUI()
                Toast.makeText(this, "Cần cấp quyền để tìm kiếm", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel(){
            viewModel.getFileByTypeAndQuery().observe(this) { files ->
                fileAdapter.submitList(files)

                if (permissionHelper.hasStoragePermission()) {
                    when {
                        files.isEmpty() -> showEmptyState()
                        else -> showMainUI()
                    }
                } else showPermissionUI()

            }

            viewModel.isLoading.observe(this) { loading ->
                if (loading) showLoadingState() else showMainUI()
            }
    }

    private fun checkPermissionAndProceed(){
        when {
            permissionHelper.hasStoragePermission() -> {
                showMainUI()
                scanFiles()
            }
            permissionHelper.isUserSaidLater() -> {
                showPermissionUI()
            }
            else -> {
                showPermissionUI()
            }
        }
    }

    private fun requestPermission(){
        when {
            permissionHelper.shouldShowRationale() -> {
                showRationaleDialog()
            }
            else -> {
                permissionHelper.requestManageStoragePermission()
            }
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.permission_denied_forever)
            .setPositiveButton(R.string.open_settings){ _, _, ->
                permissionHelper.openAppSettings()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun scanFiles() {
        if(!permissionHelper.hasStoragePermission()) {
            showPermissionUI()
            Toast.makeText(this,"Cần cấp quyền để quét file", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.refreshFiles()
    }

//    private fun applyFilter(type: FileType) {
//        if (!permissionHelper.hasStoragePermission()) {
//            Toast.makeText(this, "Vui lòng cấp quyền trước khi lọc file!", Toast.LENGTH_SHORT).show()
//            return
//        }
//        viewModel.refresh(type)
//    }

    private fun handleFileClick(file: FileModel) {
        if (file.isDirectory) {
            Toast.makeText(this, "Đây là folder: ${file.name}", Toast.LENGTH_SHORT).show()
        } else {
            openFile(file)
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
                    1 -> shareFile(file)
                    2 -> showFileDetails(file)
                    3 -> confirmDeleteFile(file)
                }
            }
            .show()
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

    private fun shareFile(file: FileModel){
        try{
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file.file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = getMimeType(file)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }catch (e: Exception) {
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFileDetails(file: FileModel) {
        val message = buildString {
            append("${getString(R.string.name)} ${file.name}\n\n")
            append("${getString(R.string.path)} ${file.path}\n\n")
            append("${getString(R.string.size)} ${file.getFormattedSize()}\n\n")
            append("${getString(R.string.type)} ${file.extension}\n\n")
            append("${getString(R.string.last_modified)} ${file.getFormattedDate()}")
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.file_details)
            .setMessage(message)
            .setPositiveButton(R.string.open) { _, _ -> openFile(file) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun confirmDeleteFile(file: FileModel) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_file)
            .setMessage(getString(R.string.delete_file_message))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteFile(file)
                Snackbar.make(binding.root, R.string.file_deleted, Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
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

    //showUI
    private fun showPermissionUI() {
        binding.permissionLayout.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
        binding.fabScan.hide()
    }

    private fun showMainUI() {
        binding.permissionLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
        binding.fabScan.show()
    }

    private fun showEmptyState() {
        binding.permissionLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.loadingLayout.visibility = View.GONE
        binding.fabScan.show()
    }

    private fun showLoadingState() {
        binding.permissionLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE
        binding.fabScan.hide()
    }

    //Lifecycle callbacks
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val granted = permissionHelper.handlePermissionResult(requestCode, permissions, grantResults)

        if (granted) {
            checkPermissionAndProceed()
        } else {
            if (permissionHelper.shouldShowRationale()) {
                showPermissionUI()
            } else {
                showRationaleDialog()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val hasPermission = permissionHelper.handleActivityResult(requestCode)

        if (hasPermission) {
            showMainUI()
            scanFiles()
        } else {
            showPermissionUI()
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionHelper.hasStoragePermission() && binding.recyclerView.visibility != View.VISIBLE) {
            checkPermissionAndProceed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}