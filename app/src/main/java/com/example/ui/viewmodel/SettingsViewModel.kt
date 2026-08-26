package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.repository.SettingsRepository
import com.example.domain.model.GameSettings
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)
    val settings: StateFlow<GameSettings> = repository.settingsFlow

    fun setYasaEnabled(enabled: Boolean) {
        repository.updateYasa(enabled)
    }

    fun setTargetScores(withoutJoker: Int, withJoker: Int) {
        repository.updateTargetScores(withoutJoker, withJoker)
    }

    fun setDarkMode(isDark: Boolean?) {
        repository.updateDarkMode(isDark)
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        repository.updateAnimations(enabled)
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        repository.updateSoundEffects(enabled)
    }

    fun resetDefaults() {
        repository.resetToDefaults()
    }
}
