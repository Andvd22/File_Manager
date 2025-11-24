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
import androidx.recyclerview.widget.RecyclerView
import com.example.mylearning.R
import com.example.mylearning.adapter.FileAdapter
import com.example.mylearning.databinding.ActivityMainBinding
import com.example.mylearning.databinding.ActivitySecondBinding
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.util.PermissionHelper
import com.example.mylearning.viewmodel.FileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.getValue

class SecondActivity : BaseActivity() {
//    private var hasWatchedAd = false
    private var hasWatchedAd = true
    private lateinit var binding: ActivitySecondBinding
    private lateinit var permissionHelper: PermissionHelper
    override val viewModel: FileViewModel by viewModels()

    override val recyclerView: RecyclerView get() = binding.recyclerView
    override val emptyState: View get() = binding.emptyStateLayout
    override val loadingState: View get() = binding.loadingLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)

        permissionHelper = PermissionHelper(this)
        setupListeners()
        setupBaseUI()
        showKeyboardAndFocus()
    }

    private fun setupListeners(){
        binding.etSearch.addTextChangedListener { text ->
            if (!hasWatchedAd && (text?.toString().orEmpty()).isNotEmpty()) {
//                // 1. Ẩn bàn phím ngay lập tức để họ không gõ tiếp được
//                hideKeyboard()
//                // 2. Xóa ký tự họ vừa gõ (để chặn nhập liệu) - Tùy chọn, nếu muốn gắt
//                binding.etSearch.setText("")
//                // 3. Mất focus khỏi ô nhập để không hiện lại bàn phím
//                binding.etSearch.clearFocus()
//                // 4. Hiện Dialog bắt xem quảng cáo
//                showAdDialog()
            } else{
            viewModel.updateFilterParams(query= text?.toString().orEmpty())
//            viewModel.updateQuery(text?.toString().orEmpty())
                }
        }
        binding.goToMain.setOnClickListener {
            finish()
        }

        binding.recyclerView.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.etSearch.clearFocus()
            false // Trả về false để sự kiện cuộn (scroll) vẫn hoạt động bình thường
        }

        // B. Khi chạm vào vùng trống (Empty State) nếu đang hiện -> Tắt phím
        binding.emptyStateLayout.setOnClickListener {
            hideKeyboard()
            binding.etSearch.clearFocus()
        }

        // C. Khi chạm vào vùng nền (Root layout) -> Tắt phím
        binding.root.setOnClickListener {
            hideKeyboard()
            binding.etSearch.clearFocus()
        }
    }

    private fun showKeyboardAndFocus() {
        // Dùng post để đảm bảo View đã được vẽ xong rồi mới request
        binding.etSearch.post {
            // 1. Yêu cầu focus vào ô nhập liệu
            binding.etSearch.requestFocus()

            // 2. Gọi trình quản lý bàn phím để hiện bàn phím lên
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(binding.etSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }













//
//    private fun showAdDialog() {
//        MaterialAlertDialogBuilder(this)
//            .setTitle("Yêu cầu xem quảng cáo")
//            .setMessage("Bạn cần xem một đoạn quảng cáo ngắn để mở khóa tính năng tìm kiếm.")
//            .setCancelable(false) // Không cho bấm ra ngoài để tắt
//            .setPositiveButton("Xem ngay") { _, _ ->
//                // Giả lập việc xem quảng cáo
//                mockPlayAd()
//            }
//            .setNegativeButton("Để sau") { dialog, _ ->
//                dialog.dismiss()
//                // Nếu từ chối, có thể clear text hoặc để yên tùy bạn
//            }
//            .show()
//    }
//
//    private fun mockPlayAd() {
//        Toast.makeText(this, "Đang phát quảng cáo...", Toast.LENGTH_SHORT).show()
//
//        // Giả lập sau 2 giây quảng cáo chạy xong
//        binding.root.postDelayed({
//            Toast.makeText(this, "Đã xem xong! Bạn có thể tìm kiếm.", Toast.LENGTH_LONG).show()
//
//            // QUAN TRỌNG: Đánh dấu đã xem để không bị hỏi lại
//            hasWatchedAd = true
//
//            // Tự động Focus lại vào ô nhập và hiện bàn phím cho người dùng tiện
//            showKeyboardAndFocus()
//
//        }, 2000)
//    }
}