package com.handysparksoft.shakelamp.feature.flashlight.domain

/** Keeps the CPU awake for the duration of an active auto-off countdown. */
interface AutoOffWakeLock {
    fun acquire(timeoutMillis: Long)

    fun release()
}
