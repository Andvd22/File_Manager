package com.example.mylearning.data

import android.content.Context

object LanguagePrefs {
    private const val PREFS_NAME = "language_prefs"
    private const val KEY_SELECTED_LANGUAGE = "selected_language"

    fun saveSelectedLanguage(context: Context, languageId: String){
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_SELECTED_LANGUAGE, languageId)
            .apply()
    }

    fun getSelectedLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_SELECTED_LANGUAGE, "system") ?: "system"
    }
}
