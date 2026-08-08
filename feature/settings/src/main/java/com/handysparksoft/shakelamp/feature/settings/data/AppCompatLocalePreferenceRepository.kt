package com.handysparksoft.shakelamp.feature.settings.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import com.handysparksoft.shakelamp.feature.settings.domain.LocalePreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

private val SUPPORTED_LOCALE_TAGS = listOf("en", "ca", "de", "es", "fr", "it", "pt")

/**
 * Backed directly by AppCompat's per-app language storage - not DataStore, since the OS/AppCompat
 * layer is already the single source of truth for the applied locale (unlike ThemeMode, which has
 * no OS-level equivalent and has always been app-owned).
 */
@Single
class AppCompatLocalePreferenceRepository(
    private val context: Context,
) : LocalePreferenceRepository {
    private val selectedLocaleTagFlow = MutableStateFlow(currentLocaleTag())

    override fun observeSelectedLocaleTag(): Flow<String?> = selectedLocaleTagFlow.asStateFlow()

    override fun supportedLocaleTags(): List<String> = SUPPORTED_LOCALE_TAGS

    override suspend fun setLocaleTag(tag: String?) {
        val locales = tag?.let { LocaleListCompat.forLanguageTags(it) } ?: LocaleListCompat.getEmptyLocaleList()
        AppCompatDelegate.setApplicationLocales(locales)
        selectedLocaleTagFlow.value = tag
    }

    private fun currentLocaleTag(): String? {
        val locales = LocaleManagerCompat.getApplicationLocales(context)
        return if (locales.isEmpty) null else locales[0]?.toLanguageTag()
    }
}
