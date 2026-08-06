package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.component.SMLButton
import com.handysparksoft.shakelamp.core.designsystem.component.SMLButtonVariant
import com.handysparksoft.shakelamp.core.designsystem.component.SMLCard
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSlider
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSwitch
import com.handysparksoft.shakelamp.core.designsystem.component.SMLTextField
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing
import kotlin.math.roundToInt

@Composable
fun FlashlightScreen(
    uiState: FlashlightUiState,
    onAction: (FlashlightUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.Margin),
        verticalArrangement = Arrangement.spacedBy(Spacing.Gutter),
    ) {
        Text(
            text = "Flashlight",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        StatusRow(isOn = uiState.isOn)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PowerButton(
                isOn = uiState.isOn,
                enabled = uiState.isAvailable,
                onClick = { onAction(FlashlightUiAction.TogglePower) },
                modifier = Modifier.padding(vertical = Spacing.Margin),
            )
            Text(
                text = "Main Array",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = statusLabel(uiState),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AutoOffTimerCard(
            timerMinutes = uiState.timerMinutes,
            onTimerChanged = { onAction(FlashlightUiAction.TimerChanged(it)) },
        )
        MorseBroadcastCard(
            message = uiState.morseMessage,
            isLoopEnabled = uiState.isLoopEnabled,
            onMessageChanged = { onAction(FlashlightUiAction.MessageChanged(it)) },
            onLoopToggled = { onAction(FlashlightUiAction.LoopToggled) },
            onTransmitClicked = { onAction(FlashlightUiAction.TransmitClicked) },
        )
        QuickAccessWidgetCard(
            onConfigureClicked = { onAction(FlashlightUiAction.ConfigureWidgetClicked) },
        )
    }
}

private fun statusLabel(uiState: FlashlightUiState): String =
    when {
        !uiState.isAvailable -> "Flashlight unavailable"
        uiState.isOn -> "Active - Emitting Light"
        else -> "Ready to ignite"
    }

/** A generic dummy icon placeholder — swap for real Material Symbols glyphs once provided. */
@Composable
private fun PlaceholderIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Box(
        modifier =
            modifier
                .size(size)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(tint.copy(alpha = 0.15f))
                .border(1.dp, tint.copy(alpha = 0.4f), MaterialTheme.shapes.extraSmall),
    )
}

@Composable
private fun StatusRow(
    isOn: Boolean,
    modifier: Modifier = Modifier,
) {
    val statusColor =
        if (isOn) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.error
        }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor),
            )
            Text(
                text = if (isOn) "ARRAY ACTIVE" else "SYSTEM STANDBY",
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Unit / 2),
        ) {
            PlaceholderIcon(size = 12.dp)
            Text(
                text = "100%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PowerButton(
    isOn: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val glowColor = MaterialTheme.colorScheme.primaryContainer
    Box(modifier = modifier.size(224.dp), contentAlignment = Alignment.Center) {
        if (isOn) {
            Box(
                modifier =
                    Modifier
                        .size(224.dp)
                        .background(
                            brush = Brush.radialGradient(listOf(glowColor.copy(alpha = 0.35f), Color.Transparent)),
                            shape = CircleShape,
                        ),
            )
        }
        Box(
            modifier =
                Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(148.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
            )
        }
    }
}

@Composable
private fun AutoOffTimerCard(
    timerMinutes: Int,
    onTimerChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SMLCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
            ) {
                PlaceholderIcon()
                Text(
                    text = "Auto-Off Timer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = if (timerMinutes == 0) "OFF" else "$timerMinutes MIN",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primaryContainer,
            )
        }
        Spacer(Modifier.height(Spacing.Unit))
        SMLSlider(
            value = timerMinutes.toFloat(),
            onValueChange = { onTimerChanged(it.roundToInt()) },
            valueRange = 0f..60f,
            steps = 11,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Off", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("30m", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("60m", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MorseBroadcastCard(
    message: String,
    isLoopEnabled: Boolean,
    onMessageChanged: (String) -> Unit,
    onLoopToggled: () -> Unit,
    onTransmitClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SMLCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
            ) {
                PlaceholderIcon()
                Text(
                    text = "Morse Broadcast",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
            ) {
                Text(
                    text = "LOOP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                SMLSwitch(checked = isLoopEnabled, onCheckedChange = { onLoopToggled() })
            }
        }
        Spacer(Modifier.height(Spacing.Gutter))
        SMLTextField(
            value = message,
            onValueChange = onMessageChanged,
            placeholder = "Enter message to broadcast...",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.Unit))
        SMLButton(
            text = "Transmit Signal",
            onClick = onTransmitClicked,
            variant = SMLButtonVariant.Secondary,
            leadingIcon = { PlaceholderIcon(size = 16.dp) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun QuickAccessWidgetCard(
    onConfigureClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = MaterialTheme.colorScheme.primaryContainer
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(tint.copy(alpha = 0.1f))
                .border(1.dp, tint.copy(alpha = 0.2f), MaterialTheme.shapes.small)
                .padding(Spacing.Margin),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            PlaceholderIcon(tint = tint)
        }
        Spacer(Modifier.height(Spacing.Unit))
        Text(
            text = "Quick Access Widget",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Control your array directly from the home screen.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.Gutter))
        SMLButton(
            text = "Configure Widget",
            onClick = onConfigureClicked,
            variant = SMLButtonVariant.Primary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@PreviewLightDark
@Composable
internal fun FlashlightScreenPreview() {
    ShakeMorseLampTheme {
        Surface {
            FlashlightScreen(
                uiState =
                    FlashlightUiState(
                        isOn = true,
                        isAvailable = true,
                        timerMinutes = 30,
                        morseMessage = "SOS",
                        isLoopEnabled = true,
                    ),
                onAction = {},
            )
        }
    }
}
