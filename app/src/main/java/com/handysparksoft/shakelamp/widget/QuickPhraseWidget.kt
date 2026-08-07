package com.handysparksoft.shakelamp.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.handysparksoft.shakelamp.core.designsystem.R

private val WidgetBackground = Color(0xFF121508)
private val WidgetAccent = Color(0xFFCBF200)
private const val LABEL_MAX_CHARS = 10

/** The widget always renders in its own dark palette, regardless of the system theme. */
private fun fixedColor(color: Color) = ColorProvider(day = color, night = color)

class QuickPhraseWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            val config = currentState<Preferences>().widgetConfig()
            QuickPhraseContent(config)
        }
    }
}

@Composable
private fun QuickPhraseContent(config: WidgetConfig) {
    Box(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .background(fixedColor(WidgetBackground))
                .cornerRadius(24.dp)
                .clickable(actionRunCallback<TransmitAction>()),
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_flashlight_on),
                contentDescription = config.message,
                colorFilter = ColorFilter.tint(fixedColor(WidgetAccent)),
                modifier = GlanceModifier.size(36.dp),
            )
            Text(
                text = config.message.take(LABEL_MAX_CHARS),
                maxLines = 1,
                style =
                    TextStyle(
                        color = fixedColor(WidgetAccent),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    ),
            )
        }
        if (config.loopEnabled) {
            Box(
                modifier = GlanceModifier.fillMaxSize().padding(6.dp),
                contentAlignment = Alignment.TopEnd,
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_loop),
                    contentDescription = "Loop enabled",
                    colorFilter = ColorFilter.tint(fixedColor(WidgetBackground)),
                    modifier =
                        GlanceModifier
                            .size(16.dp)
                            .background(fixedColor(WidgetAccent))
                            .cornerRadius(8.dp)
                            .padding(2.dp),
                )
            }
        }
    }
}
