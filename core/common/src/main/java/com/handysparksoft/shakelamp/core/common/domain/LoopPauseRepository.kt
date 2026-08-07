package com.handysparksoft.shakelamp.core.common.domain

import kotlinx.coroutines.flow.Flow

interface LoopPauseRepository {
    fun observePauseMillis(): Flow<Long>

    suspend fun setPauseMillis(millis: Long)
}
