package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import com.handysparksoft.shakelamp.feature.flashlight.domain.ToggleFlashlightUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FlashlightViewModel(
    private val repository: FlashlightRepository,
    private val toggleFlashlight: ToggleFlashlightUseCase,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            FlashlightUiState(isAvailable = repository.isFlashAvailable()),
        )
    val uiState: StateFlow<FlashlightUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FlashlightUiEvent>()
    val uiEvent: SharedFlow<FlashlightUiEvent> = _uiEvent.asSharedFlow()

    private var autoOffJob: Job? = null

    fun onAction(action: FlashlightUiAction) {
        when (action) {
            FlashlightUiAction.TogglePower -> togglePower()
            is FlashlightUiAction.TimerChanged -> changeTimer(action.minutes)
            is FlashlightUiAction.MessageChanged -> {
                _uiState.update { it.copy(morseMessage = action.text) }
            }
            FlashlightUiAction.LoopToggled -> {
                _uiState.update { it.copy(isLoopEnabled = !it.isLoopEnabled) }
            }
            FlashlightUiAction.TransmitClicked -> Unit
            FlashlightUiAction.ConfigureWidgetClicked -> Unit
        }
    }

    private fun togglePower() {
        val state = _uiState.value
        if (!state.isAvailable) return
        val newIsOn = !state.isOn
        viewModelScope.launch {
            if (toggleFlashlight(newIsOn)) {
                _uiState.update { it.copy(isOn = newIsOn) }
                if (newIsOn) scheduleAutoOff(state.timerMinutes) else cancelAutoOff()
            } else {
                _uiEvent.emit(FlashlightUiEvent.ShowError("Couldn't toggle the flashlight"))
            }
        }
    }

    private fun changeTimer(minutes: Int) {
        _uiState.update { it.copy(timerMinutes = minutes) }
        if (_uiState.value.isOn) scheduleAutoOff(minutes)
    }

    private fun scheduleAutoOff(minutes: Int) {
        cancelAutoOff()
        if (minutes <= 0) return
        autoOffJob =
            viewModelScope.launch {
                delay(minutes * MILLIS_PER_MINUTE)
                if (toggleFlashlight(false)) {
                    _uiState.update { it.copy(isOn = false) }
                } else {
                    _uiEvent.emit(FlashlightUiEvent.ShowError("Couldn't turn off the flashlight"))
                }
            }
    }

    private fun cancelAutoOff() {
        autoOffJob?.cancel()
        autoOffJob = null
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
    }
}
