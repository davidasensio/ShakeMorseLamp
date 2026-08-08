package com.handysparksoft.shakelamp

import android.app.Application
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.feature.flashlight.ui.FlashlightScreen
import com.handysparksoft.shakelamp.feature.flashlight.ui.FlashlightUiState
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode
import com.handysparksoft.shakelamp.feature.settings.ui.SettingsScreen
import com.handysparksoft.shakelamp.feature.settings.ui.SettingsUiState
import com.handysparksoft.shakelamp.widget.WidgetConfigScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Raw Play Store listing screenshots for the app's three screens - one light, one dark, default
 * state only. `:app` is the only module with visibility into all three: `FlashlightScreen` and
 * `SettingsScreen` are public composables from their feature modules, and `WidgetConfigScreen`
 * lives here directly. Output lands in docs/google-play/images/screenshots, one `../` above this
 * module's own directory (Roborazzi resolves capture paths relative to the module running the
 * test, same as DesignSystemGalleryScreenshotTest writing under core/designsystem/src/test/).
 *
 * Robolectric otherwise instantiates the real ShakeMorseLampApplication for an :app unit test,
 * whose onCreate() calls startKoin() - unnecessary here since every composable below is called
 * directly with a hand-built UiState (no koinViewModel()), and it throws
 * KoinApplicationAlreadyStartedException the second time a test method recreates it. Pointing
 * Robolectric at the plain base Application skips that entirely.
 *
 * The device is pinned to a Pixel 6 profile (`RobolectricDeviceQualifiers.Pixel6`, 411x914dp)
 * instead of Robolectric's tiny default window, so captures are Play-Store-realistic and tall
 * enough to fit most screens in one shot. Settings still overflows even at that height, so its
 * two extra `*_scrolled_*` tests set `initialScroll = Int.MAX_VALUE` on the screen's scroll
 * state to capture the remaining content scrolled to the bottom.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = RobolectricDeviceQualifiers.Pixel6, application = Application::class)
class PlayStoreScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun flashlightLight() = captureScreen("flashlight_light") { FlashlightPreviewContent() }

    @Config(qualifiers = "+night")
    @Test
    fun flashlightDark() = captureScreen("flashlight_dark") { FlashlightPreviewContent() }

    @Test
    fun settingsLight() = captureScreen("settings_light") { SettingsPreviewContent() }

    @Config(qualifiers = "+night")
    @Test
    fun settingsDark() = captureScreen("settings_dark") { SettingsPreviewContent() }

    @Test
    fun settingsScrolledLight() =
        captureScreen("settings_scrolled_light") { SettingsPreviewContent(initialScroll = Int.MAX_VALUE) }

    @Config(qualifiers = "+night")
    @Test
    fun settingsScrolledDark() =
        captureScreen("settings_scrolled_dark") { SettingsPreviewContent(initialScroll = Int.MAX_VALUE) }

    @Test
    fun widgetConfigLight() = captureScreen("widget_config_light") { WidgetConfigScreen(onAddToHomeScreen = {}) }

    @Config(qualifiers = "+night")
    @Test
    fun widgetConfigDark() = captureScreen("widget_config_dark") { WidgetConfigScreen(onAddToHomeScreen = {}) }

    private fun captureScreen(
        name: String,
        content: @Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            ShakeMorseLampTheme {
                Surface { content() }
            }
        }
        composeTestRule.onRoot().captureRoboImage("../docs/google-play/images/screenshots/en_$name.png")
    }
}

@Composable
private fun FlashlightPreviewContent() {
    FlashlightScreen(
        uiState =
            FlashlightUiState(
                isOn = true,
                isAvailable = true,
                timerMinutes = 30,
                autoOffRemainingMillis = 20 * 60_000L,
                morseMessage = "SOS",
                isLoopEnabled = true,
                sentMessageHistory = listOf("SOS", "MEET AT DAWN", "OK"),
                isHistoryExpanded = true,
            ),
        onAction = {},
        onNavigateToSettings = {},
    )
}

@Composable
private fun SettingsPreviewContent(initialScroll: Int = 0) {
    SettingsScreen(
        uiState =
            SettingsUiState(
                themeMode = ThemeMode.LIGHT,
                emergencyMessage = "SOS",
                isStrobeActive = false,
                isLanguagePickerSupported = true,
            ),
        onAction = {},
        onNavigateBack = {},
        onNavigateToAbout = {},
        onNavigateToLanguage = {},
        initialScroll = initialScroll,
    )
}
