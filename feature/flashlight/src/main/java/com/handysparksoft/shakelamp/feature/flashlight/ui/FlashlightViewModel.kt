package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handysparksoft.shakelamp.core.common.domain.HapticFeedbackRepository
import com.handysparksoft.shakelamp.core.common.domain.LoopPauseRepository
import com.handysparksoft.shakelamp.core.common.domain.TransmissionSpeedRepository
import com.handysparksoft.shakelamp.core.morse.domain.SendMorseMessageUseCase
import com.handysparksoft.shakelamp.feature.flashlight.domain.AutoOffServiceController
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import com.handysparksoft.shakelamp.feature.flashlight.domain.MorseHistoryRepository
import com.handysparksoft.shakelamp.feature.flashlight.domain.ToggleFlashlightUseCase
import com.handysparksoft.shakelamp.feature.flashlight.domain.WidgetPinRequester
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
class FlashlightViewModel(
    private val repository: FlashlightRepository,
    private val toggleFlashlight: ToggleFlashlightUseCase,
    private val sendMorseMessage: SendMorseMessageUseCase,
    private val historyRepository: MorseHistoryRepository,
    private val autoOffServiceController: AutoOffServiceController,
    private val hapticFeedbackRepository: HapticFeedbackRepository,
    private val loopPauseRepository: LoopPauseRepository,
    private val transmissionSpeedRepository: TransmissionSpeedRepository,
    private val widgetPinRequester: WidgetPinRequester,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            FlashlightUiState(isAvailable = repository.isFlashAvailable()),
        )
    val uiState: StateFlow<FlashlightUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FlashlightUiEvent>()
    val uiEvent: SharedFlow<FlashlightUiEvent> = _uiEvent.asSharedFlow()

    private var transmitJob: Job? = null

    init {
        viewModelScope.launch {
            historyRepository.observeHistory().collect { history ->
                _uiState.update { it.copy(sentMessageHistory = history) }
            }
        }
        viewModelScope.launch {
            hapticFeedbackRepository.observeEnabled().collect { enabled ->
                _uiState.update { it.copy(isHapticFeedbackEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            loopPauseRepository.observePauseMillis().collect { millis ->
                _uiState.update { it.copy(loopPauseMillis = millis) }
            }
        }
        viewModelScope.launch {
            transmissionSpeedRepository.observeSpeedWpm().collect { wpm ->
                _uiState.update { it.copy(morseSpeedWpm = wpm) }
            }
        }
        viewModelScope.launch {
            repository.observeTorchState().collect { isOn ->
                _uiState.update { it.copy(isOn = isOn) }
            }
        }
        viewModelScope.launch {
            autoOffServiceController.observeRemainingMillis().collect { remaining ->
                _uiState.update { it.copy(autoOffRemainingMillis = remaining) }
                // The service already turned the torch off directly; a still-alive transmit
                // loop would otherwise flip it back on at its next dot/dash.
                if (remaining == 0L && _uiState.value.isTransmitting) {
                    transmitJob?.cancel()
                    transmitJob = null
                }
            }
        }
    }

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
            FlashlightUiAction.TransmitClicked -> toggleTransmission()
            FlashlightUiAction.HistoryToggled -> {
                _uiState.update { it.copy(isHistoryExpanded = !it.isHistoryExpanded) }
            }
            FlashlightUiAction.ConfigureWidgetClicked -> requestConfigureWidget()
        }
    }

    private fun togglePower() {
        val state = _uiState.value
        // A Morse transmission already owns the torch; ignore manual toggles until it's
        // done (or explicitly stopped) instead of racing two independent torch callers.
        if (!state.isAvailable || state.isTransmitting) return
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
        val state = _uiState.value
        // A running single-shot transmission ends on its own shortly; don't saddle it with
        // a timeout. Manual power-on and a looping transmission (no natural end) both want
        // the countdown rescheduled against the new duration.
        val appliesHere = state.isOn && (!state.isTransmitting || state.isLoopEnabled)
        if (appliesHere) scheduleAutoOff(minutes)
    }

    private fun scheduleAutoOff(minutes: Int) {
        if (minutes <= 0) {
            cancelAutoOff()
            return
        }
        autoOffServiceController.start(minutes)
    }

    private fun cancelAutoOff() {
        autoOffServiceController.stop()
    }

    private fun toggleTransmission() {
        if (_uiState.value.isTransmitting) {
            stopTransmission()
        } else {
            startTransmission()
        }
    }

    private fun startTransmission() {
        val state = _uiState.value
        if (!state.isAvailable || state.morseMessage.isBlank()) return

        if (state.isLoopEnabled && state.timerMinutes > 0) {
            // A loop has no natural end — let the configured auto-off duration cap it,
            // same as it would the manual power button.
            scheduleAutoOff(state.timerMinutes)
        } else {
            // A single-shot transmission ends on its own; don't let a stale timer cut it short.
            cancelAutoOff()
        }
        _uiState.update { it.copy(isTransmitting = true, isOn = true) }
        viewModelScope.launch { historyRepository.addMessage(state.morseMessage) }

        transmitJob =
            viewModelScope.launch {
                try {
                    do {
                        sendMorseMessage(state.morseMessage, state.morseSpeedWpm)
                        val loopingAgain = _uiState.value.isLoopEnabled && isActive
                        if (loopingAgain) delay(_uiState.value.loopPauseMillis)
                    } while (_uiState.value.isLoopEnabled && isActive)
                } finally {
                    _uiState.update { it.copy(isTransmitting = false, isOn = false) }
                }
            }
    }

    private fun stopTransmission() {
        cancelAutoOff()
        transmitJob?.cancel()
        transmitJob = null
    }

    private fun requestConfigureWidget() {
        if (widgetPinRequester.isSupported()) {
            widgetPinRequester.requestPin()
        } else {
            viewModelScope.launch { _uiEvent.emit(FlashlightUiEvent.ShowWidgetPinInstructions) }
        }
    }
}
