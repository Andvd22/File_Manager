package com.example.mylearning.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityScreenshotBinding
import com.example.mylearning.databinding.DialogCommonActionBinding
import com.example.mylearning.databinding.DialogScreenshotOptionsBinding
import com.example.mylearning.databinding.DialogSortOptionBinding
import com.example.mylearning.model.FileModel
import com.example.mylearning.viewmodel.FileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File

class ScreenshotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenshotBinding
    private lateinit var fileModel: FileModel
    private val viewModel: FileViewModel by viewModels()
    private var currentRotation = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenshotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMore.setOnClickListener {
            showOptionsBottomSheet()
        }

        binding.btnRotate.setOnClickListener {
            rotateImage()
        }

        binding.btnShare.setOnClickListener {
            shareImage()
        }
        binding.btnBack.setOnClickListener { finish() }

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent ?: return
        val path = intent.getStringExtra("extra_path")
        if (path == null) {
            Toast.makeText(this, "Không có đường dẫn file", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val uri = path.toUri()
        binding.ivScreenshot.setImageURI(uri)
        val file = File(path)
        if (!file.exists()) {
            Toast.makeText(this, "File không tồn tại", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        fileModel = FileModel(file)
    }

    private fun rotateImage(){
        Toast.makeText(this, "Xoay ${fileModel.path}, ${fileModel.name}", Toast.LENGTH_SHORT).show()

        currentRotation = (currentRotation + 90) % 360
        binding.ivScreenshot.rotation = currentRotation.toFloat()
    }

    private fun shareImage(){
        try{
            Toast.makeText(this, "Chia sẻ ${fileModel.path}, ${fileModel.name}", Toast.LENGTH_SHORT).show()
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", fileModel.file)
            val intent = Intent(Intent.ACTION_SEND).apply{
                type = getMimeType()
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent,"Chia sẻ ảnh"))
        }catch (e: Exception){
            Toast.makeText(this, "Lỗi khi chia sẻ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeType(): String{
        val extension = fileModel.extension.lowercase()
        return when(extension){
            "jpg","jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "image/*"
        }
    }

    private fun showOptionsBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val bindingDialog = DialogScreenshotOptionsBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        bindingDialog.tvFileName.text = fileModel.name

        bindingDialog.optionRename.setOnClickListener {
            dialog.dismiss()
            showRenameDialog()
        }

        bindingDialog.optionDetails.setOnClickListener {
            dialog.dismiss()
            showDetailsDialog()
        }

        bindingDialog.optionDelete.setOnClickListener {
            dialog.dismiss()
            showDeleteDialog()
        }

        dialog.show()
    }

    private fun showRenameDialog() {
        val input = EditText(this).apply{
            val nameWithoughtExtension = if(fileModel.extension.isNotEmpty()){
                fileModel.name.substringBeforeLast(".")
                }else {
                    fileModel.name
                }
                setText(nameWithoughtExtension)
                hint = "Nhập tên mới"
                setSelectAllOnFocus(true)
            }

        MaterialAlertDialogBuilder(this)
            .setTitle("Đổi tên")
            .setMessage("Nhập tên mới cho file")
            .setView(input)
            .setPositiveButton("Đổi tên"){ dialog, _ ->
                val newName = input.text.toString().trim()
                if(newName.isEmpty()){
                    Toast.makeText(this, "Tên file không được để trống", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                lifecycleScope.launch{
                    try{
                        val renamedFile = viewModel.renameFile(fileModel, newName)
                        fileModel = renamedFile
                        val newUri = fileModel.file.toUri()
                        binding.ivScreenshot.setImageURI(newUri)
//                        binding.tvFileName.text = fileModel.name
                        Toast.makeText(this@ScreenshotActivity, "Đổi tên thành công", Toast.LENGTH_SHORT).show()
                    }catch (e: Exception){
                        Toast.makeText(this@ScreenshotActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showDetailsDialog(){
        val message = buildString{
            append("Tên file: ${fileModel.name}\n\n")
            append("Đường dẫn: ${fileModel.path}\n\n")
            append("Kích thước: ${fileModel.getFormattedSize()}\n\n")
            append("Loại file: ${fileModel.extension.uppercase()}\n\n")
            append("Ngày sửa đổi: ${fileModel.getFormattedDate()}")
        }
        MaterialAlertDialogBuilder(this)
            .setTitle("Thông tin file")
            .setMessage(message)
            .setPositiveButton("Đóng", null)
            .show()
    }

    private fun showDeleteDialog(){
        MaterialAlertDialogBuilder(this)
            .setTitle("Xóa file")
            .setMessage("Bạn có chắc chắn muốn xóa file này không? Hành động này không thể hoàn tác.")
            .setPositiveButton("Xóa"){ dialog,_ ->
                lifecycleScope.launch{
                    try{
                        viewModel.deleteFile(fileModel)
                        Toast.makeText(this@ScreenshotActivity, "Đã xóa file: ${fileModel.name} thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    }catch(e: Exception){
                        Toast.makeText(this@ScreenshotActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}

