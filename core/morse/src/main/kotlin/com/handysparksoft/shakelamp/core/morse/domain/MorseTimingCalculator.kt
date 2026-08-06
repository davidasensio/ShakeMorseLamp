package com.handysparksoft.shakelamp.core.morse.domain

import kotlin.math.roundToLong

object MorseTimingCalculator {
    fun resolve(config: MorseTimingConfig): ResolvedMorseTiming {
        val unitMillis = wpmToUnitMillis(config.normalized().speedWpm)

        return ResolvedMorseTiming(
            dotMillis = unitMillis,
            dashMillis = unitMillis * DASH_UNITS,
            intraCharacterGapMillis = unitMillis,
            interCharacterGapMillis = unitMillis * LETTER_GAP_UNITS,
            wordGapMillis = unitMillis * WORD_GAP_UNITS,
        )
    }

    private fun wpmToUnitMillis(speedWpm: Int): Long =
        // Standard Morse timing is derived from the 50-unit word "PARIS", which yields
        // the well-known dot formula 1200 / WPM in milliseconds.
        (PARIS_WORD_MILLIS / MorseTimingDefaults.coerceWpm(speedWpm).toDouble())
            .roundToLong()
            .coerceAtLeast(MIN_DURATION_MILLIS)
}

private const val PARIS_WORD_MILLIS = 1200.0
private const val DASH_UNITS = 3L
private const val LETTER_GAP_UNITS = 3L
private const val WORD_GAP_UNITS = 7L
private const val MIN_DURATION_MILLIS = 1L
