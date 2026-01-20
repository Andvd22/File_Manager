package com.example.mylearning.data

import com.example.mylearning.R
import com.example.mylearning.model.Language2Item

object Language2Data {
    private val languages = listOf(
        // Thêm tiếng Việt vào danh sách
        Language2Item("vi_VN", "Tiếng Việt",englishName = "Vietnamese", R.drawable.language_activity_flag_system),

        Language2Item("en_UK", "English (UK)",englishName = "English (United Kingdom)", R.drawable.language_activity_flag_en_uk),
        Language2Item("en_US", "English (US)",englishName = "English (United States)", R.drawable.language_activity_flag_en_us),
        Language2Item("fr", "Français",  englishName = "French",R.drawable.language_activity_flag_france),
        Language2Item("ko", "한국어", englishName = "Korean", R.drawable.language_activity_flag_korea),
        Language2Item("ja", "日本語", englishName = "Japanese",R.drawable.language_activity_flag_japan),
        Language2Item("id", "Indonesia",    englishName = "Indonesian",R.drawable.language_activity_flag_indo),
        Language2Item("zh", "Español",englishName = "Spanish", R.drawable.language_activity_flag_espanha)
    )

    fun getAllLanguages(): List<Language2Item> {
        return languages
    }
}