package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handysparksoft.shakelamp.core.common.domain.TorchBrightnessRepository
import com.handysparksoft.shakelamp.core.morse.domain.MorseTimingDefaults
import com.handysparksoft.shakelamp.core.morse.domain.SendMorseMessageUseCase
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ThemePreferenceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val themePreferenceRepository: ThemePreferenceRepository,
    private val sendMorseMessage: SendMorseMessageUseCase,
    private val torchBrightnessRepository: TorchBrightnessRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(SettingsUiState(dimmerMaxLevel = torchBrightnessRepository.maxStrengthLevel()))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var strobeJob: Job? = null

    init {
        viewModelScope.launch {
            themePreferenceRepository.observeThemeMode().collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            torchBrightnessRepository.observeStrengthLevel().collect { level ->
                _uiState.update { it.copy(dimmerLevel = level) }
            }
        }
    }

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.ThemeModeChanged -> changeThemeMode(action.mode)
            is SettingsUiAction.EmergencyMessageChanged -> {
                _uiState.update { it.copy(emergencyMessage = action.text) }
            }
            SettingsUiAction.StrobeToggled -> toggleStrobe()
            is SettingsUiAction.DimmerLevelChanged -> changeDimmerLevel(action.level)
        }
    }

    private fun changeThemeMode(mode: ThemeMode) {
        viewModelScope.launch { themePreferenceRepository.setThemeMode(mode) }
    }

    private fun changeDimmerLevel(level: Int) {
        viewModelScope.launch { torchBrightnessRepository.setStrengthLevel(level) }
    }

    private fun toggleStrobe() {
        if (_uiState.value.isStrobeActive) stopStrobe() else startStrobe()
    }

    private fun startStrobe() {
        val message = _uiState.value.emergencyMessage
        if (message.isBlank()) return
        _uiState.update { it.copy(isStrobeActive = true) }
        strobeJob =
            viewModelScope.launch {
                do {
                    sendMorseMessage(message, MorseTimingDefaults.DEFAULT_WPM)
                } while (_uiState.value.isStrobeActive && isActive)
            }
    }

    private fun stopStrobe() {
        strobeJob?.cancel()
        strobeJob = null
        _uiState.update { it.copy(isStrobeActive = false) }
    }
}
