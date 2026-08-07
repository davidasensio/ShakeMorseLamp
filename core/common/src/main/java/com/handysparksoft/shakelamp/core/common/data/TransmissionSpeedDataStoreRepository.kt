package com.handysparksoft.shakelamp.core.common.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.handysparksoft.shakelamp.core.common.domain.TransmissionSpeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val Context.transmissionSpeedDataStore by preferencesDataStore(name = "transmission_speed")
private val SPEED_WPM_KEY = intPreferencesKey("speed_wpm")

@Single
class TransmissionSpeedDataStoreRepository(
    private val context: Context,
) : TransmissionSpeedRepository {
    override fun observeSpeedWpm(): Flow<Int> =
        context.transmissionSpeedDataStore.data.map { preferences ->
            preferences[SPEED_WPM_KEY] ?: DEFAULT_WPM
        }

    override suspend fun setSpeedWpm(wpm: Int) {
        context.transmissionSpeedDataStore.edit { preferences ->
            preferences[SPEED_WPM_KEY] = wpm
        }
    }

    private companion object {
        // Mirrors core/morse's MorseTimingDefaults.DEFAULT_WPM — duplicated rather than depended
        // on, since :core:common must not depend on :core:morse.
        const val DEFAULT_WPM = 10
    }
}
