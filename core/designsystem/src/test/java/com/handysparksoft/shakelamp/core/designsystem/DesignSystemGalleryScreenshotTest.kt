package com.handysparksoft.shakelamp.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.handysparksoft.shakelamp.core.designsystem.component.SMLButtonPreview
import com.handysparksoft.shakelamp.core.designsystem.component.SMLCardPreview
import com.handysparksoft.shakelamp.core.designsystem.component.SMLChipPreview
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSliderPreview
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSwitchPreview
import com.handysparksoft.shakelamp.core.designsystem.component.SMLTextFieldPreview
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Captures each component's own `@PreviewLightDark` composable, once per UI mode. There's no
 * Android emulator in this workflow, so these screenshots are the actual visual verification
 * that both themes look like the Stitch design — one screenshot per component per mode,
 * rather than hand-rolled gallery content that would drift from what Android Studio's
 * Preview shows. The "+night" qualifier is what `@PreviewLightDark` itself toggles to render
 * its dark variant; ShakeMorseLampTheme reads it via isSystemInDarkTheme() by default.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class DesignSystemGalleryScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttonLight() = captureComponent("SMLButton_light") { SMLButtonPreview() }

    @Config(qualifiers = "+night")
    @Test
    fun buttonDark() = captureComponent("SMLButton_dark") { SMLButtonPreview() }

    @Test
    fun textFieldLight() = captureComponent("SMLTextField_light") { SMLTextFieldPreview() }

    @Config(qualifiers = "+night")
    @Test
    fun textFieldDark() = captureComponent("SMLTextField_dark") { SMLTextFieldPreview() }

    @Test
    fun cardLight() = captureComponent("SMLCard_light") { SMLCardPreview() }

    @Config(qualifiers = "+night")
    @Test
    fun cardDark() = captureComponent("SMLCard_dark") { SMLCardPreview() }

    @Test
    fun switchLight() = captureComponent("SMLSwitch_light") { SMLSwitchPreview() }

    @Config(qualifiers = "+night")
    @Test
    fun switchDark() = captureComponent("SMLSwitch_dark") { SMLSwitchPreview() }

    @Test
    fun sliderLight() = captureComponent("SMLSlider_light") { SMLSliderPreview() }

    @Config(qualifiers = "+night")
    @Test
    fun sliderDark() = captureComponent("SMLSlider_dark") { SMLSliderPreview() }

    @Test
    fun chipLight() = captureComponent("SMLChip_light") { SMLChipPreview() }

    @Config(qualifiers = "+night")
    @Test
    fun chipDark() = captureComponent("SMLChip_dark") { SMLChipPreview() }

    private fun captureComponent(
        name: String,
        content: @Composable () -> Unit,
    ) {
        composeTestRule.setContent(content)
        composeTestRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }
}
