package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.compose.runtime.Immutable
import com.handysparksoft.shakelamp.core.morse.domain.MorseTimingDefaults

@Immutable
data class FlashlightUiState(
    val isOn: Boolean = false,
    val isAvailable: Boolean = true,
    /** 0 = off. */
    val timerMinutes: Int = 0,
    val morseMessage: String = "",
    val isLoopEnabled: Boolean = false,
    val isTransmitting: Boolean = false,
    /** In-memory only for now; real persistence lands with the Settings screen. */
    val morseSpeedWpm: Int = MorseTimingDefaults.DEFAULT_WPM,
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

    data object TransmitClicked : FlashlightUiAction

    // TODO: wire real widget configuration navigation once that screen exists.
    data object ConfigureWidgetClicked : FlashlightUiAction
}

sealed interface FlashlightUiEvent {
    data class ShowError(
        val message: String,
    ) : FlashlightUiEvent
}
