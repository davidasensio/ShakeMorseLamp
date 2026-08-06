package com.handysparksoft.shakelamp.feature.flashlight.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.handysparksoft.shakelamp.feature.flashlight.domain.MorseHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

private val Context.morseHistoryDataStore by preferencesDataStore(name = "morse_history")
private val HISTORY_KEY = stringPreferencesKey("sent_message_history")

@Single
class MorseHistoryDataStoreRepository(
    private val context: Context,
) : MorseHistoryRepository {
    override fun observeHistory(): Flow<List<String>> =
        context.morseHistoryDataStore.data.map { preferences -> preferences.decodeHistory() }

    override suspend fun addMessage(text: String) {
        context.morseHistoryDataStore.edit { preferences ->
            val updated =
                (listOf(text) + preferences.decodeHistory().filterNot { it.equals(text, ignoreCase = true) })
                    .take(MAX_HISTORY_SIZE)
            preferences[HISTORY_KEY] = Json.encodeToString(updated)
        }
    }

    private fun Preferences.decodeHistory(): List<String> =
        this[HISTORY_KEY]?.let { raw ->
            runCatching { Json.decodeFromString<List<String>>(raw) }.getOrDefault(emptyList())
        } ?: emptyList()

    private companion object {
        const val MAX_HISTORY_SIZE = 10
    }
}
