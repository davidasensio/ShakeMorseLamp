package com.handysparksoft.shakelamp.widget

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.handysparksoft.shakelamp.MainActivity
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.morse.domain.MorseTimingDefaults
import com.handysparksoft.shakelamp.core.morse.domain.SendMorseMessageUseCase
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import com.handysparksoft.shakelamp.R as AppR

/**
 * Runs a single home screen widget tap: sends a morse phrase once or on loop until stopped. A
 * foreground service is required for the same reason as SosTransmissionService (see
 * docs/ROADMAP.md's Known Constraints) - a widget ActionCallback's execution window is too short
 * for a multi-second transmission or a loop.
 *
 * Only one widget's transmission runs at a time - a new start cancels whatever was running before,
 * matching [WidgetTransmissionState] tracking a single active widget instance.
 */
class WidgetTransmissionService : Service() {
    private val sendMorseMessage: SendMorseMessageUseCase by inject()
    private val flashlightRepository: FlashlightRepository by inject()
    private val transmissionState: WidgetTransmissionState by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        ensureChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        if (intent?.action == ACTION_STOP) {
            stopRun()
            return START_NOT_STICKY
        }
        val message = intent?.getStringExtra(EXTRA_MESSAGE) ?: return START_NOT_STICKY
        val loop = intent.getBooleanExtra(EXTRA_LOOP, false)
        job?.cancel()
        startRun(message, loop)
        return START_STICKY
    }

    private fun startRun(
        message: String,
        loop: Boolean,
    ) {
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(message, loop),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
        )
        job =
            scope.launch {
                if (loop) {
                    do {
                        sendMorseMessage(message, MorseTimingDefaults.DEFAULT_WPM)
                    } while (isActive)
                } else {
                    // TorchOutputDriver.emit() guarantees the torch is off once this returns,
                    // even on cancellation - no need to turn it off again here.
                    sendMorseMessage(message, MorseTimingDefaults.DEFAULT_WPM)
                    finishRun()
                }
            }
    }

    private fun stopRun() {
        job?.cancel()
        job = null
        scope.launch { flashlightRepository.setTorchEnabled(false) }
        finishRun()
    }

    private fun finishRun() {
        transmissionState.clear()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        job?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    private fun ensureChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(AppR.string.notification_widget_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = getString(AppR.string.notification_widget_channel_description)
            }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(
        message: String,
        loop: Boolean,
    ): Notification {
        val contentIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE,
            )
        val stopIntent =
            PendingIntent.getService(
                this,
                0,
                Intent(this, WidgetTransmissionService::class.java).setAction(ACTION_STOP),
                PendingIntent.FLAG_IMMUTABLE,
            )
        val text =
            if (loop) {
                getString(AppR.string.notification_widget_text_looping, message)
            } else {
                getString(AppR.string.notification_widget_text, message)
            }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(getString(AppR.string.notification_widget_title))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_stop, getString(AppR.string.notification_stop_action), stopIntent)
            .build()
    }

    companion object {
        const val EXTRA_MESSAGE = "com.handysparksoft.shakelamp.widget.EXTRA_MESSAGE"
        const val EXTRA_LOOP = "com.handysparksoft.shakelamp.widget.EXTRA_LOOP"
        const val ACTION_STOP = "com.handysparksoft.shakelamp.widget.ACTION_STOP"
        private const val CHANNEL_ID = "widget_transmission"
        private const val NOTIFICATION_ID = 1004
    }
}
