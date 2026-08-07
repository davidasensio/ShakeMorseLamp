package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.designsystem.component.SMLCard
import com.handysparksoft.shakelamp.core.designsystem.component.SMLOptionCard
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSegmentedButtonRow
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSegmentedOption
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSlider
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSwitch
import com.handysparksoft.shakelamp.core.designsystem.component.SMLTextField
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onAction: (SettingsUiAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        SettingsContent(
            uiState = uiState,
            onAction = onAction,
            onNavigateBack = onNavigateBack,
            onNavigateToAbout = onNavigateToAbout,
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(Spacing.Margin),
        )
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onAction: (SettingsUiAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val performHaptic: (HapticFeedbackType) -> Unit = {
        if (uiState.isHapticFeedbackEnabled) haptic.performHapticFeedback(it)
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.Gutter),
    ) {
        ScreenHeader(title = "Settings", onNavigateBack = onNavigateBack) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = "About",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(onClick = onNavigateToAbout),
            )
        }
        GesturesSection(uiState = uiState, onAction = onAction, performHaptic = performHaptic)
        EmergencyModeCard(
            message = uiState.emergencyMessage,
            isStrobeActive = uiState.isStrobeActive,
            onMessageChanged = { onAction(SettingsUiAction.EmergencyMessageChanged(it)) },
            onStrobeToggled = { onAction(SettingsUiAction.StrobeToggled) },
        )
        TransmissionCard(
            speedWpm = uiState.transmissionSpeedWpm,
            onSpeedChanged = {
                performHaptic(HapticFeedbackType.SegmentFrequentTick)
                onAction(SettingsUiAction.TransmissionSpeedChanged(it))
            },
            pauseMillis = uiState.loopPauseMillis,
            onPauseChanged = {
                performHaptic(HapticFeedbackType.SegmentFrequentTick)
                onAction(SettingsUiAction.LoopPauseChanged(it))
            },
        )
        HardwareCard(
            dimmerLevel = uiState.dimmerLevel,
            dimmerMaxLevel = uiState.dimmerMaxLevel,
            onDimmerLevelChanged = {
                performHaptic(HapticFeedbackType.SegmentFrequentTick)
                onAction(SettingsUiAction.DimmerLevelChanged(it))
            },
        )
        HapticsCard(
            isEnabled = uiState.isHapticFeedbackEnabled,
            onToggled = { onAction(SettingsUiAction.HapticFeedbackToggled) },
        )
        AppearanceCard(
            themeMode = uiState.themeMode,
            onThemeModeChanged = {
                performHaptic(HapticFeedbackType.SegmentTick)
                onAction(SettingsUiAction.ThemeModeChanged(it))
            },
        )
        AboutCard(onAboutClicked = onNavigateToAbout)
    }
}

@Composable
private fun GesturesSection(
    uiState: SettingsUiState,
    onAction: (SettingsUiAction) -> Unit,
    performHaptic: (HapticFeedbackType) -> Unit,
) {
    GesturesCard(
        state =
            GesturesUiState(
                isShakeEnabled = uiState.isShakeEnabled,
                shakeSensitivity = uiState.shakeSensitivity,
                shakeMode = uiState.shakeMode,
            ),
        onShakeEnabledToggled = {
            performHaptic(if (uiState.isShakeEnabled) HapticFeedbackType.ToggleOff else HapticFeedbackType.ToggleOn)
            onAction(SettingsUiAction.ShakeEnabledToggled)
        },
        onSensitivityChanged = {
            performHaptic(HapticFeedbackType.SegmentFrequentTick)
            onAction(SettingsUiAction.ShakeSensitivityChanged(it))
        },
        onShakeModeChanged = {
            performHaptic(HapticFeedbackType.SegmentTick)
            onAction(SettingsUiAction.ShakeModeChanged(it))
        },
    )
}

@Composable
private fun SectionLabel(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(text = text, style = MaterialTheme.typography.labelSmall, color = color, modifier = modifier)
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = titleColor)
            Spacer(Modifier.height(Spacing.S))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        SMLSwitch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = DIVIDER_ALPHA))
}

@Composable
private fun Badge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = BADGE_BACKGROUND_ALPHA))
                .padding(horizontal = Spacing.Unit, vertical = Spacing.Unit / 2),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primaryContainer,
        )
    }
}

private data class GesturesUiState(
    val isShakeEnabled: Boolean,
    val shakeSensitivity: Int,
    val shakeMode: ShakeMode,
)

@Composable
private fun GesturesCard(
    state: GesturesUiState,
    onShakeEnabledToggled: () -> Unit,
    onSensitivityChanged: (Int) -> Unit,
    onShakeModeChanged: (ShakeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(text = "GESTURES", color = MaterialTheme.colorScheme.primaryContainer)
        Spacer(Modifier.height(Spacing.Unit))
        SMLCard(modifier = Modifier.fillMaxWidth()) {
            SettingToggleRow(
                title = "Shake to Turn On",
                subtitle = "Double-shake to toggle the flashlight on or off",
                checked = state.isShakeEnabled,
                onCheckedChange = { onShakeEnabledToggled() },
            )
            Spacer(Modifier.height(Spacing.Gutter))
            SectionDivider()
            Spacer(Modifier.height(Spacing.Gutter))
            SensitivitySelector(sensitivity = state.shakeSensitivity, onSensitivityChanged = onSensitivityChanged)
            Spacer(Modifier.height(Spacing.Gutter))
            SectionDivider()
            Spacer(Modifier.height(Spacing.Gutter))
            ShakeModeSelector(mode = state.shakeMode, onModeChanged = onShakeModeChanged)
        }
    }
}

@Composable
private fun SensitivitySelector(
    sensitivity: Int,
    onSensitivityChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Sensor Sensitivity",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Badge(text = sensitivityLabel(sensitivity))
        }
        Spacer(Modifier.height(Spacing.Unit))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
        ) {
            SliderEndIcon(R.drawable.ic_mobile_vibrate)
            SMLSlider(
                value = sensitivity.toFloat(),
                onValueChange = { onSensitivityChanged(it.roundToInt()) },
                valueRange = MIN_SENSITIVITY..MAX_SENSITIVITY,
                steps = 1,
                modifier = Modifier.weight(1f),
            )
            SliderEndIcon(R.drawable.ic_mobile_vibrate)
        }
    }
}

@Composable
private fun ShakeModeSelector(
    mode: ShakeMode,
    onModeChanged: (ShakeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "When shaken",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(Spacing.Unit))
        SMLSegmentedButtonRow(
            options =
                listOf(
                    SMLSegmentedOption(ShakeMode.NORMAL, "Normal", painterResource(R.drawable.ic_flashlight_on)),
                    SMLSegmentedOption(ShakeMode.EMERGENCY, "Emergency", painterResource(R.drawable.ic_warning)),
                ),
            selected = mode,
            onOptionSelected = onModeChanged,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun sensitivityLabel(level: Int): String =
    when (level) {
        SENSITIVITY_LOW -> "Low"
        SENSITIVITY_HIGH -> "High"
        else -> "Medium"
    }

@Composable
private fun HardwareCard(
    dimmerLevel: Int,
    dimmerMaxLevel: Int,
    onDimmerLevelChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSupported = dimmerMaxLevel > 1
    Column(modifier = modifier) {
        SectionLabel(text = "HARDWARE", color = MaterialTheme.colorScheme.primaryContainer)
        Spacer(Modifier.height(Spacing.Unit))
        SMLCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Flashlight Dimmer",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(Spacing.S))
            Text(
                text = if (isSupported) "Adjust flashlight brightness" else "Requires supported hardware",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Spacing.Gutter))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
            ) {
                SMLSlider(
                    value = dimmerLevel.toFloat(),
                    onValueChange = { onDimmerLevelChanged(it.roundToInt()) },
                    valueRange = 1f..dimmerMaxLevel.coerceAtLeast(1).toFloat(),
                    steps = DIMMER_SLIDER_STEPS,
                    enabled = isSupported,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SliderStopLabel("Min")
                SliderStopLabel("Max")
            }
        }
    }
}

@Composable
private fun TransmissionCard(
    speedWpm: Int,
    onSpeedChanged: (Int) -> Unit,
    pauseMillis: Long,
    onPauseChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(text = "TRANSMISSION", color = MaterialTheme.colorScheme.primaryContainer)
        Spacer(Modifier.height(Spacing.Unit))
        SMLCard(modifier = Modifier.fillMaxWidth()) {
            TransmissionStat(
                title = "Transmission Speed",
                value = "$speedWpm WPM",
                subtitle = "Speed of the flashlight blink pattern",
            )
            Spacer(Modifier.height(Spacing.Gutter))
            SMLSlider(
                value = speedWpm.toFloat(),
                onValueChange = { onSpeedChanged(it.roundToInt()) },
                valueRange = MIN_TRANSMISSION_WPM.toFloat()..MAX_TRANSMISSION_WPM.toFloat(),
                steps = MAX_TRANSMISSION_WPM - MIN_TRANSMISSION_WPM - 1,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SliderStopLabel("$MIN_TRANSMISSION_WPM WPM")
                SliderStopLabel("$MAX_TRANSMISSION_WPM WPM")
            }
            Spacer(Modifier.height(Spacing.Gutter))
            SectionDivider()
            Spacer(Modifier.height(Spacing.Gutter))
            TransmissionStat(
                title = "Pause Between Loops",
                value = formatLoopPause(pauseMillis),
                subtitle = "Delay before repeating a looped or emergency message",
            )
            Spacer(Modifier.height(Spacing.Gutter))
            val stopIndex = LOOP_PAUSE_STOPS_MILLIS.indexOf(pauseMillis).coerceAtLeast(0)
            SMLSlider(
                value = stopIndex.toFloat(),
                onValueChange = { onPauseChanged(LOOP_PAUSE_STOPS_MILLIS[it.roundToInt()]) },
                valueRange = 0f..(LOOP_PAUSE_STOPS_MILLIS.size - 1).toFloat(),
                steps = LOOP_PAUSE_STOPS_MILLIS.size - 2,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SliderStopLabel("Off")
                SliderStopLabel("1 Min")
                SliderStopLabel("10 Min")
            }
        }
    }
}

@Composable
private fun TransmissionStat(
    title: String,
    value: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primaryContainer,
        )
    }
    Spacer(Modifier.height(Spacing.S))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SliderStopLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

private const val MIN_TRANSMISSION_WPM = 5
private const val MAX_TRANSMISSION_WPM = 15

private val LOOP_PAUSE_STOPS_MILLIS =
    listOf(0L, 2_000L, 5_000L, 10_000L, 20_000L, 30_000L, 60_000L, 120_000L, 300_000L, 600_000L)
private const val MILLIS_PER_SECOND = 1_000L
private const val SECONDS_PER_MINUTE = 60

private fun formatLoopPause(millis: Long): String {
    if (millis <= 0) return "OFF"
    val totalSeconds = millis / MILLIS_PER_SECOND
    return if (totalSeconds < SECONDS_PER_MINUTE) {
        "${totalSeconds}S"
    } else {
        "${totalSeconds / SECONDS_PER_MINUTE}MIN"
    }
}

@Composable
private fun SliderEndIcon(iconRes: Int) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(16.dp),
    )
}

@Composable
private fun EmergencyModeCard(
    message: String,
    isStrobeActive: Boolean,
    onMessageChanged: (String) -> Unit,
    onStrobeToggled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(text = "EMERGENCY MODE", color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(Spacing.Unit))
        SMLCard(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.error.copy(alpha = EMERGENCY_BORDER_ALPHA),
                        shape = MaterialTheme.shapes.small,
                    ),
        ) {
            Text(
                text = "Morse Message",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(Spacing.Unit / 2))
            SMLTextField(
                value = message,
                onValueChange = onMessageChanged,
                placeholder = "Enter emergency message...",
                enabled = !isStrobeActive,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.Gutter))
            SectionDivider()
            Spacer(Modifier.height(Spacing.Gutter))
            EmergencyTestRow(
                isActive = isStrobeActive,
                enabled = isStrobeActive || message.isNotBlank(),
                onClick = onStrobeToggled,
            )
        }
    }
}

@Composable
private fun EmergencyTestRow(
    isActive: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isActive) "Testing…" else "Test It",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(Spacing.S))
            Text(
                text = "Plays the saved message when shaken in Emergency mode or from the SOS tile",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        EmergencyTestButton(isActive = isActive, enabled = enabled, onClick = onClick)
    }
}

@Composable
private fun EmergencyTestButton(
    isActive: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val tint = MaterialTheme.colorScheme.error
    val backgroundAlpha = if (enabled) EMERGENCY_TEST_BUTTON_ALPHA else EMERGENCY_TEST_BUTTON_ALPHA / 2
    Box(
        modifier =
            Modifier
                .size(EmergencyTestButtonSize)
                .clip(CircleShape)
                .background(tint.copy(alpha = backgroundAlpha))
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(if (isActive) R.drawable.ic_stop else R.drawable.ic_send),
            contentDescription = if (isActive) "Stop test" else "Test emergency message",
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun HapticsCard(
    isEnabled: Boolean,
    onToggled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(text = "HAPTICS", color = MaterialTheme.colorScheme.primaryContainer)
        Spacer(Modifier.height(Spacing.Unit))
        SMLCard(modifier = Modifier.fillMaxWidth()) {
            SettingToggleRow(
                title = "Haptic Feedback",
                subtitle = "Vibrate on slider drags, power taps, and selections",
                checked = isEnabled,
                onCheckedChange = { onToggled() },
            )
        }
    }
}

@Composable
private fun AppearanceCard(
    themeMode: ThemeMode,
    onThemeModeChanged: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(text = "APPEARANCE", color = MaterialTheme.colorScheme.primaryContainer)
        Spacer(Modifier.height(Spacing.Unit))
        SMLCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "App Theme",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(Spacing.Gutter))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
            ) {
                SMLOptionCard(
                    label = "Dark",
                    icon = painterResource(R.drawable.ic_theme_dark),
                    selected = themeMode == ThemeMode.DARK,
                    onClick = { onThemeModeChanged(ThemeMode.DARK) },
                    modifier = Modifier.weight(1f),
                )
                SMLOptionCard(
                    label = "Light",
                    icon = painterResource(R.drawable.ic_theme_light),
                    selected = themeMode == ThemeMode.LIGHT,
                    onClick = { onThemeModeChanged(ThemeMode.LIGHT) },
                    modifier = Modifier.weight(1f),
                )
                SMLOptionCard(
                    label = "Auto",
                    icon = painterResource(R.drawable.ic_theme_light_dark),
                    selected = themeMode == ThemeMode.AUTO,
                    onClick = { onThemeModeChanged(ThemeMode.AUTO) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AboutCard(
    onAboutClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SMLCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAboutClicked)
                    .padding(horizontal = Spacing.Margin, vertical = Spacing.Margin),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "About",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

private const val DIVIDER_ALPHA = 0.08f
private const val BADGE_BACKGROUND_ALPHA = 0.2f
private const val EMERGENCY_BORDER_ALPHA = 0.3f
private val EmergencyTestButtonSize = 40.dp
private const val EMERGENCY_TEST_BUTTON_ALPHA = 0.15f
private const val MIN_SENSITIVITY = 1f
private const val MAX_SENSITIVITY = 3f
private const val SENSITIVITY_LOW = 1
private const val SENSITIVITY_HIGH = 3
private const val DIMMER_SLIDER_STEPS = 8

@PreviewLightDark
@Composable
internal fun SettingsScreenPreview() {
    ShakeMorseLampTheme {
        Surface {
            SettingsScreen(
                uiState =
                    SettingsUiState(
                        themeMode = ThemeMode.LIGHT,
                        emergencyMessage = "SOS",
                        isStrobeActive = false,
                    ),
                onAction = {},
                onNavigateBack = {},
                onNavigateToAbout = {},
            )
        }
    }
}
