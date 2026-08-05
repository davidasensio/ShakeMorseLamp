package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.compose.runtime.Immutable

@Immutable
data class FlashlightUiState(
    val isOn: Boolean = false,
    val isAvailable: Boolean = true,
)

sealed interface FlashlightUiAction {
    data object TogglePower : FlashlightUiAction
}

sealed interface FlashlightUiEvent {
    data class ShowError(
        val message: String,
    ) : FlashlightUiEvent
}
