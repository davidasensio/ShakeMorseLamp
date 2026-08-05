package com.handysparksoft.shakelamp.feature.flashlight.domain

import org.koin.core.annotation.Factory

@Factory
class ToggleFlashlightUseCase(
    private val repository: FlashlightRepository,
) {
    suspend operator fun invoke(enabled: Boolean): Boolean = repository.setTorchEnabled(enabled)
}
