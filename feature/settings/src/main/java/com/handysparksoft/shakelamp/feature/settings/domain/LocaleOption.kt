package com.handysparksoft.shakelamp.feature.settings.domain

/** One selectable app language. [tag] `null` represents "follow the system language". */
data class LocaleOption(
    val tag: String?,
    val label: String,
    val flagEmoji: String,
)
