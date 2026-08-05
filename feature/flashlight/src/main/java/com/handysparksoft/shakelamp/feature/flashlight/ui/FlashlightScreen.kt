package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing

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
                .padding(Spacing.Margin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Flashlight",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
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
}

private fun statusLabel(uiState: FlashlightUiState): String =
    when {
        !uiState.isAvailable -> "Flashlight unavailable"
        uiState.isOn -> "Active - Emitting Light"
        else -> "Ready to ignite"
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

@PreviewLightDark
@Composable
internal fun FlashlightScreenPreview() {
    ShakeMorseLampTheme {
        Surface {
            FlashlightScreen(
                uiState = FlashlightUiState(isOn = false, isAvailable = true),
                onAction = {},
            )
        }
    }
}
