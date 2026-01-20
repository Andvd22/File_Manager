package com.example.mylearning.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylearning.adapter.Language2Adapter
import com.example.mylearning.data.Language2Data
import com.example.mylearning.databinding.ActivityLanguage2Binding
import com.example.mylearning.model.Language2Item
import java.util.Locale

class LanguageActivity2 : AppCompatActivity() {
    private lateinit var binding : ActivityLanguage2Binding
    private lateinit var  adapter: Language2Adapter
    private var languages = mutableListOf<Language2Item>()
    private var selectedId: String? = null
    private var fromSplash = false
    private val prefs by lazy { getSharedPreferences("language_prefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Khởi tạo hệ thống & Binding
        enableEdgeToEdge()
        binding = ActivityLanguage2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        fromSplash = intent.getBooleanExtra(SplashActivity.FROM_SPLASH, false)

        // 2. Thực thi theo đúng thứ tự yêu cầu
        setupViews()        // Thiết lập View tĩnh
        setupAdapter()      // Khởi tạo đối tượng Adapter (Quan trọng: phải chạy trước setupRecyclerView)
        setupRecyclerView() // Cấu hình danh sách
        setupData()         // Load dữ liệu
        setupListener()     // Gán các sự kiện (Search, Click, Done)
        setupUpdateUi()     // Cập nhật giao diện sau cùng
    }
    private fun setupViews() {
    }
    private fun setupAdapter(){
        adapter = Language2Adapter { item ->
            handleLanguageSelection(item.id)
        }
    }
    private fun setupRecyclerView() {
        binding.rvLanguages.apply {
            layoutManager = LinearLayoutManager(this@LanguageActivity2)
            setHasFixedSize(true)
            adapter = this@LanguageActivity2.adapter // Gán adapter đã khởi tạo ở bước 2
        }
    }
    private fun setupData(){
        val rawLanguages = Language2Data.getAllLanguages()
        val targetId = getSavedLanguage() ?: getSystemLanguageTag()
        selectedId = targetId
        languages = rawLanguages.map { it.copy(isSelected = it.id == targetId) }.toMutableList()
        adapter.submitList(languages)
        scrollToSelected()
    }
    private fun setupListener(){
        binding.etSearch.doAfterTextChanged { text ->
            val keyword = text.toString().trim().lowercase()
            val filtered = if(keyword.isEmpty()){
                languages
            } else {
                languages.filter {
                    it.name.lowercase().contains(keyword) ||
                            it.englishName.lowercase().contains(keyword)
                }
            }
            adapter.submitList(filtered)
        }

        binding.btnDone.setOnClickListener {
            onDoneClicked()
        }
    }

    private fun setupUpdateUi() {
    }

    private fun handleLanguageSelection(id: String){
        selectedId = id
        val newList = languages.map { it.copy(isSelected = it.id == id) }
        languages = newList.toMutableList()
        adapter.submitList(newList)
    }


    private fun scrollToSelected(){
        val index = languages.indexOfFirst { it.id == selectedId }
        if(index >= 0) binding.rvLanguages.scrollToPosition(index)
    }

    private fun onDoneClicked(){
        val raw = selectedId ?: return
        saveSelectedLanguage(raw)
        val tag = if(raw == "system") "" else raw.replace('_', '-')
        val locales = LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(locales)
        if(fromSplash){
            startActivity(Intent(this, RequestAllFileActivity::class.java))
            fromSplash=false
        }
        finish()
    }

    private fun saveSelectedLanguage(value: String) = prefs.edit().putString("selected_language", value).apply()
    private fun getSavedLanguage(): String? = prefs.getString("selected_language", null)
    private fun getSystemLanguageTag(): String{
        val locale = Locale.getDefault()
        val tag = "${locale.language}_${locale.country}"
        val exists = Language2Data.getAllLanguages().any(){it.id == tag}
        return if(exists) tag else "en_US"
    }
}