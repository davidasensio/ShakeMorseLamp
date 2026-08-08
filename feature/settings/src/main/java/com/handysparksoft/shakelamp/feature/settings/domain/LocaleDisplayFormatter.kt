package com.handysparksoft.shakelamp.feature.settings.domain

import java.util.Locale

/** Shared display formatting for locale tags, used by both the Language picker and Settings. */
object LocaleDisplayFormatter {
    const val SYSTEM_DEFAULT_FLAG_EMOJI = "🌐"

    fun displayNameIn(
        tag: String,
        displayLocale: Locale,
    ): String {
        val name = Locale.forLanguageTag(tag).getDisplayName(displayLocale)
        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(displayLocale) else it.toString() }
    }

    fun flagEmojiFor(tag: String?): String =
        when (tag) {
            "en" -> "🇺🇸"
            "es" -> "🇪🇸"
            "pt" -> "🇵🇹"
            "it" -> "🇮🇹"
            "fr" -> "🇫🇷"
            "de" -> "🇩🇪"
            "ca" -> "🇪🇸"
            else -> SYSTEM_DEFAULT_FLAG_EMOJI
        }
}
