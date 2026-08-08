package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.designsystem.component.SMLButton
import com.handysparksoft.shakelamp.core.designsystem.component.SMLButtonVariant
import com.handysparksoft.shakelamp.core.designsystem.component.SMLCard
import com.handysparksoft.shakelamp.core.designsystem.component.SMLChip
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSlider
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSwitch
import com.handysparksoft.shakelamp.core.designsystem.component.SMLTextField
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing
import kotlin.math.roundToInt
import com.handysparksoft.shakelamp.feature.flashlight.R as FlashlightR

@Composable
fun FlashlightScreen(
    uiState: FlashlightUiState,
    onAction: (FlashlightUiAction) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val performHaptic: (HapticFeedbackType) -> Unit = {
        if (uiState.isHapticFeedbackEnabled) haptic.performHapticFeedback(it)
    }
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(Spacing.Margin),
        verticalArrangement = Arrangement.spacedBy(Spacing.Gutter),
    ) {
        StatusRow(isOn = uiState.isOn, onSettingsClicked = onNavigateToSettings)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PowerButton(
                isOn = uiState.isOn,
                enabled = uiState.isAvailable,
                autoOffProgress = autoOffProgress(uiState),
                onClick = {
                    performHaptic(HapticFeedbackType.Confirm)
                    onAction(FlashlightUiAction.TogglePower)
                },
                modifier = Modifier.padding(vertical = Spacing.Margin),
            )
            Text(
                text = statusLabel(uiState),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AutoOffTimerCard(
            timerMinutes = uiState.timerMinutes,
            onTimerChanged = {
                performHaptic(HapticFeedbackType.SegmentFrequentTick)
                onAction(FlashlightUiAction.TimerChanged(it))
            },
        )
        MorseBroadcastCard(uiState = uiState, onAction = onAction)
        QuickAccessWidgetCard(
            onConfigureClicked = { onAction(FlashlightUiAction.ConfigureWidgetClicked) },
        )
    }
}

@Composable
private fun statusLabel(uiState: FlashlightUiState): String =
    when {
        !uiState.isAvailable -> stringResource(FlashlightR.string.flashlight_status_unavailable)
        uiState.isTransmitting -> stringResource(FlashlightR.string.flashlight_status_transmitting)
        uiState.isOn && uiState.autoOffRemainingMillis != null ->
            stringResource(FlashlightR.string.flashlight_status_auto_off)
        uiState.isOn -> stringResource(FlashlightR.string.flashlight_status_emitting_light)
        else -> stringResource(FlashlightR.string.flashlight_status_ready)
    }

/** Fraction of the auto-off timer remaining, or null when no countdown is active. */
private fun autoOffProgress(uiState: FlashlightUiState): Float? {
    val remainingMillis = uiState.autoOffRemainingMillis ?: return null
    if (uiState.timerMinutes <= 0) return null
    val totalMillis = uiState.timerMinutes * MILLIS_PER_MINUTE
    return (remainingMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
}

private const val MILLIS_PER_MINUTE = 60_000L

@Composable
private fun StatusRow(
    isOn: Boolean,
    onSettingsClicked: () -> Unit,
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
                text =
                    if (isOn) {
                        stringResource(FlashlightR.string.flashlight_status_flashlight_active)
                    } else {
                        stringResource(FlashlightR.string.flashlight_status_system_standby)
                    },
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_tune),
            contentDescription = stringResource(FlashlightR.string.flashlight_settings_content_description),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(onClick = onSettingsClicked),
        )
    }
}

@Composable
private fun PowerButton(
    isOn: Boolean,
    enabled: Boolean,
    autoOffProgress: Float?,
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
        if (autoOffProgress != null) {
            CircularProgressIndicator(
                progress = { autoOffProgress },
                modifier = Modifier.size(200.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                strokeWidth = 4.dp,
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
                contentAlignment = Alignment.Center,
            ) {
                PowerIcon(isOn = isOn, glowColor = glowColor)
            }
        }
    }
}

@Composable
private fun PowerIcon(
    isOn: Boolean,
    glowColor: Color,
) {
    if (isOn) {
        val glowBrush = Brush.radialGradient(listOf(glowColor.copy(alpha = 0.5f), Color.Transparent))
        Box(modifier = Modifier.size(PowerIconGlowSize).background(brush = glowBrush, shape = CircleShape))
    }
    Icon(
        painter = painterResource(R.drawable.ic_power),
        contentDescription = null,
        tint = if (isOn) glowColor else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(PowerIconSize),
    )
}

private val PowerIconSize = 64.dp
private val PowerIconGlowSize = 96.dp

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
                Icon(
                    painter = painterResource(R.drawable.ic_timer),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(FlashlightR.string.flashlight_autooff_timer_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = formatTimerDuration(timerMinutes),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primaryContainer,
            )
        }
        Spacer(Modifier.height(Spacing.Unit))
        val stopIndex = AUTO_OFF_TIMER_STOPS_MINUTES.indexOf(timerMinutes).coerceAtLeast(0)
        SMLSlider(
            value = stopIndex.toFloat(),
            onValueChange = { onTimerChanged(AUTO_OFF_TIMER_STOPS_MINUTES[it.roundToInt()]) },
            valueRange = 0f..(AUTO_OFF_TIMER_STOPS_MINUTES.size - 1).toFloat(),
            steps = AUTO_OFF_TIMER_STOPS_MINUTES.size - 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Off", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("1H", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("8H", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private val AUTO_OFF_TIMER_STOPS_MINUTES = listOf(0, 1, 2, 3, 5, 10, 15, 20, 30, 60, 120, 240, 480)
private const val MINUTES_PER_HOUR = 60

private fun formatTimerDuration(minutes: Int): String =
    when {
        minutes <= 0 -> "OFF"
        minutes < MINUTES_PER_HOUR -> "$minutes MIN"
        else -> "${minutes / MINUTES_PER_HOUR}H"
    }

@Composable
private fun MorseBroadcastHeader(
    isLoopEnabled: Boolean,
    isTransmitting: Boolean,
    onLoopToggled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_satellite),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(FlashlightR.string.flashlight_morse_broadcast_title),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
        ) {
            Text(
                text = stringResource(FlashlightR.string.flashlight_loop_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SMLSwitch(
                checked = isLoopEnabled,
                onCheckedChange = { onLoopToggled() },
                enabled = !isTransmitting,
            )
        }
    }
}

@Composable
private fun MorseBroadcastCard(
    uiState: FlashlightUiState,
    onAction: (FlashlightUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isTransmitting = uiState.isTransmitting
    SMLCard(modifier = modifier.fillMaxWidth()) {
        MorseBroadcastHeader(
            isLoopEnabled = uiState.isLoopEnabled,
            isTransmitting = isTransmitting,
            onLoopToggled = { onAction(FlashlightUiAction.LoopToggled) },
        )
        Spacer(Modifier.height(Spacing.Gutter))
        SMLTextField(
            value = uiState.morseMessage,
            onValueChange = { onAction(FlashlightUiAction.MessageChanged(it)) },
            placeholder = stringResource(FlashlightR.string.flashlight_message_placeholder),
            enabled = !isTransmitting,
            onClear = { onAction(FlashlightUiAction.MessageChanged("")) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.Unit))
        SMLButton(
            text =
                if (isTransmitting) {
                    stringResource(FlashlightR.string.flashlight_stop_transmission)
                } else {
                    stringResource(FlashlightR.string.flashlight_transmit_signal)
                },
            onClick = { onAction(FlashlightUiAction.TransmitClicked) },
            variant = SMLButtonVariant.Secondary,
            enabled = isTransmitting || uiState.morseMessage.isNotBlank(),
            leadingIcon = {
                Icon(
                    painter = painterResource(if (isTransmitting) R.drawable.ic_stop else R.drawable.ic_send),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.Gutter))
        QuickSignalsRow(
            isTransmitting = isTransmitting,
            onSignalSelected = { onAction(FlashlightUiAction.MessageChanged(it)) },
        )
        HistorySection(
            history = uiState.sentMessageHistory,
            isExpanded = uiState.isHistoryExpanded,
            isTransmitting = isTransmitting,
            onToggleExpanded = { onAction(FlashlightUiAction.HistoryToggled) },
            onMessageSelected = { onAction(FlashlightUiAction.MessageChanged(it)) },
        )
    }
}

private val QuickSignals = listOf("SOS", "HELP", "OK")

@Composable
private fun QuickSignalsRow(
    isTransmitting: Boolean,
    onSignalSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(FlashlightR.string.flashlight_quick_signals_title),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.Unit / 2))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.Unit)) {
            QuickSignals.forEach { signal ->
                SMLChip(label = signal, onClick = { onSignalSelected(signal) }, enabled = !isTransmitting)
            }
        }
    }
}

private val HistoryChipMaxWidth = 160.dp

@Composable
private fun HistorySection(
    history: List<String>,
    isExpanded: Boolean,
    isTransmitting: Boolean,
    onToggleExpanded: () -> Unit,
    onMessageSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (history.isEmpty()) return
    Column(modifier = modifier.padding(top = Spacing.Gutter)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleExpanded),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(FlashlightR.string.flashlight_history_title),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Icon(
                painter = painterResource(if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (isExpanded) {
            Spacer(Modifier.height(Spacing.Unit / 2))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.Unit)) {
                history.forEach { message ->
                    SMLChip(
                        label = message,
                        onClick = { onMessageSelected(message) },
                        enabled = !isTransmitting,
                        modifier = Modifier.widthIn(max = HistoryChipMaxWidth),
                    )
                }
            }
        }
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
            Icon(
                painter = painterResource(R.drawable.ic_home),
                contentDescription = null,
                tint = tint,
            )
        }
        Spacer(Modifier.height(Spacing.Unit))
        Text(
            text = stringResource(FlashlightR.string.flashlight_quick_access_widget_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(Spacing.S))
        Text(
            text = stringResource(FlashlightR.string.flashlight_quick_access_widget_subtitle),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.Gutter))
        SMLButton(
            text = stringResource(FlashlightR.string.flashlight_configure_widget_cta),
            onClick = onConfigureClicked,
            variant = SMLButtonVariant.Primary,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            },
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
                        autoOffRemainingMillis = 20 * 60_000L,
                        morseMessage = "SOS",
                        isLoopEnabled = true,
                        sentMessageHistory = listOf("SOS", "MEET AT DAWN", "OK"),
                        isHistoryExpanded = true,
                    ),
                onAction = {},
                onNavigateToSettings = {},
            )
        }
    }
}
