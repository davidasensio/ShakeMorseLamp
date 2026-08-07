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
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(Spacing.Margin),
            verticalArrangement = Arrangement.spacedBy(Spacing.Gutter),
        ) {
            ScreenHeader(title = "Settings", onNavigateBack = onNavigateBack)
            GesturesCard(
                shakeSensitivity = uiState.shakeSensitivity,
                shakeMode = uiState.shakeMode,
                onSensitivityChanged = { onAction(SettingsUiAction.ShakeSensitivityChanged(it)) },
                onShakeModeChanged = { onAction(SettingsUiAction.ShakeModeChanged(it)) },
            )
            HardwareCard(
                dimmerLevel = uiState.dimmerLevel,
                dimmerMaxLevel = uiState.dimmerMaxLevel,
                onDimmerLevelChanged = { onAction(SettingsUiAction.DimmerLevelChanged(it)) },
            )
            EmergencyModeCard(
                message = uiState.emergencyMessage,
                isStrobeActive = uiState.isStrobeActive,
                onMessageChanged = { onAction(SettingsUiAction.EmergencyMessageChanged(it)) },
                onStrobeToggled = { onAction(SettingsUiAction.StrobeToggled) },
            )
            AppearanceCard(
                themeMode = uiState.themeMode,
                onThemeModeChanged = { onAction(SettingsUiAction.ThemeModeChanged(it)) },
            )
            AboutCard(onAboutClicked = onNavigateToAbout)
        }
    }
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

@Composable
private fun GesturesCard(
    shakeSensitivity: Int,
    shakeMode: ShakeMode,
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
                subtitle = "Activate flashlight with a double shake",
                checked = false,
                onCheckedChange = {},
                enabled = false,
            )
            Spacer(Modifier.height(Spacing.Gutter))
            SectionDivider()
            Spacer(Modifier.height(Spacing.Gutter))
            SensitivitySelector(sensitivity = shakeSensitivity, onSensitivityChanged = onSensitivityChanged)
            Spacer(Modifier.height(Spacing.Gutter))
            SectionDivider()
            Spacer(Modifier.height(Spacing.Gutter))
            ShakeModeSelector(mode = shakeMode, onModeChanged = onShakeModeChanged)
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
                SliderEndIcon(R.drawable.ic_brightness_low)
                SMLSlider(
                    value = dimmerLevel.toFloat(),
                    onValueChange = { onDimmerLevelChanged(it.roundToInt()) },
                    valueRange = 1f..dimmerMaxLevel.coerceAtLeast(1).toFloat(),
                    enabled = isSupported,
                    modifier = Modifier.weight(1f),
                )
                SliderEndIcon(R.drawable.ic_brightness_high)
            }
        }
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
            SettingToggleRow(
                title = "Strobe Active",
                subtitle = "Loops message continuously",
                checked = isStrobeActive,
                onCheckedChange = { onStrobeToggled() },
                enabled = isStrobeActive || message.isNotBlank(),
                titleColor = MaterialTheme.colorScheme.error,
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
private const val MIN_SENSITIVITY = 1f
private const val MAX_SENSITIVITY = 3f
private const val SENSITIVITY_LOW = 1
private const val SENSITIVITY_HIGH = 3

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
