package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.designsystem.component.SMLCard
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing
import java.time.Year

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    appVersion: AppVersionInfo,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(Spacing.Margin),
        verticalArrangement = Arrangement.spacedBy(Spacing.Gutter),
    ) {
        ScreenHeader(title = "About", onNavigateBack = onNavigateBack)
        AboutAppBadge(appVersion = appVersion)
        SMLCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
            AboutRow(
                icon = painterResource(R.drawable.ic_share),
                title = "Share app",
                subtitle = "Share it with friends and family",
                onClick = {},
            )
            AboutDivider()
            AboutRow(
                icon = painterResource(R.drawable.ic_star_filled),
                title = "Rate on Google Play",
                subtitle = "Tell us what you think",
                onClick = {},
            )
            AboutDivider()
            AboutRow(
                icon = painterResource(R.drawable.ic_send_feedback),
                title = "Send feedback",
                subtitle = "Email us directly",
                onClick = {},
            )
        }
        SMLCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
            AboutRow(
                icon = painterResource(R.drawable.ic_privacy_policy),
                title = "Privacy Policy",
                trailingIcon = painterResource(R.drawable.ic_open_in_new),
                onClick = {},
            )
            AboutDivider()
            AboutRow(
                icon = painterResource(R.drawable.ic_contract),
                title = "Terms of Use",
                trailingIcon = painterResource(R.drawable.ic_open_in_new),
                onClick = {},
            )
        }
        AboutFooter()
    }
}

@Composable
private fun AboutAppBadge(
    appVersion: AppVersionInfo,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(BADGE_SIZE)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = BADGE_BACKGROUND_ALPHA)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(BADGE_ICON_SIZE),
            )
        }
        Spacer(Modifier.height(Spacing.Unit))
        Text(
            text = "ShakeMorseLamp",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "v${appVersion.versionName} (Build ${appVersion.versionCode})",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Ripple/tap target intentionally spans the row's full padded bounds, not just the text —
 * padding is applied after `.clickable` so it's included in the clickable/ripple area, and the
 * parent cards use zero contentPadding so this reaches the card's edges too.
 */
@Composable
private fun AboutRow(
    icon: Painter,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingIcon: Painter = painterResource(R.drawable.ic_arrow_forward),
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.Margin, vertical = Spacing.Margin),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Icon(
            painter = trailingIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun AboutDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = Spacing.Margin),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = DIVIDER_ALPHA),
    )
}

@Composable
private fun AboutFooter(modifier: Modifier = Modifier) {
    val year = remember { Year.now().value }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "© $year ShakeMorseLamp",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Made with ❤️",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val BADGE_SIZE = 88.dp
private val BADGE_ICON_SIZE = 40.dp
private const val BADGE_BACKGROUND_ALPHA = 0.2f
private const val DIVIDER_ALPHA = 0.08f

@PreviewLightDark
@Composable
internal fun AboutScreenPreview() {
    ShakeMorseLampTheme {
        Surface {
            AboutScreen(
                onNavigateBack = {},
                appVersion = AppVersionInfo(versionName = "1.0", versionCode = 1),
            )
        }
    }
}
