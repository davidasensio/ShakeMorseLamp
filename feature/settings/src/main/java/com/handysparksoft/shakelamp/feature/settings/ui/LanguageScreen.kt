package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.handysparksoft.shakelamp.core.designsystem.component.SMLCard
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing
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
                .verticalScroll(rememberScrollState())
                .padding(Spacing.Margin),
        verticalArrangement = Arrangement.spacedBy(Spacing.Gutter),
    ) {
        ScreenHeader(title = stringResource(SettingsR.string.language_screen_title), onNavigateBack = onNavigateBack)
        SMLCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
            LanguageOptionRow(
                label = stringResource(SettingsR.string.language_system_default_label),
                selected = uiState.selectedTag == null,
                onClick = { onAction(LanguageUiAction.LocaleSelected(null)) },
            )
            uiState.options.forEach { option ->
                LanguageOptionDivider()
                LanguageOptionRow(
                    label = option.label,
                    selected = uiState.selectedTag == option.tag,
                    onClick = { onAction(LanguageUiAction.LocaleSelected(option.tag)) },
                )
            }
        }
    }
}

@Composable
private fun LanguageOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onSurface,
        )
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
                                LocaleOption(tag = "en", label = "English"),
                                LocaleOption(tag = "es", label = "Español"),
                                LocaleOption(tag = "de", label = "Deutsch"),
                            ),
                        selectedTag = null,
                    ),
                onAction = {},
                onNavigateBack = {},
            )
        }
    }
}
