package com.handysparksoft.shakelamp.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.handysparksoft.shakelamp.core.designsystem.component.SMLButtonPreview
import com.handysparksoft.shakelamp.core.designsystem.component.SMLCardPreview
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSwitchPreview
import com.handysparksoft.shakelamp.core.designsystem.component.SMLTextFieldPreview
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Captures each component's own `@PreviewLightDark` composable. There's no Android emulator
 * in this workflow, so these screenshots are the actual visual verification that components
 * look like the Stitch design — one screenshot per component's existing preview, rather than
 * hand-rolled gallery content that would drift from what Android Studio's Preview shows.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class DesignSystemGalleryScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun button() = captureComponent("SMLButton") { SMLButtonPreview() }

    @Test
    fun textField() = captureComponent("SMLTextField") { SMLTextFieldPreview() }

    @Test
    fun card() = captureComponent("SMLCard") { SMLCardPreview() }

    @Test
    fun switch() = captureComponent("SMLSwitch") { SMLSwitchPreview() }

    private fun captureComponent(
        name: String,
        content: @Composable () -> Unit,
    ) {
        composeTestRule.setContent(content)
        composeTestRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }
}
