package com.handysparksoft.shakelamp.widget

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/** Tapping a widget starts its configured transmission - tapping it again while it's running stops it. */
class TransmitAction : ActionCallback, KoinComponent {
    private val transmissionState: WidgetTransmissionState by inject()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        if (transmissionState.isActive(glanceId)) {
            val stopIntent =
                Intent(context, WidgetTransmissionService::class.java).setAction(WidgetTransmissionService.ACTION_STOP)
            context.startService(stopIntent)
            return
        }

        val state = getAppWidgetState<Preferences>(context, PreferencesGlanceStateDefinition, glanceId)
        val config = state.widgetConfig()
        transmissionState.start(glanceId)

        val startIntent =
            Intent(context, WidgetTransmissionService::class.java)
                .putExtra(WidgetTransmissionService.EXTRA_MESSAGE, config.message)
                .putExtra(WidgetTransmissionService.EXTRA_LOOP, config.loopEnabled)
        ContextCompat.startForegroundService(context, startIntent)
    }
}
