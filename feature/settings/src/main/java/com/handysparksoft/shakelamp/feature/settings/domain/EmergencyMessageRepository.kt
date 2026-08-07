package com.handysparksoft.shakelamp.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface EmergencyMessageRepository {
    fun observeMessage(): Flow<String>

    suspend fun setMessage(message: String)
}
