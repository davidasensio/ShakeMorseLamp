package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object SettingsRoute : NavKey

/** Static app version info, owned by :app's own BuildConfig — passed in, not resolved via Koin. */
data class AppVersionInfo(
    val versionName: String,
    val versionCode: Int,
)

@Composable
fun SettingsEntry(
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onNavigateToAbout = onNavigateToAbout,
        modifier = modifier,
    )
}
