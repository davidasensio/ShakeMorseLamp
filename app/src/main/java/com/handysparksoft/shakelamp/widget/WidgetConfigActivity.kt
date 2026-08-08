package com.handysparksoft.shakelamp.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.designsystem.component.SMLButton
import com.handysparksoft.shakelamp.core.designsystem.component.SMLChip
import com.handysparksoft.shakelamp.core.designsystem.component.SMLSwitch
import com.handysparksoft.shakelamp.core.designsystem.component.SMLTextField
import com.handysparksoft.shakelamp.core.designsystem.theme.ShakeMorseLampTheme
import com.handysparksoft.shakelamp.core.designsystem.theme.Spacing
import kotlinx.coroutines.launch

private val WidgetBackground = Color(0xFF121508)
private val WidgetAccent = Color(0xFFCBF200)
private val QuickFillPhrases = listOf("SOS", "HELP", "OK")

/**
 * The standard Android widget configuration activity, launched by the system after the user
 * drags [QuickPhraseWidget] onto the home screen. Lets them set the message and loop behavior
 * this widget instance will transmit before it's actually placed.
 */
class WidgetConfigActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appWidgetId =
            intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            ShakeMorseLampTheme {
                WidgetConfigScreen(onAddToHomeScreen = ::finishWithSelection)
            }
        }
    }

    private fun finishWithSelection(config: WidgetConfig) {
        lifecycleScope.launch {
            val glanceId = GlanceAppWidgetManager(this@WidgetConfigActivity).getGlanceIdBy(appWidgetId)
            updateAppWidgetState(this@WidgetConfigActivity, glanceId) { prefs -> prefs.setWidgetConfig(config) }
            QuickPhraseWidget().update(this@WidgetConfigActivity, glanceId)

            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
            finish()
        }
    }
}

@Composable
private fun WidgetConfigScreen(onAddToHomeScreen: (WidgetConfig) -> Unit) {
    var message by remember { mutableStateOf(WidgetConfig.DEFAULT_MESSAGE) }
    var loopEnabled by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(Spacing.Margin),
            verticalArrangement = Arrangement.spacedBy(Spacing.ContainerPadding),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
                Text(text = "Widget Config", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Customize your home screen widget for instant access to critical signals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.Unit)) {
                Text(
                    text = "LIVE PREVIEW",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.Gutter),
                    contentAlignment = Alignment.Center,
                ) {
                    WidgetLivePreview(message = message, loopEnabled = loopEnabled)
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.Gutter),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.Unit)) {
                    Text(
                        text = "MESSAGE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SMLTextField(
                        value = message,
                        onValueChange = { message = it.uppercase().take(WidgetConfig.MAX_MESSAGE_LENGTH) },
                        placeholder = "Message",
                        onClear = { message = "" },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Unit)) {
                        QuickFillPhrases.forEach { phrase ->
                            SMLChip(label = phrase, onClick = { message = phrase })
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(text = "Loop", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Repeat the message until the widget is tapped again",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    SMLSwitch(checked = loopEnabled, onCheckedChange = { loopEnabled = it })
                }
            }

            SMLButton(
                text = "Add to Home Screen",
                onClick = {
                    val finalMessage = message.ifBlank { WidgetConfig.DEFAULT_MESSAGE }
                    onAddToHomeScreen(WidgetConfig(finalMessage, loopEnabled))
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun WidgetLivePreview(
    message: String,
    loopEnabled: Boolean,
) {
    Box(
        modifier =
            Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(WidgetBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.S),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_flashlight_on),
                contentDescription = null,
                tint = WidgetAccent,
                modifier = Modifier.size(36.dp),
            )
            Text(
                text = message.ifBlank { WidgetConfig.DEFAULT_MESSAGE },
                color = WidgetAccent,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 88.dp),
            )
        }
        if (loopEnabled) {
            Box(modifier = Modifier.fillMaxSize().padding(6.dp), contentAlignment = Alignment.TopEnd) {
                Icon(
                    painter = painterResource(R.drawable.ic_loop),
                    contentDescription = "Loop enabled",
                    tint = WidgetBackground,
                    modifier =
                        Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WidgetAccent)
                            .padding(2.dp),
                )
            }
        }
    }
}
