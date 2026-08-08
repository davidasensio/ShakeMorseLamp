package com.handysparksoft.shakelamp.feature.settings.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.designsystem.component.SMLCard
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing
import timber.log.Timber
import java.time.Year
import com.handysparksoft.shakelamp.feature.settings.R as SettingsR

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    appVersion: AppVersionInfo,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(Spacing.Margin),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.Gutter),
        ) {
            ScreenHeader(
                title = stringResource(SettingsR.string.settings_about_title),
                onNavigateBack = onNavigateBack,
            )
            AboutAppBadge(appVersion = appVersion)
            Spacer(Modifier.height(Spacing.Margin))
            AboutActionsCard(context = context)
            AboutLegalCard(context = context)
        }
        AboutFooter(modifier = Modifier.padding(top = Spacing.Gutter))
    }
}

@Composable
private fun AboutActionsCard(
    context: Context,
    modifier: Modifier = Modifier,
) {
    val shareChooserTitle = stringResource(SettingsR.string.about_share_chooser_title)
    val shareMessage = stringResource(SettingsR.string.about_share_message, PLAY_STORE_WEB_URL + PACKAGE_ID)
    val feedbackEmailSubject = stringResource(SettingsR.string.about_feedback_email_subject)
    SMLCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
        AboutRow(
            icon = painterResource(R.drawable.ic_share),
            title = stringResource(SettingsR.string.about_share_app_title),
            subtitle = stringResource(SettingsR.string.about_share_app_subtitle),
            onClick = { shareApp(context, chooserTitle = shareChooserTitle, message = shareMessage) },
        )
        AboutDivider()
        AboutRow(
            icon = painterResource(R.drawable.ic_star_filled),
            title = stringResource(SettingsR.string.about_rate_title),
            subtitle = stringResource(SettingsR.string.about_rate_subtitle),
            onClick = { rateApp(context) },
        )
        AboutDivider()
        AboutRow(
            icon = painterResource(R.drawable.ic_send_feedback),
            title = stringResource(SettingsR.string.about_send_feedback_title),
            subtitle = stringResource(SettingsR.string.about_send_feedback_subtitle),
            onClick = { sendFeedback(context, subject = feedbackEmailSubject) },
        )
    }
}

@Composable
private fun AboutLegalCard(
    context: Context,
    modifier: Modifier = Modifier,
) {
    SMLCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
        AboutRow(
            icon = painterResource(R.drawable.ic_language),
            title = stringResource(SettingsR.string.about_visit_website_title),
            trailingIcon = painterResource(R.drawable.ic_open_in_new),
            onClick = { openUrl(context, SITE_URL) },
        )
        AboutDivider()
        AboutRow(
            icon = painterResource(R.drawable.ic_privacy_policy),
            title = stringResource(SettingsR.string.about_privacy_policy_title),
            trailingIcon = painterResource(R.drawable.ic_open_in_new),
            onClick = { openUrl(context, PRIVACY_POLICY_URL) },
        )
        AboutDivider()
        AboutRow(
            icon = painterResource(R.drawable.ic_contract),
            title = stringResource(SettingsR.string.about_terms_of_use_title),
            trailingIcon = painterResource(R.drawable.ic_open_in_new),
            onClick = { openUrl(context, TERMS_OF_USE_URL) },
        )
    }
}

/**
 * Hands the URL to whatever browser the device has. No `INTERNET` permission is needed - the
 * browser does the networking - which is what keeps the app fully offline.
 */
private fun openUrl(
    context: Context,
    url: String,
) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: ActivityNotFoundException) {
        Timber.w(e, "No browser available to open %s", url)
    }
}

private fun shareApp(
    context: Context,
    chooserTitle: String,
    message: String,
) {
    val shareIntent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
    context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
}

private fun rateApp(context: Context) {
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$PACKAGE_ID")).apply {
                setPackage("com.android.vending")
            },
        )
    } catch (e: ActivityNotFoundException) {
        Timber.w(e, "Play Store app not installed, falling back to web")
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$PLAY_STORE_WEB_URL$PACKAGE_ID")))
    }
}

private fun sendFeedback(
    context: Context,
    subject: String,
) {
    val emailIntent =
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(FEEDBACK_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
    context.startActivity(emailIntent)
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
        val glowColor = MaterialTheme.colorScheme.primaryContainer
        Box(
            modifier =
                Modifier
                    .size(BADGE_SIZE)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(BADGE_GLOW_SIZE)
                        .background(
                            brush = Brush.radialGradient(listOf(glowColor.copy(alpha = 0.5f), Color.Transparent)),
                            shape = CircleShape,
                        ),
            )
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                tint = glowColor,
                modifier = Modifier.size(BADGE_ICON_SIZE),
            )
        }
        Spacer(Modifier.height(Spacing.Margin))
        Text(
            text = stringResource(SettingsR.string.about_app_badge_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text =
                stringResource(
                    SettingsR.string.about_version_label,
                    appVersion.versionName,
                    appVersion.versionCode,
                ),
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
            text = stringResource(SettingsR.string.about_footer, year),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val BADGE_SIZE = 88.dp
private val BADGE_GLOW_SIZE = 56.dp
private val BADGE_ICON_SIZE = 40.dp
private const val DIVIDER_ALPHA = 0.08f
private const val PACKAGE_ID = "com.handysparksoft.shakelamp"
private const val PLAY_STORE_WEB_URL = "https://play.google.com/store/apps/details?id="
private const val FEEDBACK_EMAIL = "handysparksoft@gmail.com"
private const val SITE_URL = "https://morseshakelamp.web.app"
private const val PRIVACY_POLICY_URL = "$SITE_URL/privacy-policy"
private const val TERMS_OF_USE_URL = "$SITE_URL/terms"

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
