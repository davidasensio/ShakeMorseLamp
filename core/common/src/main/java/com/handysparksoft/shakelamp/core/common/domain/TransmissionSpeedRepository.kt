package com.handysparksoft.shakelamp.core.common.domain

import kotlinx.coroutines.flow.Flow

interface TransmissionSpeedRepository {
    fun observeSpeedWpm(): Flow<Int>

    suspend fun setSpeedWpm(wpm: Int)
}
