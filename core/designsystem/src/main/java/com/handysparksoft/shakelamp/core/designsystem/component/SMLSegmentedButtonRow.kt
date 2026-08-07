package com.handysparksoft.shakelamp.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing

/** A Material3 single-choice segmented button row (e.g. Normal/Emergency shake mode). */
@Composable
fun <T> SMLSegmentedButtonRow(
    options: List<SMLSegmentedOption<T>>,
    selected: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                selected = option.value == selected,
                onClick = { onOptionSelected(option.value) },
                colors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                icon = {
                    option.icon?.let {
                        Icon(painter = it, contentDescription = null, modifier = Modifier.size(SEGMENT_ICON_SIZE))
                    }
                },
            ) {
                Text(text = option.label)
            }
        }
    }
}

private val SEGMENT_ICON_SIZE = 16.dp

private enum class PreviewMode { NORMAL, EMERGENCY }

@PreviewLightDark
@Composable
internal fun SMLSegmentedButtonRowPreview() {
    ShakeMorseLampTheme {
        Surface {
            var selected by remember { mutableStateOf(PreviewMode.NORMAL) }
            SMLSegmentedButtonRow(
                options =
                    listOf(
                        SMLSegmentedOption(PreviewMode.NORMAL, "Normal", painterResource(R.drawable.ic_flashlight_on)),
                        SMLSegmentedOption(PreviewMode.EMERGENCY, "Emergency", painterResource(R.drawable.ic_warning)),
                    ),
                selected = selected,
                onOptionSelected = { selected = it },
                modifier = Modifier.padding(Spacing.Margin),
            )
        }
    }
}
