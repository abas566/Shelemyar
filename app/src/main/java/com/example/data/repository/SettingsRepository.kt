package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.model.GameSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("shelemyar_settings", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<GameSettings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): GameSettings {
        val yasa = prefs.getBoolean(KEY_YASA, true)
        val targetWithout = prefs.getInt(KEY_TARGET_WITHOUT, 165)
        val targetWith = prefs.getInt(KEY_TARGET_WITH, 200)
        val darkVal = if (prefs.contains(KEY_DARK_MODE)) prefs.getBoolean(KEY_DARK_MODE, true) else null
        val anim = prefs.getBoolean(KEY_ANIMATIONS, true)
        val sound = prefs.getBoolean(KEY_SOUND, true)

        return GameSettings(
            yasaEnabled = yasa,
            targetScoreWithoutJoker = targetWithout,
            targetScoreWithJoker = targetWith,
            isDarkMode = darkVal,
            animationsEnabled = anim,
            soundEffectsEnabled = sound
        )
    }

    fun updateYasa(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_YASA, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(yasaEnabled = enabled)
    }

    fun updateTargetScores(withoutJoker: Int, withJoker: Int) {
        prefs.edit()
            .putInt(KEY_TARGET_WITHOUT, withoutJoker)
            .putInt(KEY_TARGET_WITH, withJoker)
            .apply()
        _settingsFlow.value = _settingsFlow.value.copy(
            targetScoreWithoutJoker = withoutJoker,
            targetScoreWithJoker = withJoker
        )
    }

    fun updateDarkMode(isDark: Boolean?) {
        val editor = prefs.edit()
        if (isDark == null) {
            editor.remove(KEY_DARK_MODE)
        } else {
            editor.putBoolean(KEY_DARK_MODE, isDark)
        }
        editor.apply()
        _settingsFlow.value = _settingsFlow.value.copy(isDarkMode = isDark)
    }

    fun updateAnimations(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(animationsEnabled = enabled)
    }

    fun updateSoundEffects(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(soundEffectsEnabled = enabled)
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        _settingsFlow.value = GameSettings()
    }

    companion object {
        private const val KEY_YASA = "pref_yasa"
        private const val KEY_TARGET_WITHOUT = "pref_target_without"
        private const val KEY_TARGET_WITH = "pref_target_with"
        private const val KEY_DARK_MODE = "pref_dark_mode"
        private const val KEY_ANIMATIONS = "pref_animations"
        private const val KEY_SOUND = "pref_sound"
    }
}
