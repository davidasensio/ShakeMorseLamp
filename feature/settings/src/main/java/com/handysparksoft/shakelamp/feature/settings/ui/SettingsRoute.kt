package com.handysparksoft.shakelamp.feature.settings.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import com.handysparksoft.shakelamp.feature.settings.domain.SosTileRequestResult
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
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    SettingsUiEvent.ShowMissingEmergencyMessageSnackbar -> {
                        snackbarHostState.showSnackbar("Set an emergency message first")
                    }
                    is SettingsUiEvent.ShowSosTileResult -> {
                        snackbarHostState.showSnackbar(sosTileResultMessage(event.result))
                    }
                }
            }
        }
    }

    SettingsScreen(
        uiState = uiState,
        onAction = { action ->
            if (action is SettingsUiAction.ShakeEnabledToggled && !uiState.isShakeEnabled) {
                requestNotificationPermissionIfNeeded(context, notificationPermissionLauncher)
            }
            viewModel.onAction(action)
        },
        onNavigateBack = onNavigateBack,
        onNavigateToAbout = onNavigateToAbout,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}

private fun sosTileResultMessage(result: SosTileRequestResult): String =
    when (result) {
        SosTileRequestResult.ADDED -> "SOS tile added to Quick Settings"
        SosTileRequestResult.ALREADY_ADDED -> "SOS tile is already in Quick Settings"
        SosTileRequestResult.DECLINED -> "Not added — you can still add it from Quick Settings > Edit tiles"
        SosTileRequestResult.UNSUPPORTED -> "Add it manually from Quick Settings > Edit tiles"
    }

private fun requestNotificationPermissionIfNeeded(
    context: Context,
    launcher: ActivityResultLauncher<String>,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val granted =
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
}
