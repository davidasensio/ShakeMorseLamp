package com.handysparksoft.shakelamp.core.common.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.handysparksoft.shakelamp.core.common.domain.HapticFeedbackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val Context.hapticFeedbackDataStore by preferencesDataStore(name = "haptic_feedback")
private val ENABLED_KEY = booleanPreferencesKey("enabled")

@Single
class HapticFeedbackDataStoreRepository(
    private val context: Context,
) : HapticFeedbackRepository {
    override fun observeEnabled(): Flow<Boolean> =
        context.hapticFeedbackDataStore.data.map { preferences ->
            preferences[ENABLED_KEY] ?: DEFAULT_ENABLED
        }

    override suspend fun setEnabled(enabled: Boolean) {
        context.hapticFeedbackDataStore.edit { preferences ->
            preferences[ENABLED_KEY] = enabled
        }
    }

    private companion object {
        const val DEFAULT_ENABLED = true
    }
}
