package com.handysparksoft.shakelamp.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing

/**
 * A container card with the design system's "rim light" treatment — no drop shadow, instead
 * a subtle 1dp inner top stroke to make the elevated edge read against the dark background.
 */
@Composable
fun SMLCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentPadding: PaddingValues = PaddingValues(Spacing.Margin),
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .clip(shape)
                .background(containerColor),
    ) {
        HorizontalDivider(
            modifier = Modifier.align(Alignment.TopCenter),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.1f),
        )
        Column(modifier = Modifier.padding(contentPadding)) { content() }
    }
}

@PreviewLightDark
@Composable
internal fun SMLCardPreview() {
    ShakeMorseLampTheme {
        Surface {
            SMLCard(modifier = Modifier.padding(Spacing.Margin)) {
                Text("Morse Broadcast")
            }
        }
    }
}
