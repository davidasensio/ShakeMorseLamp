package com.handysparksoft.shakelamp.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing

/** A vertically-stacked icon+label option in a selectable group (e.g. theme Dark/Light/Auto). */
@Composable
fun SMLOptionCard(
    label: String,
    icon: Painter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor =
        if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        }
    val contentColor =
        if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.large)
                .background(containerColor)
                .clickable(onClick = onClick)
                .padding(vertical = Spacing.Gutter, horizontal = Spacing.Unit),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Unit / 2),
        ) {
            Icon(painter = icon, contentDescription = null, tint = contentColor)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = contentColor)
        }
        if (selected) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer),
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun SMLOptionCardPreview() {
    ShakeMorseLampTheme {
        Surface {
            Row(
                modifier = Modifier.padding(Spacing.Margin),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
            ) {
                SMLOptionCard(
                    label = "Dark",
                    icon = painterResource(R.drawable.ic_dark_mode),
                    selected = true,
                    onClick = {},
                )
                SMLOptionCard(
                    label = "Light",
                    icon = painterResource(R.drawable.ic_light_mode),
                    selected = false,
                    onClick = {},
                )
                SMLOptionCard(
                    label = "Auto",
                    icon = painterResource(R.drawable.ic_brightness_auto),
                    selected = false,
                    onClick = {},
                )
            }
        }
    }
}
