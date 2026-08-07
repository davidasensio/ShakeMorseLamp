package com.handysparksoft.shakelamp.autooff

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.handysparksoft.shakelamp.MainActivity
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Owns the auto-off countdown itself, rather than delegating it to `FlashlightViewModel`'s
 * `viewModelScope`. A foreground service only keeps the *process* out of Android's app-freezer —
 * it does nothing to keep a ViewModel (and its coroutine scope) alive if the Activity it's scoped
 * to gets torn down while backgrounded (OEM battery managers, "Don't keep activities", low-memory
 * reclaim). Confirmed on-device: the flashlight stayed on past a 3-minute timer with the screen
 * locked despite this service's notification staying up, because the countdown itself lived in
 * the ViewModel. Matches the pattern already used by ShakeDetectionService/SosTransmissionService,
 * which likewise own their loop directly instead of relying on a ViewModel to survive.
 */
class AutoOffKeepAliveService : Service() {
    private val flashlightRepository: FlashlightRepository by inject()
    private val autoOffState: AutoOffState by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var countdownJob: Job? = null

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
        val minutes = intent?.getIntExtra(EXTRA_MINUTES, 0) ?: 0
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
        )
        startCountdown(minutes)
        return START_NOT_STICKY
    }

    /**
     * Ticks against [SystemClock.elapsedRealtime] rather than decrementing a counter by
     * `TICK_MILLIS` each loop — a screen-off, foreground-serviced process still isn't guaranteed
     * full-speed CPU scheduling, so a `delay(1000)` can quietly overrun its nominal duration.
     * Decrementing a counter lets that jitter compound over dozens of ticks (confirmed on-device:
     * a 1-minute timer took roughly twice as long to fire); recomputing the remaining time from
     * the real clock on every iteration self-corrects instead of accumulating drift.
     */
    private fun startCountdown(minutes: Int) {
        countdownJob?.cancel()
        if (minutes <= 0) return
        countdownJob =
            scope.launch {
                val endAtElapsedRealtime = SystemClock.elapsedRealtime() + minutes * MILLIS_PER_MINUTE
                var remainingMillis = endAtElapsedRealtime - SystemClock.elapsedRealtime()
                autoOffState.update(remainingMillis)
                while (remainingMillis > 0) {
                    delay(minOf(TICK_MILLIS, remainingMillis))
                    remainingMillis = (endAtElapsedRealtime - SystemClock.elapsedRealtime()).coerceAtLeast(0)
                    autoOffState.update(remainingMillis)
                }
                autoOffState.update(null)
                flashlightRepository.setTorchEnabled(false)
                stopSelf()
            }
    }

    override fun onDestroy() {
        countdownJob?.cancel()
        autoOffState.update(null)
        scope.cancel()
        super.onDestroy()
    }

    private fun ensureChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, "Auto-off timer", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Persistent status while the flashlight auto-off timer is counting down"
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
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Auto-off timer active")
            .setContentText("The flashlight will turn off automatically")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .build()
    }

    companion object {
        const val EXTRA_MINUTES = "com.handysparksoft.shakelamp.autooff.EXTRA_MINUTES"
        private const val CHANNEL_ID = "auto_off_timer"
        private const val NOTIFICATION_ID = 1002
        private const val MILLIS_PER_MINUTE = 60_000L
        private const val TICK_MILLIS = 1_000L
    }
}
