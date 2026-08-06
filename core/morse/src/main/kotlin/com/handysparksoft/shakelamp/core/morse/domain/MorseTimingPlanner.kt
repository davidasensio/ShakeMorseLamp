package com.handysparksoft.shakelamp.core.morse.domain

import org.koin.core.annotation.Factory

@Factory
class MorseTimingPlanner {
    fun createPlan(
        morse: String,
        timing: ResolvedMorseTiming,
    ): MorsePlaybackPlan {
        val tokens =
            morse
                .trim()
                .split(TOKEN_SEPARATOR_REGEX)
                .filter { token -> token.isNotBlank() }

        if (tokens.isEmpty()) {
            return MorsePlaybackPlan(steps = emptyList(), signalCount = 0)
        }

        val steps = mutableListOf<MorsePlaybackStep>()
        var signalCount = 0

        tokens.forEachIndexed { index, token ->
            if (token == WORD_TOKEN) return@forEachIndexed

            val emittedInToken = appendTokenSteps(token, timing, steps)
            signalCount += emittedInToken
            if (emittedInToken == 0) return@forEachIndexed

            appendGapStep(tokens, index, timing, steps)
        }

        trimTrailingSilence(steps)

        return MorsePlaybackPlan(steps = steps, signalCount = signalCount)
    }

    private fun appendTokenSteps(
        token: String,
        timing: ResolvedMorseTiming,
        steps: MutableList<MorsePlaybackStep>,
    ): Int {
        var emittedSignals = 0
        token.forEachIndexed { symbolIndex, symbol ->
            val signalDurationMillis = symbol.toSignalDurationMillis(timing) ?: return@forEachIndexed
            steps +=
                MorsePlaybackStep(
                    isSignalOn = true,
                    durationMillis = signalDurationMillis,
                    isMorseSignal = true,
                )
            emittedSignals += 1

            if (symbolIndex < token.lastIndex) {
                steps +=
                    MorsePlaybackStep(
                        isSignalOn = false,
                        durationMillis = timing.intraCharacterGapMillis,
                        isMorseSignal = false,
                    )
            }
        }
        return emittedSignals
    }

    private fun appendGapStep(
        tokens: List<String>,
        index: Int,
        timing: ResolvedMorseTiming,
        steps: MutableList<MorsePlaybackStep>,
    ) {
        val nextToken = tokens.getOrNull(index + 1) ?: return
        // The parser uses "/" as an explicit word separator token so the planner can
        // emit a 7-unit word gap without embedding spacing rules in the morse string.
        val gapDurationMillis =
            if (nextToken == WORD_TOKEN) {
                timing.wordGapMillis
            } else {
                timing.interCharacterGapMillis
            }
        steps += MorsePlaybackStep(isSignalOn = false, durationMillis = gapDurationMillis, isMorseSignal = false)
    }

    private fun trimTrailingSilence(steps: MutableList<MorsePlaybackStep>) {
        // Trailing silence is not part of the emitted symbol sequence and would only
        // add dead air at the end of playback.
        while (steps.lastOrNull()?.isSignalOn == false) {
            steps.removeAt(steps.lastIndex)
        }
    }
}

data class MorsePlaybackPlan(
    val steps: List<MorsePlaybackStep>,
    val signalCount: Int,
)

data class MorsePlaybackStep(
    val isSignalOn: Boolean,
    val durationMillis: Long,
    val isMorseSignal: Boolean,
)

private fun Char.toSignalDurationMillis(timing: ResolvedMorseTiming): Long? =
    when (this) {
        DOT_SYMBOL -> timing.dotMillis
        DASH_SYMBOL -> timing.dashMillis
        else -> null
    }

private const val DOT_SYMBOL = '.'
private const val DASH_SYMBOL = '-'
private const val WORD_TOKEN = "/"
private val TOKEN_SEPARATOR_REGEX = Regex("\\s+")
