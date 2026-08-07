package com.handysparksoft.shakelamp.feature.settings.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.handysparksoft.shakelamp.feature.settings.domain.DEFAULT_SENSITIVITY_LEVEL
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeSettings
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val Context.shakeSettingsDataStore by preferencesDataStore(name = "shake_settings")
private val ENABLED_KEY = booleanPreferencesKey("enabled")
private val SENSITIVITY_KEY = intPreferencesKey("sensitivity_level")
private val MODE_KEY = stringPreferencesKey("mode")

@Single
class ShakeSettingsDataStoreRepository(
    private val context: Context,
) : ShakeSettingsRepository {
    override fun observeSettings(): Flow<ShakeSettings> =
        context.shakeSettingsDataStore.data.map { preferences ->
            ShakeSettings(
                enabled = preferences[ENABLED_KEY] ?: false,
                sensitivityLevel = preferences[SENSITIVITY_KEY] ?: DEFAULT_SENSITIVITY_LEVEL,
                mode =
                    preferences[MODE_KEY]
                        ?.let { runCatching { ShakeMode.valueOf(it) }.getOrNull() }
                        ?: ShakeMode.NORMAL,
            )
        }

    override suspend fun setEnabled(enabled: Boolean) {
        context.shakeSettingsDataStore.edit { preferences -> preferences[ENABLED_KEY] = enabled }
    }

    override suspend fun setSensitivity(level: Int) {
        context.shakeSettingsDataStore.edit { preferences -> preferences[SENSITIVITY_KEY] = level }
    }

    override suspend fun setMode(mode: ShakeMode) {
        context.shakeSettingsDataStore.edit { preferences -> preferences[MODE_KEY] = mode.name }
    }
}
