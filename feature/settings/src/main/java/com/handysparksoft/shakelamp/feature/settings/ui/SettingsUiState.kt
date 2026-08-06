package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.runtime.Immutable
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode

@Immutable
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val emergencyMessage: String = "SOS",
    val isStrobeActive: Boolean = false,
    val dimmerLevel: Int = 1,
    val dimmerMaxLevel: Int = 1,
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
}
