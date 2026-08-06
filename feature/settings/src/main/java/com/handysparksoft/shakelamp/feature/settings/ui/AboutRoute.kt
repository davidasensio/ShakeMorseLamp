package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object AboutRoute : NavKey

@Composable
fun AboutEntry(
    onNavigateBack: () -> Unit,
    appVersion: AppVersionInfo,
    modifier: Modifier = Modifier,
) {
    AboutScreen(onNavigateBack = onNavigateBack, appVersion = appVersion, modifier = modifier)
}
