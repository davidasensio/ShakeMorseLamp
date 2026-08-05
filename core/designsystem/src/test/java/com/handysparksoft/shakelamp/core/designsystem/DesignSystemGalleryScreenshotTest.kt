package com.handysparksoft.shakelamp.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.handysparksoft.shakelamp.core.designsystem.component.LumenButton
import com.handysparksoft.shakelamp.core.designsystem.component.LumenButtonVariant
import com.handysparksoft.shakelamp.core.designsystem.component.LumenCard
import com.handysparksoft.shakelamp.core.designsystem.component.LumenSwitch
import com.handysparksoft.shakelamp.core.designsystem.component.LumenTextField
import com.handysparksoft.shakelamp.core.designsystem.theme.LumenSpacing
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Renders every base component together as a visual gallery. There's no Android
 * emulator in this workflow, so this screenshot is the verification that the
 * components actually look like the Stitch design once composed together.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class DesignSystemGalleryScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gallery() {
        composeTestRule.setContent {
            ShakeMorseLampTheme {
                Surface { GalleryContent() }
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/screenshots/designsystem_gallery.png")
    }
}

@Composable
private fun GalleryContent() {
    var switchOn by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(LumenSpacing.Margin),
        verticalArrangement = Arrangement.spacedBy(LumenSpacing.Gutter),
    ) {
        LumenCard {
            Column(verticalArrangement = Arrangement.spacedBy(LumenSpacing.Unit)) {
                Text("Morse Broadcast")
                LumenTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = "Enter message to broadcast...",
                    modifier = Modifier.fillMaxWidth(),
                )
                LumenButton(
                    text = "Transmit Signal",
                    onClick = {},
                    variant = LumenButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
                LumenButton(
                    text = "Configure Widget",
                    onClick = {},
                    variant = LumenButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        LumenCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Shake to Turn On")
                LumenSwitch(checked = switchOn, onCheckedChange = { switchOn = it })
            }
        }
    }
}
