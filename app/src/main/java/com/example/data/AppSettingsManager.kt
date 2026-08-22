package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.compositionLocalOf

enum class AppFontSize(val displayName: String, val scaleFactor: Float, val label: String) {
    PEQUENO("Pequeño", 1.0f, "A Pequeño"),
    MEDIANO("Mediano", 1.15f, "A Mediano"),
    GRANDE("Grande", 1.30f, "A Grande")
}

val LocalAppFontScale = compositionLocalOf { 1.15f }
val LocalAppFontSize = compositionLocalOf { AppFontSize.MEDIANO }

object AppSettingsManager {
    private const val PREFS_NAME = "asistente_electoral_settings_prefs"
    private const val KEY_FONT_SIZE = "app_font_size_setting"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getAppFontSize(context: Context): AppFontSize {
        val prefs = getPrefs(context)
        val savedName = prefs.getString(KEY_FONT_SIZE, AppFontSize.MEDIANO.name)
        return try {
            AppFontSize.valueOf(savedName ?: AppFontSize.MEDIANO.name)
        } catch (e: Exception) {
            AppFontSize.MEDIANO
        }
    }

    fun setAppFontSize(context: Context, fontSize: AppFontSize) {
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_FONT_SIZE, fontSize.name).apply()
    }
}
