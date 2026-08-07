package com.handysparksoft.shakelamp.sos

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

/** Shared in-process between [SosTransmissionService] and `SosTileService` — both live in `:app`. */
@Single
class SosTransmissionState {
    private val running = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = running.asStateFlow()

    fun update(value: Boolean) {
        running.value = value
    }
}
