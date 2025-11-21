package com.example.mylearning.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.mylearning.R
import com.example.mylearning.adapter.FileAdapter
import com.example.mylearning.model.FileModel
import com.example.mylearning.viewmodel.FileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.security.acl.Owner

abstract class BaseActivity : AppCompatActivity(){
    protected abstract val viewModel: FileViewModel
    protected lateinit var fileAdapter: FileAdapter

    protected abstract val recyclerView: RecyclerView
    protected abstract val emptyState: View
    protected abstract val loadingState: View

    protected open val permissionLayout: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun setupBaseUI() {
        setupAdapter()
        observeFiles(this)
    }

    private fun setupAdapter(){
        fileAdapter = FileAdapter(
            onItemClick = { file -> handleFileClick(file)},
            onMoreClick = { file -> showFileOptions(file) }
        )
        recyclerView.adapter = fileAdapter
    }

    private fun observeFiles(owner: LifecycleOwner) {
        viewModel.getFileByTypeAndQuery().observe(owner, Observer { files ->
            fileAdapter.submitList(files)
            when{
                files.isEmpty() -> showEmptyState()
                else -> showMainUI()
            }
        })

        viewModel.isLoading.observe(owner) {isLoading ->
            when{
                isLoading -> showLoadingState()
                else -> showMainUI()
            }
        }
    }

    protected open fun handleFileClick(file: FileModel) {
        if (file.isDirectory) {
            Toast.makeText(this, "Đây là folder: ${file.name}", Toast.LENGTH_SHORT).show()
        } else {
            openFile(file)
        }
    }

    protected fun openFile(file: FileModel) {
        try {
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
        } catch (e: ActivityNotFoundException){
            Toast.makeText(this, R.string.no_app_to_open, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    protected open fun showFileOptions(file: FileModel){
        val options = arrayOf(
            getString(R.string.open),
            getString(R.string.share),
            getString(R.string.details),
            getString(R.string.delete)
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(file.name)
            .setItems(options){_, which ->
                when(which){
                    0 -> openFile(file)
                    1 -> shareFile(file)
                    2 -> showFileDetails(file)
                    3 -> confirmDeleteFile(file)
                }
            }
            .show()
    }

    private fun shareFile(file: FileModel) {
        try {
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
        } catch (e: Exception) {
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

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.file_details)
            .setMessage(message)
            .setPositiveButton(R.string.open) { _, _ -> openFile(file) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun confirmDeleteFile(file: FileModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_file)
            .setMessage(getString(R.string.delete_file_message))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteFile(file)
                Snackbar.make(recyclerView, R.string.file_deleted, Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    protected open fun getMimeType(file: FileModel): String {
        val extension = file.extension.lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
    }

    protected open fun showMainUI() {
        permissionLayout?.visibility = View.GONE
        loadingState.visibility = View.GONE
        emptyState.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    protected open fun showEmptyState() {
        permissionLayout?.visibility = View.GONE
        loadingState.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyState.visibility = View.VISIBLE
    }

    protected open fun showLoadingState() {
        permissionLayout?.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE
        loadingState.visibility = View.VISIBLE
    }
}