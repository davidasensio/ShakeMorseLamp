package com.handysparksoft.shakelamp.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing

/** A pill-shaped suggestion chip for quick-select shortcuts (e.g. "SOS", "HELP"). */
@Composable
fun SMLChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SuggestionChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.extraLarge,
        colors =
            SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                labelColor = MaterialTheme.colorScheme.onSurface,
            ),
        border = null,
    )
}

@PreviewLightDark
@Composable
internal fun SMLChipPreview() {
    ShakeMorseLampTheme {
        Surface {
            Row(
                modifier = Modifier.padding(Spacing.Margin),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
            ) {
                SMLChip(label = "SOS", onClick = {})
                SMLChip(label = "HELP", onClick = {})
                SMLChip(label = "OK", onClick = {}, enabled = false)
            }
        }
    }
}
