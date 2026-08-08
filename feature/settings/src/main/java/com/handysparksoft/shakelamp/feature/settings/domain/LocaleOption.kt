package com.handysparksoft.shakelamp.feature.settings.domain

/**
 * One selectable app language. [tag] `null` represents "follow the system language". [label] is
 * the language's name rendered in the app's current display locale; [nativeLabel] is its name
 * rendered in itself (e.g. "German" vs "Deutsch").
 */
data class LocaleOption(
    val tag: String?,
    val label: String,
    val nativeLabel: String,
    val flagEmoji: String,
)
