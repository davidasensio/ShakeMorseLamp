package com.handysparksoft.shakelamp.sos

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
import com.handysparksoft.shakelamp.feature.settings.domain.EmergencyMessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import com.handysparksoft.shakelamp.R as AppR

/**
 * Loops the configured emergency Morse message until stopped. A foreground service is required to
 * survive a screen lock — see docs/ROADMAP.md's Known Constraints, the same fix already proven for
 * shake detection and the auto-off timer.
 */
class SosTransmissionService : Service() {
    private val emergencyMessageRepository: EmergencyMessageRepository by inject()
    private val sendMorseMessage: SendMorseMessageUseCase by inject()
    private val flashlightRepository: FlashlightRepository by inject()
    private val transmissionState: SosTransmissionState by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var loopJob: Job? = null

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
            stopLoop()
            return START_NOT_STICKY
        }
        if (loopJob == null) startLoop()
        return START_STICKY
    }

    private fun startLoop() {
        transmissionState.update(true)
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
        )
        loopJob =
            scope.launch {
                val message = emergencyMessageRepository.observeMessage().first().ifBlank { DEFAULT_MESSAGE }
                do {
                    sendMorseMessage(message, MorseTimingDefaults.DEFAULT_WPM)
                    delay(LOOP_PAUSE_MILLIS)
                } while (isActive)
            }
    }

    private fun stopLoop() {
        loopJob?.cancel()
        loopJob = null
        transmissionState.update(false)
        scope.launch { flashlightRepository.setTorchEnabled(false) }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        loopJob?.cancel()
        transmissionState.update(false)
        scope.cancel()
        super.onDestroy()
    }

    private fun ensureChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(AppR.string.notification_sos_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = getString(AppR.string.notification_sos_channel_description)
            }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
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
                Intent(this, SosTransmissionService::class.java).setAction(ACTION_STOP),
                PendingIntent.FLAG_IMMUTABLE,
            )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(getString(AppR.string.notification_sos_title))
            .setContentText(getString(AppR.string.notification_sos_text))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_stop, getString(AppR.string.notification_stop_action), stopIntent)
            .build()
    }

    companion object {
        const val ACTION_STOP = "com.handysparksoft.shakelamp.sos.ACTION_STOP"
        private const val CHANNEL_ID = "sos_transmission"
        private const val NOTIFICATION_ID = 1003
        private const val DEFAULT_MESSAGE = "SOS"

        /**
         * Fixed gap between repetitions, so the loop reads as a distinct repeated signal rather
         * than one continuous strobe. Deliberately not the user's Transmission "Pause Between
         * Loops" setting: the tile is a one-tap distress control and stays predictable.
         */
        private const val LOOP_PAUSE_MILLIS = 3_000L
    }
}
