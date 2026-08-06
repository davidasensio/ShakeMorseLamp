package com.handysparksoft.shakelamp.feature.flashlight.data

import android.content.Context
import android.os.PowerManager
import com.handysparksoft.shakelamp.feature.flashlight.domain.AutoOffWakeLock
import org.koin.core.annotation.Single

@Single
class PowerManagerAutoOffWakeLock(
    context: Context,
) : AutoOffWakeLock {
    private val wakeLock: PowerManager.WakeLock =
        (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG)

    override fun acquire(timeoutMillis: Long) {
        wakeLock.acquire(timeoutMillis)
    }

    override fun release() {
        if (wakeLock.isHeld) wakeLock.release()
    }

    private companion object {
        const val WAKE_LOCK_TAG = "ShakeMorseLamp:AutoOffTimer"
    }
}
