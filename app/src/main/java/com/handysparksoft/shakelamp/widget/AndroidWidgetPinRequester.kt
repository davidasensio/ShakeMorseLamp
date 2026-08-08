package com.handysparksoft.shakelamp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.handysparksoft.shakelamp.feature.flashlight.domain.WidgetPinRequester
import org.koin.core.annotation.Single

@Single
class AndroidWidgetPinRequester(
    private val context: Context,
) : WidgetPinRequester {
    private val appWidgetManager: AppWidgetManager
        get() = AppWidgetManager.getInstance(context)

    override fun isSupported(): Boolean = appWidgetManager.isRequestPinAppWidgetSupported

    override fun requestPin() {
        val provider = ComponentName(context, QuickPhraseWidgetReceiver::class.java)
        // requestPinAppWidget does NOT auto-launch the provider's configure activity the way
        // the manual "drag from widget tray" flow does - that's only triggered here via this
        // success callback, which the system fires with EXTRA_APPWIDGET_ID filled in once the
        // user confirms placement.
        val successCallback =
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, WidgetConfigActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )
        appWidgetManager.requestPinAppWidget(provider, null, successCallback)
    }
}
