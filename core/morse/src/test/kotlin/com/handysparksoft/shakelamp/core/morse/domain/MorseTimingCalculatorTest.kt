package com.handysparksoft.shakelamp.core.morse.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MorseTimingCalculatorTest {
    @Test
    fun `resolves the PARIS-word dot duration at 20 WPM`() {
        val timing = MorseTimingCalculator.resolve(MorseTimingConfig(speedWpm = 20))

        assertEquals(60L, timing.dotMillis)
    }

    @Test
    fun `dash is three times the dot duration`() {
        val timing = MorseTimingCalculator.resolve(MorseTimingConfig(speedWpm = 20))

        assertEquals(timing.dotMillis * 3, timing.dashMillis)
    }

    @Test
    fun `inter-character gap is 3 units, word gap is 7`() {
        val timing = MorseTimingCalculator.resolve(MorseTimingConfig(speedWpm = 20))

        assertEquals(timing.dotMillis * 3, timing.interCharacterGapMillis)
        assertEquals(timing.dotMillis * 7, timing.wordGapMillis)
    }

    @Test
    fun `speeds outside the valid range are coerced`() {
        val fast = MorseTimingCalculator.resolve(MorseTimingConfig(speedWpm = 999))
        val atMax = MorseTimingCalculator.resolve(MorseTimingConfig(speedWpm = MorseTimingDefaults.MAX_WPM))

        assertEquals(atMax, fast)
    }
}
