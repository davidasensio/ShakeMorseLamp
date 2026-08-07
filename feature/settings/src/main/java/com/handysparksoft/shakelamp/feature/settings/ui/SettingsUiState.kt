package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.runtime.Immutable
import com.handysparksoft.shakelamp.feature.settings.domain.DEFAULT_SENSITIVITY_LEVEL
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode

@Immutable
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val emergencyMessage: String = "SOS",
    val isStrobeActive: Boolean = false,
    val dimmerLevel: Int = 1,
    val dimmerMaxLevel: Int = 1,
    val isShakeEnabled: Boolean = false,
    val shakeSensitivity: Int = DEFAULT_SENSITIVITY_LEVEL,
    val shakeMode: ShakeMode = ShakeMode.NORMAL,
)

sealed interface SettingsUiAction {
    data class ThemeModeChanged(
        val mode: ThemeMode,
    ) : SettingsUiAction

    data class EmergencyMessageChanged(
        val text: String,
    ) : SettingsUiAction

    data object StrobeToggled : SettingsUiAction

    data class DimmerLevelChanged(
        val level: Int,
    ) : SettingsUiAction

    data object ShakeEnabledToggled : SettingsUiAction

    data class ShakeSensitivityChanged(
        val level: Int,
    ) : SettingsUiAction

    data class ShakeModeChanged(
        val mode: ShakeMode,
    ) : SettingsUiAction
}

sealed interface SettingsUiEvent {
    data object ShowMissingEmergencyMessageSnackbar : SettingsUiEvent
}
