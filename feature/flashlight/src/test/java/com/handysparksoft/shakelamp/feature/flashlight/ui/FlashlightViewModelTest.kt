package com.handysparksoft.shakelamp.feature.flashlight.ui

import app.cash.turbine.test
import com.handysparksoft.shakelamp.core.common.testing.MainDispatcherExtension
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import com.handysparksoft.shakelamp.feature.flashlight.domain.ToggleFlashlightUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class FlashlightViewModelTest {
    private val repository = mockk<FlashlightRepository>()
    private val toggleFlashlight = mockk<ToggleFlashlightUseCase>()

    @BeforeEach
    fun setUp() {
        every { repository.isFlashAvailable() } returns true
        coEvery { toggleFlashlight(true) } returns true
        coEvery { toggleFlashlight(false) } returns true
    }

    @Test
    fun `initial state reflects flash unavailability`() =
        runTest {
            every { repository.isFlashAvailable() } returns false
            val viewModel = FlashlightViewModel(repository, toggleFlashlight)

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(false, state.isAvailable)
                assertEquals(false, state.isOn)
            }
        }

    @Test
    fun `TogglePower turns flashlight on when successful`() =
        runTest {
            val viewModel = FlashlightViewModel(repository, toggleFlashlight)

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
            val viewModel = FlashlightViewModel(repository, toggleFlashlight)

            viewModel.uiEvent.test {
                viewModel.onAction(FlashlightUiAction.TogglePower)
                assertInstanceOf(FlashlightUiEvent.ShowError::class.java, awaitItem())
            }
            assertEquals(false, viewModel.uiState.value.isOn)
        }

    @Test
    fun `TimerChanged updates minutes, keeps power off`() =
        runTest {
            val viewModel = FlashlightViewModel(repository, toggleFlashlight)

            viewModel.onAction(FlashlightUiAction.TimerChanged(15))

            assertEquals(15, viewModel.uiState.value.timerMinutes)
            assertEquals(false, viewModel.uiState.value.isOn)
        }

    @Test
    fun `MessageChanged updates the morse message`() =
        runTest {
            val viewModel = FlashlightViewModel(repository, toggleFlashlight)

            viewModel.onAction(FlashlightUiAction.MessageChanged("SOS"))

            assertEquals("SOS", viewModel.uiState.value.morseMessage)
        }

    @Test
    fun `LoopToggled flips the loop flag`() =
        runTest {
            val viewModel = FlashlightViewModel(repository, toggleFlashlight)

            viewModel.onAction(FlashlightUiAction.LoopToggled)

            assertEquals(true, viewModel.uiState.value.isLoopEnabled)
        }

    @Test
    fun `flashlight auto-offs after the timer duration`() =
        runTest {
            val viewModel = FlashlightViewModel(repository, toggleFlashlight)

            viewModel.onAction(FlashlightUiAction.TimerChanged(5))
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()
            assertEquals(true, viewModel.uiState.value.isOn)

            advanceTimeBy(FIVE_MINUTES_MILLIS + 1)
            runCurrent()

            assertEquals(false, viewModel.uiState.value.isOn)
            coVerify(exactly = 1) { toggleFlashlight(false) }
        }

    @Test
    fun `changing timer while on reschedules the auto-off`() =
        runTest {
            val viewModel = FlashlightViewModel(repository, toggleFlashlight)

            viewModel.onAction(FlashlightUiAction.TimerChanged(TEN_MINUTES))
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()

            viewModel.onAction(FlashlightUiAction.TimerChanged(2))
            advanceTimeBy(TEN_MINUTES_MILLIS + 1)
            runCurrent()

            assertEquals(false, viewModel.uiState.value.isOn)
            coVerify(exactly = 1) { toggleFlashlight(false) }
        }

    @Test
    fun `manual toggle off cancels the scheduled auto-off`() =
        runTest {
            val viewModel = FlashlightViewModel(repository, toggleFlashlight)

            viewModel.onAction(FlashlightUiAction.TimerChanged(5))
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()
            viewModel.onAction(FlashlightUiAction.TogglePower)
            runCurrent()

            advanceTimeBy(FIVE_MINUTES_MILLIS + 1)
            runCurrent()

            coVerify(exactly = 1) { toggleFlashlight(false) }
        }

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()

        private const val TEN_MINUTES = 10
        private const val FIVE_MINUTES_MILLIS = 5 * 60_000L
        private const val TEN_MINUTES_MILLIS = TEN_MINUTES * 60_000L
    }
}
