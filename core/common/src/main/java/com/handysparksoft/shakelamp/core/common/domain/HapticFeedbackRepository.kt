package com.handysparksoft.shakelamp.core.common.domain

import kotlinx.coroutines.flow.Flow

interface HapticFeedbackRepository {
    fun observeEnabled(): Flow<Boolean>

    suspend fun setEnabled(enabled: Boolean)
}
