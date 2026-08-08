package com.handysparksoft.shakelamp.feature.flashlight.ui

import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import com.handysparksoft.shakelamp.feature.flashlight.R
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

/** :app owns the back stack. */
@Serializable
data object FlashlightRoute : NavKey

@Composable
fun FlashlightEntry(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FlashlightViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var showWidgetPinInstructions by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent
                .filterIsInstance<FlashlightUiEvent.ShowWidgetPinInstructions>()
                .collect { showWidgetPinInstructions = true }
        }
    }

    FlashlightScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateToSettings = onNavigateToSettings,
        modifier = modifier,
    )

    if (showWidgetPinInstructions) {
        WidgetPinInstructionsDialog(
            onGoToHome = {
                showWidgetPinInstructions = false
                (context as? Activity)?.moveTaskToBack(true)
            },
            onDismiss = { showWidgetPinInstructions = false },
        )
    }
}

@Composable
private fun WidgetPinInstructionsDialog(
    onGoToHome: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.flashlight_widget_dialog_title)) },
        text = { Text(stringResource(R.string.flashlight_widget_dialog_body)) },
        confirmButton = {
            TextButton(onClick = onGoToHome) { Text(stringResource(R.string.flashlight_widget_dialog_go_home)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.flashlight_widget_dialog_stay_here)) }
        },
    )
}
