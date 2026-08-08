package com.handysparksoft.shakelamp.feature.flashlight.ui

import app.cash.turbine.test
import com.handysparksoft.shakelamp.core.common.domain.HapticFeedbackRepository
import com.handysparksoft.shakelamp.core.common.domain.LoopPauseRepository
import com.handysparksoft.shakelamp.core.common.domain.TransmissionSpeedRepository
import com.handysparksoft.shakelamp.core.common.testing.MainDispatcherExtension
import com.handysparksoft.shakelamp.core.morse.domain.MorseTimingDefaults
import com.handysparksoft.shakelamp.core.morse.domain.PlaybackResult
import com.handysparksoft.shakelamp.core.morse.domain.SendMorseMessageUseCase
import com.handysparksoft.shakelamp.feature.flashlight.domain.AutoOffServiceController
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import com.handysparksoft.shakelamp.feature.flashlight.domain.MorseHistoryRepository
import com.handysparksoft.shakelamp.feature.flashlight.domain.ToggleFlashlightUseCase
import com.handysparksoft.shakelamp.feature.flashlight.domain.WidgetPinRequester
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class FlashlightViewModelTest {
    private val repository = mockk<FlashlightRepository>()
    private val toggleFlashlight = mockk<ToggleFlashlightUseCase>()
    private val sendMorseMessage = mockk<SendMorseMessageUseCase>()
    private val historyRepository = FakeMorseHistoryRepository()
    private val autoOffServiceController = mockk<AutoOffServiceController>(relaxUnitFun = true)
    private val hapticFeedbackRepository = mockk<HapticFeedbackRepository>()
    private val loopPauseRepository = mockk<LoopPauseRepository>()
    private val transmissionSpeedRepository = mockk<TransmissionSpeedRepository>()
    private val widgetPinRequester = mockk<WidgetPinRequester>(relaxUnitFun = true)

    @BeforeEach
    fun setUp() {
        every { repository.isFlashAvailable() } returns true
        coEvery { toggleFlashlight(true) } returns true
        coEvery { toggleFlashlight(false) } returns true
        coEvery { sendMorseMessage(any(), any(), any()) } returns PlaybackResult.Completed
        every { hapticFeedbackRepository.observeEnabled() } returns MutableStateFlow(true)
        every { loopPauseRepository.observePauseMillis() } returns MutableStateFlow(2_000L)
        every { transmissionSpeedRepository.observeSpeedWpm() } returns
            MutableStateFlow(MorseTimingDefaults.DEFAULT_WPM)
        every { repository.observeTorchState() } returns MutableStateFlow(false)
        every { autoOffServiceController.observeRemainingMillis() } returns MutableStateFlow(null)
        every { widgetPinRequester.isSupported() } returns true
    }

    @Test
    fun `initial state reflects flash unavailability`() =
        runTest {
            every { repository.isFlashAvailable() } returns false
            val viewModel = newViewModel()

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(false, state.isAvailable)
                assertEquals(false, state.isOn)
            }
        }

    @Test
    fun `TogglePower turns flashlight on when successful`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.uiState.test {
                assertEquals(false, awaitItem().isOn)
                viewModel.onAction(FlashlightUiAction.TogglePower)
                assertEquals(true, awaitItem().isOn)
            }
        }

    @Test
    fun `TogglePower emits error when use case fails`() =
        runTest {
            coEvery { toggleFlashlight(true) } returns false
            val viewModel = newViewModel()

            viewModel.uiEvent.test {
                viewModel.onAction(FlashlightUiAction.TogglePower)
                assertInstanceOf(FlashlightUiEvent.ShowError::class.java, awaitItem())
            }
            assertEquals(false, viewModel.uiState.value.isOn)
        }

    @Test
    fun `TimerChanged updates minutes, keeps power off`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(FlashlightUiAction.TimerChanged(15))

            assertEquals(15, viewModel.uiState.value.timerMinutes)
            assertEquals(false, viewModel.uiState.value.isOn)
        }

    @Test
    fun `MessageChanged updates the morse message`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))

            assertEquals("SOS", viewModel.uiState.value.morseMessage)
        }

    @Test
    fun `LoopToggled flips the loop flag`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(FlashlightUiAction.LoopToggled)

            assertEquals(true, viewModel.uiState.value.isLoopEnabled)
        }

    @Test
    fun `starting the timer starts the keep-alive service`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(FlashlightUiAction.TimerChanged(5))
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()

            verify(atLeast = 1) { autoOffServiceController.start(5) }
        }

    @Test
    fun `cancelling the timer stops the keep-alive svc`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(FlashlightUiAction.TimerChanged(5))
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()

            verify(atLeast = 1) { autoOffServiceController.stop() }
        }

    @Test
    fun `changing timer while on reschedules the auto-off`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(FlashlightUiAction.TimerChanged(TEN_MINUTES))
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()

            viewModel.onAction(FlashlightUiAction.TimerChanged(2))
            runCurrent()

            verify { autoOffServiceController.start(2) }
        }

    @Test
    fun `manual toggle off cancels the scheduled auto-off`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(FlashlightUiAction.TimerChanged(5))
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()

            coVerify(exactly = 1) { toggleFlashlight(false) }
            verify(atLeast = 1) { autoOffServiceController.stop() }
        }

    @Test
    fun `auto-off remaining mirrors the service state`() =
        runTest {
            val remaining = MutableStateFlow<Long?>(null)
            every { autoOffServiceController.observeRemainingMillis() } returns remaining
            val viewModel = newViewModel()
            runCurrent()

            remaining.value = FIVE_MINUTES_MILLIS
            runCurrent()

            assertEquals(FIVE_MINUTES_MILLIS, viewModel.uiState.value.autoOffRemainingMillis)
        }

    @Test
    fun `TransmitClicked sends the message, resets after`() =
        runTest {
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            coVerify { sendMorseMessage("SOS", any(), any()) }
            assertEquals(false, viewModel.uiState.value.isTransmitting)
            assertEquals(false, viewModel.uiState.value.isOn)
        }

    @Test
    fun `TransmitClicked with a blank message is a no-op`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            assertEquals(false, viewModel.uiState.value.isTransmitting)
            coVerify(exactly = 0) { sendMorseMessage(any(), any(), any()) }
        }

    @Test
    fun `TransmitClicked again stops the transmission`() =
        runTest {
            coEvery { sendMorseMessage(any(), any(), any()) } coAnswers {
                delay(LONG_TRANSMISSION_MILLIS)
                PlaybackResult.Completed
            }
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()
            assertEquals(true, viewModel.uiState.value.isTransmitting)

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            assertEquals(false, viewModel.uiState.value.isTransmitting)
            assertEquals(false, viewModel.uiState.value.isOn)
        }

    @Test
    fun `loop mode keeps transmitting until stopped`() =
        runTest {
            coEvery { sendMorseMessage(any(), any(), any()) } coAnswers {
                delay(1)
                PlaybackResult.Completed
            }
            every { loopPauseRepository.observePauseMillis() } returns MutableStateFlow(0L)
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))
            viewModel.onAction(FlashlightUiAction.LoopToggled)

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            advanceTimeBy(LOOP_ITERATIONS_TO_OBSERVE)
            runCurrent()

            assertEquals(true, viewModel.uiState.value.isTransmitting)
            coVerify(atLeast = 2) { sendMorseMessage(any(), any(), any()) }

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            assertEquals(false, viewModel.uiState.value.isTransmitting)
        }

    @Test
    fun `transmission uses the configured speed`() =
        runTest {
            every { transmissionSpeedRepository.observeSpeedWpm() } returns MutableStateFlow(TRANSMISSION_SPEED_WPM)
            val viewModel = newViewModel()
            runCurrent()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            coVerify { sendMorseMessage("SOS", TRANSMISSION_SPEED_WPM, any()) }
        }

    @Test
    fun `loop mode waits the configured pause`() =
        runTest {
            every { loopPauseRepository.observePauseMillis() } returns MutableStateFlow(LOOP_PAUSE_MILLIS)
            val viewModel = newViewModel()
            runCurrent()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))
            viewModel.onAction(FlashlightUiAction.LoopToggled)

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()
            coVerify(exactly = 1) { sendMorseMessage(any(), any(), any()) }

            advanceTimeBy(LOOP_PAUSE_MILLIS - 1)
            runCurrent()
            coVerify(exactly = 1) { sendMorseMessage(any(), any(), any()) }

            advanceTimeBy(2)
            runCurrent()
            coVerify(exactly = 2) { sendMorseMessage(any(), any(), any()) }

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()
        }

    @Test
    fun `starting a transmission cancels the auto-off`() =
        runTest {
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.TimerChanged(5))
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            verify(atLeast = 1) { autoOffServiceController.stop() }
        }

    @Test
    fun `TogglePower is ignored during a transmission`() =
        runTest {
            coEvery { sendMorseMessage(any(), any(), any()) } coAnswers {
                delay(LONG_TRANSMISSION_MILLIS)
                PlaybackResult.Completed
            }
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))
            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()

            coVerify(exactly = 0) { toggleFlashlight(any()) }
            assertTrue(viewModel.uiState.value.isTransmitting)
        }

    @Test
    fun `sending a message records it in history`() =
        runTest {
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            assertEquals(listOf("SOS"), viewModel.uiState.value.sentMessageHistory)
        }

    @Test
    fun `history trims whitespace before recording`() =
        runTest {
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.MessageChanged("  SOS  "))

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            assertEquals(listOf("SOS"), viewModel.uiState.value.sentMessageHistory)
        }

    @Test
    fun `resending a message moves it to front, no dupe`() =
        runTest {
            val viewModel = newViewModel()
            sendAndAwait(viewModel, "SOS")
            sendAndAwait(viewModel, "OK")
            sendAndAwait(viewModel, "SOS")

            assertEquals(listOf("SOS", "OK"), viewModel.uiState.value.sentMessageHistory)
        }

    @Test
    fun `history is capped at ten entries`() =
        runTest {
            val viewModel = newViewModel()
            (1..HISTORY_OVERFLOW_COUNT).forEach { index -> sendAndAwait(viewModel, "MSG$index") }

            assertEquals(MAX_HISTORY_SIZE, viewModel.uiState.value.sentMessageHistory.size)
            assertEquals(
                "MSG$HISTORY_OVERFLOW_COUNT",
                viewModel.uiState.value.sentMessageHistory
                    .first(),
            )
        }

    @Test
    fun `HistoryToggled flips the expansion state`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(FlashlightUiAction.HistoryToggled)

            assertEquals(true, viewModel.uiState.value.isHistoryExpanded)
        }

    @Test
    fun `single-shot transmission ignores the auto-off`() =
        runTest {
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))
            viewModel.onAction(FlashlightUiAction.TimerChanged(5))

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            assertEquals(null, viewModel.uiState.value.autoOffRemainingMillis)
        }

    @Test
    fun `looping transmission stops when auto-off fires`() =
        runTest {
            coEvery { sendMorseMessage(any(), any(), any()) } coAnswers {
                delay(TRANSMISSION_STEP_MILLIS)
                PlaybackResult.Completed
            }
            val remaining = MutableStateFlow<Long?>(null)
            every { autoOffServiceController.observeRemainingMillis() } returns remaining
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))
            viewModel.onAction(FlashlightUiAction.LoopToggled)
            viewModel.onAction(FlashlightUiAction.TimerChanged(5))

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()
            assertEquals(true, viewModel.uiState.value.isTransmitting)

            remaining.value = 0L
            runCurrent()

            assertEquals(false, viewModel.uiState.value.isTransmitting)
            assertEquals(false, viewModel.uiState.value.isOn)
        }

    @Test
    fun `stopping a loop cancels its auto-off`() =
        runTest {
            coEvery { sendMorseMessage(any(), any(), any()) } coAnswers {
                delay(TRANSMISSION_STEP_MILLIS)
                PlaybackResult.Completed
            }
            val viewModel = newViewModel()
            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))
            viewModel.onAction(FlashlightUiAction.LoopToggled)
            viewModel.onAction(FlashlightUiAction.TimerChanged(5))
            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            viewModel.onAction(FlashlightUiAction.TransmitClicked)
            runCurrent()

            verify(atLeast = 1) { autoOffServiceController.stop() }
        }

    @Test
    fun `ConfigureWidgetClicked calls requestPin`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.uiEvent.test {
                viewModel.onAction(FlashlightUiAction.ConfigureWidgetClicked)
                runCurrent()

                expectNoEvents()
            }
            verify { widgetPinRequester.requestPin() }
        }

    @Test
    fun `ConfigureWidgetClicked emits instructions`() =
        runTest {
            every { widgetPinRequester.isSupported() } returns false
            val viewModel = newViewModel()

            viewModel.uiEvent.test {
                viewModel.onAction(FlashlightUiAction.ConfigureWidgetClicked)
                runCurrent()

                assertEquals(FlashlightUiEvent.ShowWidgetPinInstructions, awaitItem())
            }
            verify(exactly = 0) { widgetPinRequester.requestPin() }
        }

    private suspend fun TestScope.sendAndAwait(
        viewModel: FlashlightViewModel,
        message: String,
    ) {
        viewModel.onAction(FlashlightUiAction.MessageChanged(message))
        viewModel.onAction(FlashlightUiAction.TransmitClicked)
        runCurrent()
    }

    private fun newViewModel() =
        FlashlightViewModel(
            repository,
            toggleFlashlight,
            sendMorseMessage,
            historyRepository,
            autoOffServiceController,
            hapticFeedbackRepository,
            loopPauseRepository,
            transmissionSpeedRepository,
            widgetPinRequester,
        )

    private class FakeMorseHistoryRepository : MorseHistoryRepository {
        private val history = MutableStateFlow<List<String>>(emptyList())

        override fun observeHistory(): Flow<List<String>> = history

        override suspend fun addMessage(text: String) {
            val trimmed = text.trim()
            if (trimmed.isBlank()) return
            history.update { current ->
                (listOf(trimmed) + current.filterNot { it.equals(trimmed, ignoreCase = true) }).take(MAX_HISTORY_SIZE)
            }
        }
    }

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()

        private const val TEN_MINUTES = 10
        private const val FIVE_MINUTES_MILLIS = 5 * 60_000L
        private const val LONG_TRANSMISSION_MILLIS = 60_000L
        private const val LOOP_ITERATIONS_TO_OBSERVE = 5L
        private const val MAX_HISTORY_SIZE = 10
        private const val HISTORY_OVERFLOW_COUNT = 11
        private const val TRANSMISSION_STEP_MILLIS = 30_000L
        private const val LOOP_PAUSE_MILLIS = 5_000L
        private const val TRANSMISSION_SPEED_WPM = 8
    }
}
