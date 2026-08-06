package com.handysparksoft.shakelamp.feature.flashlight.data

import com.handysparksoft.shakelamp.core.morse.domain.TorchController
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import org.koin.core.annotation.Single

/**
 * Binds :core:morse's [TorchController] port to the existing [FlashlightRepository], so the
 * power button and a Morse transmission share the same physical torch state instead of two
 * independent CameraManager consumers.
 */
@Single
class FlashlightTorchController(
    private val repository: FlashlightRepository,
) : TorchController {
    override fun isAvailable(): Boolean = repository.isFlashAvailable()

    override suspend fun setTorchEnabled(enabled: Boolean): Boolean = repository.setTorchEnabled(enabled)
}
