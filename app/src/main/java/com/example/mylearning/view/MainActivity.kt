package com.example.mylearning.view

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query
import com.example.mylearning.adapter.FileAdapter
import com.example.mylearning.util.PermissionHelper
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityMainBinding
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.model.FilterAndSortParams
import com.example.mylearning.model.SortCriteria
import com.example.mylearning.model.SortOrder
import com.example.mylearning.service.FileWatchService
import com.example.mylearning.viewmodel.FileViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHelper: PermissionHelper
    // 2. Implement các biến Abstract từ BaseActivity
    // Sử dụng "get() =" để luôn lấy view mới nhất từ binding
    override val viewModel: FileViewModel by viewModels()
    override val recyclerView: RecyclerView get() = binding.recyclerView
    override val emptyState: View get() = binding.emptyStateLayout
    override val loadingState: View get() = binding.loadingLayout
    override val permissionLayout: View get() = binding.permissionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)

        permissionHelper = PermissionHelper(this)
        setupBaseUI()
        setupListeners()
        checkPermissionAndProceed()
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
        binding.goSelectAct.setOnClickListener {
            if(permissionHelper.hasStoragePermission())
            {
                val intent = Intent(this, SelectActivity::class.java)
                startActivity(intent)}
            else {
                showPermissionUI()
                Toast.makeText(this, "Cần cấp quyền để sang màn select", Toast.LENGTH_SHORT).show()
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
        binding.chipSearch.setOnClickListener {
            val currentParams = viewModel.filterParams.value ?: FilterAndSortParams()

            val dialog = SortBottomSheetDialog(
                currentCriteria = currentParams.sortCriteria,
                currentOrder = currentParams.sortOrder,
                onSortSelected = {criteria, order ->
                    viewModel.updateFilterParams(
                        sortCriteria = criteria, sortOrder = order, isSortMode = true
                    )
                }
            )
            dialog.show(supportFragmentManager,"SortBottomSheetDialog")
        }

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

    override fun handleFileClick(file: FileModel) {
        Toast.makeText(this,"${file.name}", Toast.LENGTH_SHORT).show()
    }

    override fun showFileOptions(file: FileModel) {
        Toast.makeText(this,"${file.name}", Toast.LENGTH_SHORT).show()
    }
// check prmission helper and proceedAfterAllPermissions
    private fun checkPermissionAndProceed(){
        when {
            !permissionHelper.hasStoragePermission() -> {
                if (permissionHelper.isUserSaidLater()) {
                    showPermissionUI()
                } else {
                    showPermissionUI()
                }
            }
            !permissionHelper.hasNotificationPermission() -> {
                permissionHelper.requestNotificationPermission()
            }
            else -> {
                proceedAfterAllPermissionsGranted()
            }
        }
    }

    private fun proceedAfterAllPermissionsGranted() {
        scanFiles()
        startFileWatchService()
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

    //showUI
    private fun showPermissionUI() {
        binding.permissionLayout.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
        binding.fabScan.hide()
    }

    override fun showMainUI() {
        if (permissionHelper.hasStoragePermission()) {
            super.showMainUI() // Base hiện Recycler, ẩn Empty/Loading
            binding.fabScan.show()
        } else {
            showPermissionUI()
        }
    }

    override fun showEmptyState() {
        if (permissionHelper.hasStoragePermission()) {
            super.showEmptyState()
            binding.fabScan.show()
        } else {
            showPermissionUI()
        }
    }

    override fun showLoadingState() {
        super.showLoadingState()
        binding.fabScan.hide()
    }

    //Lifecycle callbacks
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
// check
        when (requestCode) {
            PermissionHelper.REQUEST_CODE_STORAGE -> {
                val granted = permissionHelper.handlePermissionResult(
                    requestCode,
                    permissions,
                    grantResults
                )

                if (granted) {
                    checkPermissionAndProceed()
                } else if (permissionHelper.shouldShowRationale()) {
                    showPermissionUI()
                } else {
                    showRationaleDialog()
                }
            }
            PermissionHelper.REQUEST_CODE_NOTIFICATION -> {
                val granted = permissionHelper.handleNotificationPermissionResult(
                    requestCode,
                    grantResults
                )
                if (granted) {
                    proceedAfterAllPermissionsGranted()
                } else {
                    Toast.makeText(
                        this,
                        "Cần cấp quyền thông báo để nhận cảnh báo thay đổi file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
// filewatchservice
    private fun startFileWatchService(){
        val intent = Intent(this, FileWatchService::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent)
        }else{
            startService(intent)
        }
    }

    private fun stopFileWatchService(){
        val intent = Intent(this, FileWatchService::class.java)
        stopService(intent)
    }
}