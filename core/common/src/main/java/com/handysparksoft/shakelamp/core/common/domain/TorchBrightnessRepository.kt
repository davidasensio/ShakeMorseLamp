package com.handysparksoft.shakelamp.core.common.domain

import kotlinx.coroutines.flow.Flow

interface TorchBrightnessRepository {
    /** 1 when strength control isn't available (API<33, no camera flash, or hardware caps at 1). */
    fun maxStrengthLevel(): Int

    fun observeStrengthLevel(): Flow<Int>

    suspend fun setStrengthLevel(level: Int)
}
