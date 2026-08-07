package com.handysparksoft.shakelamp.feature.settings.domain

import kotlinx.coroutines.flow.Flow

data class ShakeSettings(
    val enabled: Boolean = false,
    val sensitivityLevel: Int = DEFAULT_SENSITIVITY_LEVEL,
    val mode: ShakeMode = ShakeMode.NORMAL,
)

const val DEFAULT_SENSITIVITY_LEVEL = 2

interface ShakeSettingsRepository {
    fun observeSettings(): Flow<ShakeSettings>

    suspend fun setEnabled(enabled: Boolean)

    suspend fun setSensitivity(level: Int)

    suspend fun setMode(mode: ShakeMode)
}
