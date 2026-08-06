package com.handysparksoft.shakelamp.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface ThemePreferenceRepository {
    fun observeThemeMode(): Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)
}
