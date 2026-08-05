package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import com.handysparksoft.shakelamp.feature.flashlight.domain.ToggleFlashlightUseCase
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

    fun onAction(action: FlashlightUiAction) {
        when (action) {
            FlashlightUiAction.TogglePower -> togglePower()
        }
    }

    private fun togglePower() {
        val state = _uiState.value
        if (!state.isAvailable) return
        val newIsOn = !state.isOn
        viewModelScope.launch {
            if (toggleFlashlight(newIsOn)) {
                _uiState.update { it.copy(isOn = newIsOn) }
            } else {
                _uiEvent.emit(FlashlightUiEvent.ShowError("Couldn't toggle the flashlight"))
            }
        }
    }
}
