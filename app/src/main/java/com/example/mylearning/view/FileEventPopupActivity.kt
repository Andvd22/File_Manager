package com.example.mylearning.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityFileEventPopupBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileEventPopupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFileEventPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFileEventPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        val event = intent.getStringExtra(EXTRA_EVENT).orEmpty()
        val fullPath = intent.getStringExtra(EXTRA_PATH).orEmpty()

        binding.includeNotification.ivIcon.setImageResource(R.drawable.bg_bottom_sheet1)
        binding.includeNotification.tvTitle.text = "File $event"
        binding.includeNotification.tvDescription.text = fullPath
        binding.includeNotification.tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Nút đóng
        binding.btnClose.setOnClickListener {
            finishWithoutAnimation()
        }

        // Auto-dismiss sau 3 giây (tuỳ chỉnh)
        lifecycleScope.launch {
            delay(3000)
            if (!isFinishing) {
                finishWithoutAnimation()
            }
        }

        // Tắt animation chuyển Activity cho mượt như heads-up
        overridePendingTransition(0, 0)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun finishWithoutAnimation() {
        finish()
        overridePendingTransition(0, 0)
    }


    companion object {
        const val EXTRA_EVENT = "extra_event"
        const val EXTRA_PATH = "extra_path"
    }
}