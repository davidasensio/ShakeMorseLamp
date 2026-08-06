package com.handysparksoft.shakelamp.feature.flashlight.domain

import kotlinx.coroutines.flow.Flow

interface MorseHistoryRepository {
    fun observeHistory(): Flow<List<String>>

    suspend fun addMessage(text: String)
}
