package com.example.mylearning.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.mylearning.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.mylearning.databinding.DialogSetAsDefault2Binding
import java.io.File

class SetAsDefaultBottomDialog2 : BottomSheetDialogFragment(){
    private lateinit var binding: DialogSetAsDefault2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSetAsDefault2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        styleTexts()
    }

    private fun setupClickListeners(){
        binding.btnConfirm.setOnClickListener {
            openDefaultChooser()
        }
    }

    /**
     * Mở chooser hệ thống để người dùng chọn app mặc định (Mở bằng / Luôn chọn).
     * Tạo một file PDF tạm trong cache và dùng FileProvider (đã khai báo sẵn) để lấy content://.
     */
    private fun openDefaultChooser() {
        val ctx = context ?: return
        try {
            // 1) Tạo file PDF mẫu trong cache (đường dẫn đã cho phép trong file_paths.xml)
            //val tmpFile = File(ctx.cacheDir, "sample_default.pdf").apply
            val tmpFile = File(ctx.cacheDir, "sample_default.pptx").apply {
                if (!exists()) {
                    //writeText("%PDF-1.4\n% temp\n")
                    writeBytes(ByteArray(0))
                }
            }

            // 2) Lấy content uri qua FileProvider
            val uri = FileProvider.getUriForFile(
                ctx,
                "${ctx.packageName}.fileprovider",
                tmpFile
            )

            // 3) Bắn ACTION_VIEW trực tiếp để hệ thống hiện sheet “Chỉ một lần / Luôn chọn”
            val intent = Intent(Intent.ACTION_VIEW).apply {
                //setDataAndType(uri, "application/pdf")
                setDataAndType(uri,"application/vnd.openxmlformats-officedocument.presentationml.presentation")
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                // ClipData giúp chia sẻ quyền đọc cho mọi target nhận intent
               // clipData = android.content.ClipData.newRawUri("temp_pdf", uri)
                clipData = android.content.ClipData.newRawUri("temp_pptx", uri)
            }

            startActivity(intent)
            dismiss()
        } catch (e: Exception) {
            Toast.makeText(ctx, "Không mở được PowerPoint: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun styleTexts() {
        val appName = getString(R.string.set_default2_app_name)
        val always = getString(R.string.set_default2_always)

        val step1 = getString(R.string.set_default2_step1, appName)
        val step2 = getString(R.string.set_default2_step2, always)
        val summary = getString(R.string.set_default2_summary, appName, always)

        val primary = requireContext().getColor(R.color.primaryColor)


        binding.tvStep1.text = colorize(step1, primary, appName)      // TextView cho dòng step1
        binding.tvStep2.text = colorize(step2, primary, always)       // TextView cho dòng step2
        binding.tvContent.text = colorize(summary, primary, appName, always)// TextView summary // nhãn dưới icon
        binding.tvAlwaysLabel.text = colorize(always, primary, always)        // nhãn "Always" góc phải
    }

    // Hàm tô màu tất cả occurrences của các targetWords trong text
    private fun colorize(text: String, color: Int, vararg targetWords: String): SpannableString{
        val span = SpannableString(text)
        targetWords.forEach { targetWord ->
            var start = text.indexOf(targetWord)
            while (start>=0){
                var end = start + targetWord.length
                span.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                start = text.indexOf(targetWord, end)
            }
        }
        return span
    }

    companion object {
        const val TAG = "SetAsDefaultBottomDialog2"
    }
}