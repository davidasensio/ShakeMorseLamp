package com.handysparksoft.shakelamp.feature.flashlight.ui

import app.cash.turbine.test
import com.handysparksoft.shakelamp.core.common.testing.MainDispatcherExtension
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import com.handysparksoft.shakelamp.feature.flashlight.domain.ToggleFlashlightUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
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
            coEvery { toggleFlashlight(true) } returns true
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

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }
}
