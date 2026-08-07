package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handysparksoft.shakelamp.core.common.domain.TorchBrightnessRepository
import com.handysparksoft.shakelamp.core.morse.domain.MorseTimingDefaults
import com.handysparksoft.shakelamp.core.morse.domain.SendMorseMessageUseCase
import com.handysparksoft.shakelamp.feature.settings.domain.EmergencyMessageRepository
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeServiceController
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeSettingsRepository
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ThemePreferenceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val emergencyMessageRepository: EmergencyMessageRepository,
    private val shakeSettingsRepository: ShakeSettingsRepository,
    private val shakeServiceController: ShakeServiceController,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(SettingsUiState(dimmerMaxLevel = torchBrightnessRepository.maxStrengthLevel()))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent: SharedFlow<SettingsUiEvent> = _uiEvent.asSharedFlow()

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
        viewModelScope.launch {
            emergencyMessageRepository.observeMessage().collect { message ->
                _uiState.update { it.copy(emergencyMessage = message) }
            }
        }
        viewModelScope.launch {
            shakeSettingsRepository.observeSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        isShakeEnabled = settings.enabled,
                        shakeSensitivity = settings.sensitivityLevel,
                        shakeMode = settings.mode,
                    )
                }
            }
        }
    }

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.ThemeModeChanged -> changeThemeMode(action.mode)
            is SettingsUiAction.EmergencyMessageChanged -> changeEmergencyMessage(action.text)
            SettingsUiAction.StrobeToggled -> toggleStrobe()
            is SettingsUiAction.DimmerLevelChanged -> changeDimmerLevel(action.level)
            SettingsUiAction.ShakeEnabledToggled -> toggleShakeEnabled()
            is SettingsUiAction.ShakeSensitivityChanged -> changeShakeSensitivity(action.level)
            is SettingsUiAction.ShakeModeChanged -> changeShakeMode(action.mode)
        }
    }

    private fun changeThemeMode(mode: ThemeMode) {
        viewModelScope.launch { themePreferenceRepository.setThemeMode(mode) }
    }

    private fun changeDimmerLevel(level: Int) {
        viewModelScope.launch { torchBrightnessRepository.setStrengthLevel(level) }
    }

    private fun changeEmergencyMessage(text: String) {
        _uiState.update { it.copy(emergencyMessage = text) }
        viewModelScope.launch { emergencyMessageRepository.setMessage(text) }
    }

    private fun changeShakeSensitivity(level: Int) {
        viewModelScope.launch { shakeSettingsRepository.setSensitivity(level) }
    }

    private fun toggleShakeEnabled() {
        val enabling = !_uiState.value.isShakeEnabled
        viewModelScope.launch { shakeSettingsRepository.setEnabled(enabling) }
        if (enabling) shakeServiceController.start() else shakeServiceController.stop()
    }

    private fun changeShakeMode(mode: ShakeMode) {
        if (mode == ShakeMode.EMERGENCY && _uiState.value.emergencyMessage.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(SettingsUiEvent.ShowMissingEmergencyMessageSnackbar) }
        }
        viewModelScope.launch { shakeSettingsRepository.setMode(mode) }
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
