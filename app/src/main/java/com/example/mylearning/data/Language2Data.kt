package com.example.mylearning.data

import com.example.mylearning.R
import com.example.mylearning.model.Language2Item

object Language2Data {
    private val languages = listOf(
        // Thêm tiếng Việt vào danh sách
        Language2Item("vi_VN", "Tiếng Việt", R.drawable.language_activity_flag_system),

        Language2Item("en_UK", "English (UK)", R.drawable.language_activity_flag_en_uk),
        Language2Item("en_US", "English (US)", R.drawable.language_activity_flag_en_us),
        Language2Item("fr", "Français", R.drawable.language_activity_flag_france),
        Language2Item("ko", "한국어", R.drawable.language_activity_flag_korea),
        Language2Item("ja", "日本語", R.drawable.language_activity_flag_japan),
        Language2Item("id", "Indonesia", R.drawable.language_activity_flag_indo),
        Language2Item("zh", "Español", R.drawable.language_activity_flag_espanha)
    )

    fun getAllLanguages(): List<Language2Item> {
        return languages
    }
}