package com.handysparksoft.shakelamp.feature.flashlight.domain

interface FlashlightRepository {
    fun isFlashAvailable(): Boolean

    /** @return true if the torch mode was applied, false on failure (e.g. camera unavailable). */
    suspend fun setTorchEnabled(enabled: Boolean): Boolean
}
