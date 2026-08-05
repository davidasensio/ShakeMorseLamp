package com.handysparksoft.shakelamp.feature.flashlight.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

/** :app owns the back stack; this is the only destination that exists so far. */
@Serializable
data object FlashlightRoute : NavKey

@Composable
fun FlashlightEntry(modifier: Modifier = Modifier) {
    val viewModel: FlashlightViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FlashlightScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}
