package com.handysparksoft.shakelamp.autooff

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

/**
 * Holds no timer logic of its own — `FlashlightViewModel` already owns the countdown correctly.
 * This service exists purely to keep the process out of Android's app-freezer while backgrounded,
 * the same fix already proven for shake detection (see docs/ROADMAP.md's Known Constraints).
 */
class AutoOffKeepAliveService : Service() {
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
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
        )
        return START_NOT_STICKY
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

    private companion object {
        const val CHANNEL_ID = "auto_off_timer"
        const val NOTIFICATION_ID = 1002
    }
}
