package com.example.mbkz

import android.content.Context
import org.json.JSONObject
import java.io.IOException

object LanguageManager {
    private const val PREFS_NAME = "app_prefs"
    private const val LANGUAGE_KEY = "language"

    fun loadLanguage(context: Context, languageCode: String): Map<String, String> {
        val fileName = "strings_$languageCode.json"
        return try {
            val inputStream = context.assets.open(fileName)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            jsonObject.keys().asSequence().associateWith { jsonObject.getString(it) }
        } catch (e: IOException) {
            emptyMap()
        }
    }

    fun saveLanguagePreference(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_KEY, "en") ?: "en"
    }
}
