package com.handysparksoft.shakelamp.core.morse.domain

import kotlinx.coroutines.CancellationException
import org.koin.core.annotation.Single

@Single
class DefaultMorsePlaybackEngine(
    private val timingPlanner: MorseTimingPlanner,
    private val torchOutputDriver: MorseOutputDriver,
) : MorsePlaybackEngine {
    override suspend fun play(
        morse: String,
        timing: ResolvedMorseTiming,
        onProgress: (Float) -> Unit,
    ): PlaybackResult {
        // Playback requests are exclusive. Stopping first prevents overlapping output
        // if the caller retries before a previous transmission has fully wound down.
        stop()

        val plan = timingPlanner.createPlan(morse = morse, timing = timing)

        if (plan.signalCount == 0) {
            onProgress(1f)
            return PlaybackResult.Completed
        }

        if (!torchOutputDriver.isAvailable()) {
            return PlaybackResult.Failed
        }

        return if (runPlayback(plan, onProgress)) PlaybackResult.Completed else PlaybackResult.Failed
    }

    override fun stop() {
        torchOutputDriver.stop()
    }

    private suspend fun runPlayback(
        plan: MorsePlaybackPlan,
        onProgress: (Float) -> Unit,
    ): Boolean {
        var completedSignals = 0
        val result =
            runCatching {
                onProgress(0f)
                torchOutputDriver.emit(plan) { step ->
                    // Only count real dot/dash steps as progress. Gaps are timing detail,
                    // not user-visible progress in the playback UI.
                    if (!step.isMorseSignal) return@emit
                    completedSignals += 1
                    onProgress(completedSignals / plan.signalCount.toFloat())
                }
                onProgress(1f)
            }

        result.exceptionOrNull()?.let { throwable ->
            if (throwable is CancellationException) throw throwable
        }

        return result.isSuccess
    }
}
