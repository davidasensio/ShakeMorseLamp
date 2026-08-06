package com.handysparksoft.shakelamp.core.morse.domain

data class MorseTimingConfig(
    val speedWpm: Int,
) {
    fun normalized(): MorseTimingConfig = copy(speedWpm = MorseTimingDefaults.coerceWpm(speedWpm))

    fun withSpeed(speedWpm: Int): MorseTimingConfig = copy(speedWpm = speedWpm).normalized()
}

data class ResolvedMorseTiming(
    val dotMillis: Long,
    val dashMillis: Long,
    val intraCharacterGapMillis: Long,
    val interCharacterGapMillis: Long,
    val wordGapMillis: Long,
)

object MorseTimingDefaults {
    const val MIN_WPM = 5
    const val MAX_WPM = 40
    const val MAX_TORCH_WPM = 15
    const val DEFAULT_WPM = 15

    fun coerceWpm(speedWpm: Int): Int = speedWpm.coerceIn(MIN_WPM, MAX_WPM)

    /** Torch strobing has a real hardware ceiling the camera can't reliably exceed. */
    fun effectiveTorchTiming(config: MorseTimingConfig): MorseTimingConfig =
        config.withSpeed(config.speedWpm.coerceAtMost(MAX_TORCH_WPM))

    fun defaultConfig(): MorseTimingConfig = MorseTimingConfig(DEFAULT_WPM).normalized()
}
