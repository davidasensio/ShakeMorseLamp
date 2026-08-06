package com.handysparksoft.shakelamp.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing

/** A stepped slider matching the Lumen Utility design system's flat, thin-track look. */
@Composable
fun SMLSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        colors =
            SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primaryContainer,
                activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledThumbColor =
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = DISABLED_THUMB_ALPHA),
                disabledActiveTrackColor =
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = DISABLED_ACTIVE_TRACK_ALPHA),
                disabledInactiveTrackColor =
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = DISABLED_INACTIVE_TRACK_ALPHA),
            ),
    )
}

@PreviewLightDark
@Composable
internal fun SMLSliderPreview() {
    ShakeMorseLampTheme {
        Surface {
            var value by remember { mutableFloatStateOf(PREVIEW_INITIAL_VALUE) }
            SMLSlider(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.padding(Spacing.Margin),
                valueRange = 0f..60f,
                steps = 11,
            )
        }
    }
}

private const val PREVIEW_INITIAL_VALUE = 20f
private const val DISABLED_THUMB_ALPHA = 0.4f
private const val DISABLED_ACTIVE_TRACK_ALPHA = 0.3f
private const val DISABLED_INACTIVE_TRACK_ALPHA = 0.15f
