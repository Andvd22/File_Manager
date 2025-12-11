package com.example.mylearning.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityScreenshotBinding
import com.example.mylearning.databinding.DialogCommonActionBinding
import com.example.mylearning.databinding.DialogScreenshotOptionsBinding
import com.example.mylearning.databinding.DialogSortOptionBinding
import com.example.mylearning.model.FileModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File

class ScreenshotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenshotBinding
    private lateinit var fileModel: FileModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenshotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMore.setOnClickListener {
            showOptionsBottomSheet()
        }

        binding.btnRotate.setOnClickListener {
            // TODO rotate
        }

        binding.btnShare.setOnClickListener {
            // TODO share
        }
        binding.btnBack.setOnClickListener { finish() }

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent ?: return
        val path = intent.getStringExtra("extra_path")
        val uri = path?.toUri()
        binding.ivScreenshot.setImageURI(uri)
        val file = File(path)
        fileModel = FileModel(file)
    }

    private fun showOptionsBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val bindingDialog = DialogScreenshotOptionsBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        bindingDialog.tvFileName.text = fileModel.name

        bindingDialog.optionRename.setOnClickListener {
            dialog.dismiss()
            showCommonDialog(ScreenshotAction.RENAME)
        }

        bindingDialog.optionDetails.setOnClickListener {
            dialog.dismiss()
            showCommonDialog(ScreenshotAction.DETAILS)
        }

        bindingDialog.optionDelete.setOnClickListener {
            dialog.dismiss()
            showCommonDialog(ScreenshotAction.DELETE)
        }

        dialog.show()
    }

    private fun showCommonDialog(action: ScreenshotAction) {
        val dialog = BottomSheetDialog(this)
        val binding = DialogCommonActionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        when (action) {
            ScreenshotAction.RENAME -> {
                binding.tvTitle.text = "Rename"
                binding.btnConfirm.text = "Rename"
                val input = EditText(this)
                input.hint = "Enter new name"
                binding.contentContainer.addView(input)
            }

            ScreenshotAction.DETAILS -> {
                binding.tvTitle.text = "Details"
                binding.btnConfirm.text = "Close"

                val tv = TextView(this)
                tv.text = "File Name: ${fileModel.name}\nStorage path: ${fileModel.path}\nLast viewed: ${fileModel.getFormattedDate()}\nFile size: ${fileModel.getFormattedSize()}"
                binding.contentContainer.addView(tv)
            }

            ScreenshotAction.DELETE -> {
                binding.tvTitle.text = "Delete"
                binding.btnConfirm.text = "Delete"

                val tv = TextView(this)
                tv.text = "Are you sure you want to delete this screenshot?"
                binding.contentContainer.addView(tv)
            }
        }
        binding.btnConfirm.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}
enum class ScreenshotAction {
    RENAME,
    DETAILS,
    DELETE
}

