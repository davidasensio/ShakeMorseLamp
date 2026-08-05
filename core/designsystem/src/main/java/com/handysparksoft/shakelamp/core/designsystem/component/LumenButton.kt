package com.handysparksoft.shakelamp.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

/**
 * A pill-shaped, uppercase-label button matching the Lumen Utility design system
 * (e.g. "Configure Widget", "Transmit Signal").
 */
@Composable
fun LumenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: LumenButtonVariant = LumenButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val shape = MaterialTheme.shapes.extraLarge
    val content: @Composable () -> Unit = {
        LumenButtonContent(text = text, leadingIcon = leadingIcon)
    }
    when (variant) {
        LumenButtonVariant.Primary ->
            Button(
                onClick = onClick,
                modifier = modifier.height(48.dp),
                enabled = enabled,
                shape = shape,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                content = { content() },
            )

        LumenButtonVariant.Secondary ->
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(48.dp),
                enabled = enabled,
                shape = shape,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                content = { content() },
            )
    }
}

@Composable
private fun LumenButtonContent(
    text: String,
    leadingIcon: (@Composable () -> Unit)?,
) {
    if (leadingIcon != null) {
        leadingIcon()
        Spacer(Modifier.width(8.dp))
    }
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.1.em,
    )
}
