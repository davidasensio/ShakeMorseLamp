package com.handysparksoft.shakelamp.core.morse.domain

import org.koin.core.annotation.Factory

@Factory
class SendMorseMessageUseCase(
    private val convertTextToMorse: ConvertTextToMorseUseCase,
    private val playbackEngine: MorsePlaybackEngine,
) {
    suspend operator fun invoke(
        text: String,
        speedWpm: Int,
        onProgress: (Float) -> Unit = {},
    ): PlaybackResult {
        val morse = convertTextToMorse(text)
        val timing =
            MorseTimingCalculator.resolve(
                MorseTimingDefaults.effectiveTorchTiming(MorseTimingConfig(speedWpm)),
            )
        return playbackEngine.play(morse = morse, timing = timing, onProgress = onProgress)
    }
}
