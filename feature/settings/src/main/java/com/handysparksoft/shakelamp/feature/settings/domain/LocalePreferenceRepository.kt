package com.handysparksoft.shakelamp.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface LocalePreferenceRepository {
    /**
     * Whether this device can hold a per-app language override. The platform only gained that
     * ability in API 33; below it the app always follows the system language and the picker has
     * nothing to write to, so callers must hide the entry point instead of offering a no-op.
     */
    fun isPerAppLanguageSupported(): Boolean

    fun observeSelectedLocaleTag(): Flow<String?>

    /**
     * The language the app is *actually* rendering in right now, resolved from the live resource
     * configuration. Unlike [observeSelectedLocaleTag] this is never null: when no override is set
     * it reports whatever the system resolved to, which is what a "current language" label needs.
     */
    fun currentDisplayLocaleTag(): String

    fun supportedLocaleTags(): List<String>

    suspend fun setLocaleTag(tag: String?)
}
