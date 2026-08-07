package com.handysparksoft.shakelamp.widget

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/** What a single home screen widget instance is configured to transmit. */
data class WidgetConfig(val message: String, val loopEnabled: Boolean) {
    companion object {
        const val DEFAULT_MESSAGE = "SOS"
        const val MAX_MESSAGE_LENGTH = 120
    }
}

private val MessageKey: Preferences.Key<String> = stringPreferencesKey("widget_message")
private val LoopEnabledKey: Preferences.Key<Boolean> = booleanPreferencesKey("widget_loop_enabled")

fun Preferences.widgetConfig(): WidgetConfig =
    WidgetConfig(
        message = this[MessageKey] ?: WidgetConfig.DEFAULT_MESSAGE,
        loopEnabled = this[LoopEnabledKey] ?: false,
    )

fun MutablePreferences.setWidgetConfig(config: WidgetConfig) {
    this[MessageKey] = config.message
    this[LoopEnabledKey] = config.loopEnabled
}
