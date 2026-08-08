package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.designsystem.component.SMLCard
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing
import com.handysparksoft.shakelamp.feature.settings.domain.LocaleDisplayFormatter
import com.handysparksoft.shakelamp.feature.settings.domain.LocaleOption
import com.handysparksoft.shakelamp.feature.settings.R as SettingsR

@Composable
fun LanguageScreen(
    uiState: LanguageUiState,
    onAction: (LanguageUiAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.Margin),
        verticalArrangement = Arrangement.spacedBy(Spacing.Gutter),
    ) {
        ScreenHeader(title = stringResource(SettingsR.string.language_screen_title), onNavigateBack = onNavigateBack)
        SMLCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
            LanguageOptionRow(
                flagEmoji = LocaleDisplayFormatter.SYSTEM_DEFAULT_FLAG_EMOJI,
                label = stringResource(SettingsR.string.language_system_default_label),
                selected = uiState.selectedTag == null,
                onClick = { onAction(LanguageUiAction.LocaleSelected(null)) },
            )
            uiState.options.forEach { option ->
                LanguageOptionDivider()
                LanguageOptionRow(
                    flagEmoji = option.flagEmoji,
                    label = option.label,
                    nativeLabel = option.nativeLabel,
                    selected = uiState.selectedTag == option.tag,
                    onClick = {
                        onAction(LanguageUiAction.LocaleSelected(option.tag))
                    },
                )
            }
        }
    }
}

@Composable
private fun LanguageOptionRow(
    flagEmoji: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    nativeLabel: String? = null,
) {
    val labelColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onSurface
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.Margin, vertical = Spacing.ContainerPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Unit),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = flagEmoji, style = MaterialTheme.typography.bodyMedium)
            Column {
                Text(text = label, style = MaterialTheme.typography.bodyMedium, color = labelColor)
                if (nativeLabel != null) {
                    Text(
                        text = nativeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun LanguageOptionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = Spacing.Margin),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = DIVIDER_ALPHA),
    )
}

private const val DIVIDER_ALPHA = 0.08f

@PreviewLightDark
@Composable
internal fun LanguageScreenPreview() {
    ShakeMorseLampTheme {
        Surface {
            LanguageScreen(
                uiState =
                    LanguageUiState(
                        options =
                            listOf(
                                LocaleOption(
                                    tag = "en",
                                    label = "English",
                                    nativeLabel = "English",
                                    flagEmoji = "🇺🇸",
                                ),
                                LocaleOption(
                                    tag = "es",
                                    label = "Spanish",
                                    nativeLabel = "Español",
                                    flagEmoji = "🇪🇸",
                                ),
                                LocaleOption(
                                    tag = "de",
                                    label = "German",
                                    nativeLabel = "Deutsch",
                                    flagEmoji = "🇩🇪",
                                ),
                            ),
                        selectedTag = null,
                    ),
                onAction = {},
                onNavigateBack = {},
            )
        }
    }
}
