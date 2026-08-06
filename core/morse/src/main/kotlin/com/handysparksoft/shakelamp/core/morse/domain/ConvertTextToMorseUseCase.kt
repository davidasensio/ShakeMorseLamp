package com.handysparksoft.shakelamp.core.morse.domain

import org.koin.core.annotation.Factory
import java.text.Normalizer
import java.util.Locale

@Factory
class ConvertTextToMorseUseCase {
    private val dictionary by lazy {
        MorseEntries
            .forGroup(MorseEntryGroup.All)
            .associate { entry -> entry.label.first() to entry.morse }
    }

    operator fun invoke(text: String): String {
        val normalizedText =
            text
                .stripAccents()
                .lowercase(Locale.ROOT)
        if (normalizedText.isBlank()) {
            return ""
        }

        val tokens =
            buildList {
                normalizedText.forEach { char ->
                    if (char.isWhitespace()) {
                        if (lastOrNull() != WORD_SEPARATOR) add(WORD_SEPARATOR)
                    } else {
                        dictionary[char.uppercaseChar()]?.let { add(it) }
                    }
                }
            }

        return tokens.joinToString(separator = TOKEN_SEPARATOR)
    }
}

private fun String.stripAccents(): String =
    Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace(DIACRITIC_MARKS_REGEX, "")

private const val WORD_SEPARATOR = "/"
private const val TOKEN_SEPARATOR = " "
private val DIACRITIC_MARKS_REGEX = Regex("\\p{InCombiningDiacriticalMarks}+")
