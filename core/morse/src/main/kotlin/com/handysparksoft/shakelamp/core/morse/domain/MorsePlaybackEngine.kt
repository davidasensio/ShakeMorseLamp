package com.handysparksoft.shakelamp.core.morse.domain

interface MorsePlaybackEngine {
    suspend fun play(
        morse: String,
        timing: ResolvedMorseTiming,
        onProgress: (Float) -> Unit = {},
    ): PlaybackResult

    fun stop()
}
