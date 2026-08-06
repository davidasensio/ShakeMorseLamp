package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing

/**
 * Shared back-arrow + title header. Uses a plain clickable [Icon] rather than [IconButton] —
 * the latter's 48dp minimum touch target inflated the row height and read as extra top padding.
 */
@Composable
internal fun ScreenHeader(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier =
                Modifier
                    .size(24.dp)
                    .clickable(onClick = onNavigateBack),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
