package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.compose.runtime.Immutable

@Immutable
data class FlashlightUiState(
    val isOn: Boolean = false,
    val isAvailable: Boolean = true,
    /** 0 = off. */
    val timerMinutes: Int = 0,
    val morseMessage: String = "",
    val isLoopEnabled: Boolean = false,
)

sealed interface FlashlightUiAction {
    data object TogglePower : FlashlightUiAction

    data class TimerChanged(
        val minutes: Int,
    ) : FlashlightUiAction

    data class MessageChanged(
        val text: String,
    ) : FlashlightUiAction

    data object LoopToggled : FlashlightUiAction

    // TODO: wire real Morse transmission once the Morse codebase is provided.
    data object TransmitClicked : FlashlightUiAction

    // TODO: wire real widget configuration navigation once that screen exists.
    data object ConfigureWidgetClicked : FlashlightUiAction
}

sealed interface FlashlightUiEvent {
    data class ShowError(
        val message: String,
    ) : FlashlightUiEvent
}
