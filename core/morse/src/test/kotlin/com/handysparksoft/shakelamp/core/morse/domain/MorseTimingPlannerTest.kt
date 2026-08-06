package com.handysparksoft.shakelamp.core.morse.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MorseTimingPlannerTest {
    private val planner = MorseTimingPlanner()
    private lateinit var timing: ResolvedMorseTiming

    @BeforeEach
    fun setUp() {
        timing = MorseTimingCalculator.resolve(MorseTimingConfig(speedWpm = 20))
    }

    @Test
    fun `SOS produces one on-step per dot and dash`() {
        val plan = planner.createPlan(morse = "... --- ...", timing = timing)

        assertEquals(9, plan.signalCount)
    }

    @Test
    fun `letters in a word use the inter-character gap`() {
        val plan = planner.createPlan(morse = "... --- ...", timing = timing)

        val gapAfterFirstLetter = plan.steps[5]
        assertEquals(false, gapAfterFirstLetter.isSignalOn)
        assertEquals(timing.interCharacterGapMillis, gapAfterFirstLetter.durationMillis)
    }

    @Test
    fun `symbols in a letter use the intra-character gap`() {
        val plan = planner.createPlan(morse = "...", timing = timing)

        val gapBetweenDots = plan.steps[1]
        assertEquals(false, gapBetweenDots.isSignalOn)
        assertEquals(timing.intraCharacterGapMillis, gapBetweenDots.durationMillis)
    }

    @Test
    fun `word separator produces the wider word gap`() {
        val plan = planner.createPlan(morse = ".- / .-", timing = timing)

        val wordGapStep = plan.steps.first { step -> step.durationMillis == timing.wordGapMillis }
        assertEquals(false, wordGapStep.isSignalOn)
    }

    @Test
    fun `trailing word separator leaves no dead air`() {
        val plan = planner.createPlan(morse = "... --- ... /", timing = timing)

        assertTrue(plan.steps.last().isSignalOn)
    }

    @Test
    fun `blank morse produces an empty plan`() {
        val plan = planner.createPlan(morse = "   ", timing = timing)

        assertEquals(0, plan.signalCount)
        assertTrue(plan.steps.isEmpty())
    }
}
