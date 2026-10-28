package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.SavageShieldStyle
import com.example.ui.theme.FXThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fx_security_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(readThemeMode())
    val themeMode: StateFlow<FXThemeMode> = _themeMode.asStateFlow()

    private val _savageStyle = MutableStateFlow(readSavageStyle())
    val savageStyle: StateFlow<SavageShieldStyle> = _savageStyle.asStateFlow()

    private val _savagePopupEnabled = MutableStateFlow(prefs.getBoolean(KEY_SAVAGE_POPUP, true))
    val savagePopupEnabled: StateFlow<Boolean> = _savagePopupEnabled.asStateFlow()

    private val _aggressiveSpamFilter = MutableStateFlow(prefs.getBoolean(KEY_AGGRESSIVE_SPAM, true))
    val aggressiveSpamFilter: StateFlow<Boolean> = _aggressiveSpamFilter.asStateFlow()

    private val _autoScanEnabled = MutableStateFlow(prefs.getBoolean(KEY_AUTO_SCAN, true))
    val autoScanEnabled: StateFlow<Boolean> = _autoScanEnabled.asStateFlow()

    private val _notifyOnThreats = MutableStateFlow(prefs.getBoolean(KEY_NOTIFY_THREATS, true))
    val notifyOnThreats: StateFlow<Boolean> = _notifyOnThreats.asStateFlow()

    private fun readThemeMode(): FXThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, FXThemeMode.FX_HYBRID.name)
        return try {
            FXThemeMode.valueOf(name ?: FXThemeMode.FX_HYBRID.name)
        } catch (_: Exception) {
            FXThemeMode.FX_HYBRID
        }
    }

    private fun readSavageStyle(): SavageShieldStyle {
        val name = prefs.getString(KEY_SAVAGE_STYLE, SavageShieldStyle.SAVAGE.name)
        return try {
            SavageShieldStyle.valueOf(name ?: SavageShieldStyle.SAVAGE.name)
        } catch (_: Exception) {
            SavageShieldStyle.SAVAGE
        }
    }

    fun setThemeMode(mode: FXThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setSavageStyle(style: SavageShieldStyle) {
        prefs.edit().putString(KEY_SAVAGE_STYLE, style.name).apply()
        _savageStyle.value = style
    }

    fun setSavagePopupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SAVAGE_POPUP, enabled).apply()
        _savagePopupEnabled.value = enabled
    }

    fun setAggressiveSpamFilter(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AGGRESSIVE_SPAM, enabled).apply()
        _aggressiveSpamFilter.value = enabled
    }

    fun setAutoScanEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SCAN, enabled).apply()
        _autoScanEnabled.value = enabled
    }

    fun setNotifyOnThreats(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFY_THREATS, enabled).apply()
        _notifyOnThreats.value = enabled
    }

    companion object {
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_SAVAGE_STYLE = "key_savage_style"
        private const val KEY_SAVAGE_POPUP = "key_savage_popup"
        private const val KEY_AGGRESSIVE_SPAM = "key_aggressive_spam"
        private const val KEY_AUTO_SCAN = "key_auto_scan"
        private const val KEY_NOTIFY_THREATS = "key_notify_threats"
    }
}
