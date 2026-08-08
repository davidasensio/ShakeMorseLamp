package com.handysparksoft.shakelamp.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface LocalePreferenceRepository {
    fun observeSelectedLocaleTag(): Flow<String?>

    fun supportedLocaleTags(): List<String>

    suspend fun setLocaleTag(tag: String?)
}
