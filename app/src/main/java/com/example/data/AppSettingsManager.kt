package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private const val KEY_DARK_THEME_ENABLED = "app_dark_theme_enabled"

    private val _isDarkThemeFlow = MutableStateFlow(false)
    val isDarkThemeFlow: StateFlow<Boolean> = _isDarkThemeFlow.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun init(context: Context) {
        val prefs = getPrefs(context)
        _isDarkThemeFlow.value = prefs.getBoolean(KEY_DARK_THEME_ENABLED, false)
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

    fun isDarkTheme(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(KEY_DARK_THEME_ENABLED, false)
    }

    fun setDarkTheme(context: Context, isDark: Boolean) {
        val prefs = getPrefs(context)
        prefs.edit().putBoolean(KEY_DARK_THEME_ENABLED, isDark).apply()
        _isDarkThemeFlow.value = isDark
    }

    fun toggleDarkTheme(context: Context): Boolean {
        val current = isDarkTheme(context)
        val newTheme = !current
        setDarkTheme(context, newTheme)
        return newTheme
    }
}
