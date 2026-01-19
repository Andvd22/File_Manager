package com.example.mylearning.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylearning.adapter.Language2Adapter
import com.example.mylearning.data.Language2Data
import com.example.mylearning.databinding.ActivityLanguage2Binding
import com.example.mylearning.model.Language2Item
import java.util.Locale

class LanguageActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityLanguage2Binding
    private lateinit var adapter: Language2Adapter

    private var languages = mutableListOf<Language2Item>()
    private var selectedId: String? = null

    // SharedPreferences
    private val prefs by lazy { getSharedPreferences("language_prefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Khởi tạo hệ thống & Binding
        enableEdgeToEdge()
        binding = ActivityLanguage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Thực thi theo đúng thứ tự yêu cầu
        setupViews()        // Thiết lập View tĩnh
        setupAdapter()      // Khởi tạo đối tượng Adapter (Quan trọng: phải chạy trước setupRecyclerView)
        setupRecyclerView() // Cấu hình danh sách
        setupData()         // Load dữ liệu
        setupListener()     // Gán các sự kiện (Search, Click, Done)
        setupUpdateUi()     // Cập nhật giao diện sau cùng
    }

    // ==========================================
    // 1. Setup Views tĩnh
    // ==========================================
    private fun setupViews() {
        // Hiện tại để trống hoặc gán các Text tĩnh nếu cần
        // Ví dụ: binding.tvTitle.text = "Language"
    }

    // ==========================================
    // 2. Setup Adapter (Khởi tạo biến lateinit)
    // ==========================================
    private fun setupAdapter() {
        adapter = Language2Adapter { clicked ->
            handleLanguageSelection(clicked.id)
        }
    }

    // ==========================================
    // 3. Setup RecyclerView
    // ==========================================
    private fun setupRecyclerView() {
        binding.rvLanguages.apply {
            layoutManager = LinearLayoutManager(this@LanguageActivity2)
            setHasFixedSize(true)
            adapter = this@LanguageActivity2.adapter // Gán adapter đã khởi tạo ở bước 2
        }
    }

    // ==========================================
    // 4. Load Data
    // ==========================================
    private fun setupData() {
        // 1. Lấy danh sách gốc
        val rawLanguages = Language2Data.getAllLanguages()

        // 2. Quyết định ID nào sẽ được chọn (ưu tiên đã lưu -> mặc định máy)
        val targetId = getSavedLanguage() ?: getSystemLanguageTag()
        selectedId = targetId

        // 3. Duyệt danh sách và gán trạng thái selected
        languages = rawLanguages.map { item ->
            // So sánh không phân biệt hoa thường để an toàn tuyệt đối
            item.copy(isSelected = item.id.equals(targetId, ignoreCase = true))
        }.toMutableList()

        // 4. Đẩy vào adapter
        adapter.submitList(languages)

        // 5. Cuộn tới vị trí đó
        scrollToSelected()
    }

    // ==========================================
    // 5. Setup Listeners (Search + Done)
    // ==========================================
    private fun setupListener() {
        // Xử lý Search
        binding.etSearch.doAfterTextChanged { text ->
            val keyword = text.toString().trim().lowercase()
            val filtered = if (keyword.isEmpty()) {
                languages
            } else {
                languages.filter { it.name.lowercase().contains(keyword) }
            }
            adapter.submitList(filtered.map { it.copy() })
        }

        // Xử lý Done
        binding.btnDone.setOnClickListener {
            onDoneClicked()
        }
    }

    // ==========================================
    // 6. Setup Update UI
    // ==========================================
    private fun setupUpdateUi() {
        // Cập nhật các thành phần UI khác dựa trên trạng thái dữ liệu hiện tại
    }

    // =========================
    // Logic bổ trợ (Giữ nguyên từ code của bạn)
    // =========================

//    private fun handleLanguageSelection(id: String) {
//        selectedId = id
//        languages.forEach { it.isSelected = it.id == id }
//        adapter.submitList(languages.map { it.copy() })
//    }

    private fun handleLanguageSelection(id: String) {
        selectedId = id

        val newList = languages.map {
            it.copy(isSelected = it.id == id)
        }

        languages = newList.toMutableList()
        adapter.submitList(newList)
    }


    private fun scrollToSelected() {
        val index = languages.indexOfFirst { it.id == selectedId }
        if (index >= 0) binding.rvLanguages.scrollToPosition(index)
    }

    private fun onDoneClicked() {
        val raw = selectedId ?: return
        saveSelectedLanguage(raw)
        val tag = if (raw == "system") "" else raw.replace('_', '-')
        val locales = LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(locales)
        // startActivity(Intent(this, SettingActivity::class.java))
//        finishAffinity()
    }

    private fun saveSelectedLanguage(value: String) = prefs.edit().putString("selected_language", value).apply()
    private fun getSavedLanguage(): String? = prefs.getString("selected_language", null)
    private fun getSystemLanguageTag(): String {
        val locale = Locale.getDefault()
        // Trả về định dạng chuẩn language_COUNTRY (ví dụ: vi_VN)
        val tag = "${locale.language}_${locale.country}"

        // Kiểm tra xem ID này có tồn tại trong Data không
        val exists = Language2Data.getAllLanguages().any { it.id.equals(tag, ignoreCase = true) }

        return if (exists) tag else "en_US" // Nếu không tìm thấy Tiếng Việt, mặc định chọn Tiếng Anh
    }
}