package com.handysparksoft.shakelamp.feature.flashlight.domain

import kotlinx.coroutines.flow.Flow

interface AutoOffServiceController {
    fun start(minutes: Int)

    fun stop()

    fun observeRemainingMillis(): Flow<Long?>
}
