package com.handysparksoft.shakelamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.feature.flashlight.ui.FlashlightEntry
import com.handysparksoft.shakelamp.feature.flashlight.ui.FlashlightRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShakeMorseLampTheme {
                ShakeMorseLampApp()
            }
        }
    }
}

@Composable
private fun ShakeMorseLampApp() {
    val backStack = rememberNavBackStack(FlashlightRoute)
    NavDisplay(
        backStack = backStack,
        modifier = Modifier.fillMaxSize(),
        entryProvider = entryProvider {
            entry<FlashlightRoute> { FlashlightEntry() }
        },
    )
}
