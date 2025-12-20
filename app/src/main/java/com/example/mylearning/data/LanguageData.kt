package com.example.mylearning.data

import com.example.mylearning.R
import com.example.mylearning.model.LanguageItem

object LanguageData {
    private val englishChildren: List<LanguageItem.Child> = listOf(
        LanguageItem.Child(
            id = "en_UK",
            name = "English (UK)",
            flagRes = R.drawable.language_activity_flag_en_uk,
            parentId = "en"
        ),
        LanguageItem.Child(
            id = "en_US",
            name = "English US",
            flagRes = R.drawable.language_activity_flag_en_us,
            parentId = "en"
        ),
        LanguageItem.Child(
            id = "en_CA",
            name = "English (Canada)",
            flagRes = R.drawable.language_activity_flag_en_canada,
            parentId = "en"
        )
    )

    private val parents: List<LanguageItem.Parent> = listOf(
        LanguageItem.Parent(
            id = "en",
            name = "English",
            flagRes = R.drawable.language_activity_flag_en_uk,
            hasChildren = true
        ),
        LanguageItem.Parent(
            id = "fr",
            name = "Français",
            flagRes = R.drawable.language_activity_flag_france,
            hasChildren = false 
        ),
        LanguageItem.Parent(
            id = "ko",
            name = "한국어",
            flagRes = R.drawable.language_activity_flag_korea,
            hasChildren = false
        ),
        LanguageItem.Parent(
            id = "ja",
            name = "日本語",
            flagRes = R.drawable.language_activity_flag_japan,
            hasChildren = false
        ),
        LanguageItem.Parent(
            id = "id",
            name = "Indonesia",
            flagRes = R.drawable.language_activity_flag_indo,
            hasChildren = false
        ),
        LanguageItem.Parent(
            id = "zh",
            name = "Español",
            flagRes = R.drawable.language_activity_flag_espanha,
            hasChildren = false
        )
    )

    fun getInitialList(): List<LanguageItem> {
        return listOf(LanguageItem.System()) + parents
    }

    fun getChildrenOf(parentId: String): List<LanguageItem.Child> {
        return when(parentId){
            "en" -> englishChildren
            else -> emptyList()
        }
    }

    fun getAllItems(): List<LanguageItem> {
        return listOf(LanguageItem.System()) + parents + englishChildren
    }
}