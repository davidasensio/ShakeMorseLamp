package com.handysparksoft.shakelamp.feature.settings.data

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.core.app.LocaleManagerCompat
import com.handysparksoft.shakelamp.feature.settings.domain.LocalePreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single
import java.util.Locale

private val SUPPORTED_LOCALE_TAGS = listOf("en", "ca", "de", "es", "fr", "it", "pt")

/**
 * Per-app language backed by the platform [LocaleManager] (API 33+), which is the single source of
 * truth - the OS owns the value, so nothing is mirrored into DataStore.
 *
 * Deliberately not routed through `AppCompatDelegate.setApplicationLocales()`: that resolves its
 * `LocaleManager` by walking AppCompat's registry of live delegates, and this app's activities are
 * plain `ComponentActivity`, so the registry is always empty and the call silently does nothing.
 */
@Single
class SystemLocalePreferenceRepository(
    private val context: Context,
) : LocalePreferenceRepository {
    private val selectedLocaleTagFlow = MutableStateFlow(currentLocaleTag())

    override fun isPerAppLanguageSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    override fun observeSelectedLocaleTag(): Flow<String?> = selectedLocaleTagFlow.asStateFlow()

    /**
     * Resolved from the override first, then the system locale. Deliberately not read from
     * `context.resources.configuration`: this holds the *application* context, whose configuration
     * keeps the base/system locale and never picks up a per-app override (only activities do).
     */
    override fun currentDisplayLocaleTag(): String {
        val locale =
            LocaleManagerCompat.getApplicationLocales(context).takeIf { !it.isEmpty }?.get(0)
                ?: LocaleManagerCompat.getSystemLocales(context)[0]
                ?: Locale.getDefault()
        return locale.toLanguageTag()
    }

    override fun supportedLocaleTags(): List<String> = SUPPORTED_LOCALE_TAGS

    override suspend fun setLocaleTag(tag: String?) {
        val localeManager = localeManager() ?: return
        val locales =
            tag
                ?.takeIf { it.isNotBlank() }
                ?.let(LocaleList::forLanguageTags)
                ?: LocaleList.getEmptyLocaleList()
        localeManager.applicationLocales = locales
        selectedLocaleTagFlow.value = tag
    }

    private fun currentLocaleTag(): String? {
        val locales = LocaleManagerCompat.getApplicationLocales(context)
        return if (locales.isEmpty) null else locales[0]?.toLanguageTag()
    }

    private fun localeManager(): LocaleManager? =
        if (isPerAppLanguageSupported()) {
            context.getSystemService(LocaleManager::class.java)
        } else {
            null
        }
}
