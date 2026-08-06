package com.handysparksoft.shakelamp.feature.settings.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ThemePreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val Context.settingsDataStore by preferencesDataStore(name = "settings")
private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

@Single
class ThemePreferenceDataStoreRepository(
    private val context: Context,
) : ThemePreferenceRepository {
    override fun observeThemeMode(): Flow<ThemeMode> =
        context.settingsDataStore.data.map { preferences ->
            preferences[THEME_MODE_KEY]
                ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.AUTO
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
}
