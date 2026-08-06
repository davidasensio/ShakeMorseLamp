package com.handysparksoft.shakelamp.core.morse.domain

interface MorseOutputDriver {
    fun isAvailable(): Boolean

    suspend fun emit(
        plan: MorsePlaybackPlan,
        onStepCompleted: (MorsePlaybackStep) -> Unit,
    )

    fun stop()
}
