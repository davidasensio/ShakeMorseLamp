package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.compose.runtime.Immutable
import com.handysparksoft.shakelamp.core.morse.domain.MorseTimingDefaults

@Immutable
data class FlashlightUiState(
    val isOn: Boolean = false,
    val isAvailable: Boolean = true,
    /** 0 = off. */
    val timerMinutes: Int = 0,
    /** Time left on the current auto-off countdown, or null when none is active. */
    val autoOffRemainingMillis: Long? = null,
    val morseMessage: String = "",
    val isLoopEnabled: Boolean = false,
    val isTransmitting: Boolean = false,
    val morseSpeedWpm: Int = MorseTimingDefaults.DEFAULT_WPM,
    val sentMessageHistory: List<String> = emptyList(),
    val isHistoryExpanded: Boolean = false,
    val isHapticFeedbackEnabled: Boolean = true,
    val loopPauseMillis: Long = 2_000L,
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

    data object HistoryToggled : FlashlightUiAction

    data object ConfigureWidgetClicked : FlashlightUiAction
}

sealed interface FlashlightUiEvent {
    data object ShowError : FlashlightUiEvent

    /** The launcher doesn't support one-tap widget placement - show manual instructions instead. */
    data object ShowWidgetPinInstructions : FlashlightUiEvent
}
