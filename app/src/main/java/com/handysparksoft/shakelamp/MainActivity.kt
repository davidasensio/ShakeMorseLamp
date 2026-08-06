package com.handysparksoft.shakelamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.feature.flashlight.ui.FlashlightEntry
import com.handysparksoft.shakelamp.feature.flashlight.ui.FlashlightRoute
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ThemePreferenceRepository
import com.handysparksoft.shakelamp.feature.settings.ui.AboutEntry
import com.handysparksoft.shakelamp.feature.settings.ui.AboutRoute
import com.handysparksoft.shakelamp.feature.settings.ui.AppVersionInfo
import com.handysparksoft.shakelamp.feature.settings.ui.SettingsEntry
import com.handysparksoft.shakelamp.feature.settings.ui.SettingsRoute
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShakeMorseLampApp()
        }
    }
}

@Composable
private fun ShakeMorseLampApp() {
    val themeMode by koinInject<ThemePreferenceRepository>()
        .observeThemeMode()
        .collectAsStateWithLifecycle(initialValue = ThemeMode.AUTO)
    val darkTheme =
        when (themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.AUTO -> isSystemInDarkTheme()
        }
    ShakeMorseLampTheme(darkTheme = darkTheme) {
        val backStack = rememberNavBackStack(FlashlightRoute)
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize(),
            entryProvider =
                entryProvider {
                    entry<FlashlightRoute> {
                        FlashlightEntry(onNavigateToSettings = { backStack.add(SettingsRoute) })
                    }
                    entry<SettingsRoute> {
                        SettingsEntry(
                            onNavigateBack = { backStack.removeLastOrNull() },
                            onNavigateToAbout = { backStack.add(AboutRoute) },
                        )
                    }
                    entry<AboutRoute> {
                        AboutEntry(
                            onNavigateBack = { backStack.removeLastOrNull() },
                            appVersion = AppVersionInfo(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
                        )
                    }
                },
        )
    }
}
