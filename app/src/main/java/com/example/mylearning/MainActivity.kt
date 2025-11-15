package com.example.mylearning

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.mylearning.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var fileAdapter: FileAdapter

    private var allFiles = listOf<FileModel>()
    private var currentFileType = FileType.ALL
    private var scanJob: Job? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionHelper = PermissionHelper(this)
        setupRecyclerView()
        setupListeners()
        checkPermissionAndProceed()
    }

    private fun setupRecyclerView(){
        fileAdapter = FileAdapter(
            onItemClick = {},
            onMoreClick = {file -> showFileOptions(file)}
        )
        binding.recyclerView.adapter = fileAdapter
    }

    private fun setupListeners(){
        binding.btnGrantPermission.setOnClickListener {
            requestPermission()
        }

        binding.btnLater.setOnClickListener {
            permissionHelper.setUserSaidLater(true)
            finish()
        }

        binding.fabScan.setOnClickListener {
            if(permissionHelper.hasStoragePermission()){
                scanFiles()
            } else {
                checkPermissionAndProceed()
            }
        }

        binding.etSearch.addTextChangedListener{ text ->
            filterFiles(text?.toString()?:"")
        }

        binding.chipAll.setOnClickListener {
            currentFileType = FileType.ALL
            applyFilter()
        }
        binding.chipDocument.setOnClickListener {
            currentFileType = FileType.DOCUMENT
            applyFilter()
        }
        binding.chipImage.setOnClickListener {
            currentFileType = FileType.IMAGE
            applyFilter()
        }
        binding.chipVideo.setOnClickListener {
            currentFileType = FileType.VIDEO
            applyFilter()
        }
        binding.chipAudio.setOnClickListener {
            currentFileType = FileType.AUDIO
            applyFilter()
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
        scanJob?.cancel()
        showLoadingState()
        scanWithCoroutines()
    }

    private fun scanWithCoroutines(){
        Log.d(TAG,"Bắt đầu quét file với COROUTINES")
        scanJob = lifecycleScope.launch {
            try {
                val files = FileScanner.scanFileWithCoroutine(
                    fileType = currentFileType,
                    onProgress = { file ->
                        //optional: update ui
                    }
                )

                onScanComplete(files)
            }catch (e: Exception){
                Log.e(TAG, "Lỗi khi quét file: ${e.message}",e)
//                showErrow("Lỗi: ${e.message}")
            }
        }
    }

    private fun onScanComplete(files: List<FileModel>){
        allFiles = files
        fileAdapter.submitList(files)
        if(files.isEmpty()){
            showEmptyState()
        }else{
            showMainUI()
            Toast.makeText(this, getString(R.string.files_found,files.size), Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterFiles(query: String) {
        val filtered = if (query.isEmpty()) {
            allFiles
        } else {
            allFiles.filter { file ->
                file.name.contains(query, ignoreCase = true)
            }
        }

        fileAdapter.submitList(filtered)

        if (filtered.isEmpty()) {
            showEmptyState()
        } else {
            showMainUI()
        }
    }

    private fun applyFilter() {
        if (!permissionHelper.hasStoragePermission()) {
            Toast.makeText(this, "Vui lòng cấp quyền trước khi lọc file!", Toast.LENGTH_SHORT).show()
            return
        }

        showLoadingState()
        scanFiles()
    }

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
                deleteFile(file)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteFile(file: FileModel) {
        try {
            if (file.file.delete()) {
                allFiles = allFiles.filter { it.path != file.path }
                fileAdapter.submitList(allFiles)

                Snackbar.make(binding.root, R.string.file_deleted, Snackbar.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.file_delete_error, Toast.LENGTH_SHORT).show()
            }
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

    private fun showError(message: String) {
        showEmptyState()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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
            checkPermissionAndProceed()
        } else {
            showPermissionUI()
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionHelper.hasStoragePermission() && allFiles.isEmpty()) {
            checkPermissionAndProceed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanJob?.cancel()
    }
}