package com.handysparksoft.shakelamp.shake

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.handysparksoft.shakelamp.MainActivity
import com.handysparksoft.shakelamp.core.common.domain.ShakeDetector
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.core.morse.domain.MorseTimingDefaults
import com.handysparksoft.shakelamp.core.morse.domain.SendMorseMessageUseCase
import com.handysparksoft.shakelamp.feature.flashlight.domain.ToggleFlashlightUseCase
import com.handysparksoft.shakelamp.feature.settings.domain.EmergencyMessageRepository
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeSettings
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Keeps listening for shake gestures while backgrounded. A foreground service (with its required
 * persistent notification) is the only thing that survives Android's app-freezer through a screen
 * lock — see docs/ROADMAP.md's "Known Constraints" section, confirmed the hard way while building
 * the auto-off timer.
 */
class ShakeDetectionService : Service() {
    private val shakeDetector: ShakeDetector by inject()
    private val shakeSettingsRepository: ShakeSettingsRepository by inject()
    private val emergencyMessageRepository: EmergencyMessageRepository by inject()
    private val toggleFlashlight: ToggleFlashlightUseCase by inject()
    private val sendMorseMessage: SendMorseMessageUseCase by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var loopJob: Job? = null
    private var isActiveState = false
    private var isRunning = false

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
            scope.launch {
                deactivate()
                shakeSettingsRepository.setEnabled(false)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            return START_NOT_STICKY
        }
        if (!isRunning) {
            isRunning = true
            startForegroundLoop()
        }
        return START_STICKY
    }

    private fun startForegroundLoop() {
        val settings = shakeSettingsRepository.observeSettings().stateIn(scope, SharingStarted.Eagerly, ShakeSettings())
        val message = emergencyMessageRepository.observeMessage().stateIn(scope, SharingStarted.Eagerly, "")

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(settings.value.mode),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
        )

        scope.launch {
            settings.map { it.mode }.distinctUntilChanged().collect { mode ->
                updateNotification(mode)
            }
        }
        scope.launch {
            settings.map { it.sensitivityLevel }.distinctUntilChanged()
                .flatMapLatest { level -> shakeDetector.observeShakes(level) }
                .collect { onShakeDetected(settings.value.mode, message.value) }
        }
    }

    /** Shake toggles: activates if idle, deactivates (and stops any emergency loop) if already active. */
    private fun onShakeDetected(
        mode: ShakeMode,
        message: String,
    ) {
        if (isActiveState) deactivate() else activate(mode, message)
    }

    private fun activate(
        mode: ShakeMode,
        message: String,
    ) {
        isActiveState = true
        if (mode == ShakeMode.NORMAL || message.isBlank()) {
            scope.launch { toggleFlashlight(true) }
        } else {
            loopJob =
                scope.launch {
                    do {
                        sendMorseMessage(message, MorseTimingDefaults.DEFAULT_WPM)
                    } while (isActive && isActiveState)
                }
        }
    }

    private fun deactivate() {
        isActiveState = false
        loopJob?.cancel()
        loopJob = null
        scope.launch { toggleFlashlight(false) }
    }

    override fun onDestroy() {
        deactivate()
        scope.cancel()
        super.onDestroy()
    }

    private fun ensureChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, "Shake detection", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Persistent status while shake-to-turn-on is listening in the background"
            }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(mode: ShakeMode): Notification {
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
                Intent(this, ShakeDetectionService::class.java).setAction(ACTION_STOP),
                PendingIntent.FLAG_IMMUTABLE,
            )
        val text =
            if (mode == ShakeMode.EMERGENCY) {
                "Double-shake to start or stop your emergency message"
            } else {
                "Double-shake to toggle the flashlight"
            }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Shake detection active")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_stop, "Disable", stopIntent)
            .build()
    }

    private fun updateNotification(mode: ShakeMode) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(mode))
    }

    private companion object {
        const val ACTION_STOP = "com.handysparksoft.shakelamp.shake.ACTION_STOP"
        const val CHANNEL_ID = "shake_detection"
        const val NOTIFICATION_ID = 1001
    }
}
