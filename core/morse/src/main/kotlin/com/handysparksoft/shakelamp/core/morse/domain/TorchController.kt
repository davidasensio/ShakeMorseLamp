package com.handysparksoft.shakelamp.core.morse.domain

/**
 * Port for the physical torch. `:core:morse` can't depend on `:feature:flashlight`, so the
 * concrete implementation is supplied by whichever module already owns torch hardware access —
 * keeping a single source of truth for torch state instead of a second, independent
 * CameraManager consumer.
 */
interface TorchController {
    fun isAvailable(): Boolean

    suspend fun setTorchEnabled(enabled: Boolean): Boolean
}
