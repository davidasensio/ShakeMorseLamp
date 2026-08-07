package com.handysparksoft.shakelamp.feature.settings.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.handysparksoft.shakelamp.feature.settings.domain.EmergencyMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val Context.emergencyMessageDataStore by preferencesDataStore(name = "emergency_message")
private val MESSAGE_KEY = stringPreferencesKey("message")

@Single
class EmergencyMessageDataStoreRepository(
    private val context: Context,
) : EmergencyMessageRepository {
    override fun observeMessage(): Flow<String> =
        context.emergencyMessageDataStore.data.map { preferences ->
            preferences[MESSAGE_KEY] ?: DEFAULT_MESSAGE
        }

    override suspend fun setMessage(message: String) {
        context.emergencyMessageDataStore.edit { preferences ->
            preferences[MESSAGE_KEY] = message
        }
    }

    private companion object {
        const val DEFAULT_MESSAGE = "SOS"
    }
}
