package com.handysparksoft.shakelamp.core.morse.domain

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class DefaultMorsePlaybackEngineTest {
    private val timingPlanner = MorseTimingPlanner()
    private val timing = MorseTimingCalculator.resolve(MorseTimingConfig(speedWpm = 20))

    @Test
    fun `play completes when the driver succeeds`() =
        runTest {
            val driver = FakeMorseOutputDriver(available = true, shouldFail = false)
            val engine = DefaultMorsePlaybackEngine(timingPlanner, driver)

            val result = engine.play(morse = "...", timing = timing)

            assertInstanceOf(PlaybackResult.Completed::class.java, result)
            assertEquals(true, driver.emitted)
        }

    @Test
    fun `play fails when the driver is unavailable`() =
        runTest {
            val driver = FakeMorseOutputDriver(available = false, shouldFail = false)
            val engine = DefaultMorsePlaybackEngine(timingPlanner, driver)

            val result = engine.play(morse = "...", timing = timing)

            assertInstanceOf(PlaybackResult.Failed::class.java, result)
            assertEquals(false, driver.emitted)
        }

    @Test
    fun `play reports failure when the driver throws`() =
        runTest {
            val driver = FakeMorseOutputDriver(available = true, shouldFail = true)
            val engine = DefaultMorsePlaybackEngine(timingPlanner, driver)

            val result = engine.play(morse = "...", timing = timing)

            assertInstanceOf(PlaybackResult.Failed::class.java, result)
        }

    @Test
    fun `play completes immediately for blank morse`() =
        runTest {
            val driver = FakeMorseOutputDriver(available = true, shouldFail = false)
            val engine = DefaultMorsePlaybackEngine(timingPlanner, driver)

            val result = engine.play(morse = "   ", timing = timing)

            assertInstanceOf(PlaybackResult.Completed::class.java, result)
            assertEquals(false, driver.emitted)
        }

    @Test
    fun `play reports full progress once emitted`() =
        runTest {
            val driver = FakeMorseOutputDriver(available = true, shouldFail = false)
            val engine = DefaultMorsePlaybackEngine(timingPlanner, driver)
            val progressUpdates = mutableListOf<Float>()

            engine.play(morse = "...", timing = timing, onProgress = { progressUpdates.add(it) })

            assertEquals(1f, progressUpdates.last())
        }
}

private class FakeMorseOutputDriver(
    private val available: Boolean,
    private val shouldFail: Boolean,
) : MorseOutputDriver {
    var emitted = false
        private set

    override fun isAvailable(): Boolean = available

    override suspend fun emit(
        plan: MorsePlaybackPlan,
        onStepCompleted: (MorsePlaybackStep) -> Unit,
    ) {
        emitted = true
        check(!shouldFail) { "Simulated driver failure" }
        plan.steps.forEach { step -> onStepCompleted(step) }
    }

    override fun stop() = Unit
}
