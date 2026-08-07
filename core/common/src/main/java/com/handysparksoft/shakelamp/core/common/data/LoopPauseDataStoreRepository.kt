package com.handysparksoft.shakelamp.core.common.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.handysparksoft.shakelamp.core.common.domain.LoopPauseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val Context.loopPauseDataStore by preferencesDataStore(name = "loop_pause")
private val PAUSE_MILLIS_KEY = longPreferencesKey("pause_millis")

@Single
class LoopPauseDataStoreRepository(
    private val context: Context,
) : LoopPauseRepository {
    override fun observePauseMillis(): Flow<Long> =
        context.loopPauseDataStore.data.map { preferences ->
            preferences[PAUSE_MILLIS_KEY] ?: DEFAULT_PAUSE_MILLIS
        }

    override suspend fun setPauseMillis(millis: Long) {
        context.loopPauseDataStore.edit { preferences ->
            preferences[PAUSE_MILLIS_KEY] = millis
        }
    }

    private companion object {
        const val DEFAULT_PAUSE_MILLIS = 2_000L
    }
}
