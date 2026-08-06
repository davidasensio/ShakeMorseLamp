package com.handysparksoft.shakelamp.feature.settings.ui

import com.handysparksoft.shakelamp.core.common.domain.TorchBrightnessRepository
import com.handysparksoft.shakelamp.core.common.testing.MainDispatcherExtension
import com.handysparksoft.shakelamp.core.morse.domain.PlaybackResult
import com.handysparksoft.shakelamp.core.morse.domain.SendMorseMessageUseCase
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ThemePreferenceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class SettingsViewModelTest {
    private val themePreferenceRepository = FakeThemePreferenceRepository()
    private val sendMorseMessage = mockk<SendMorseMessageUseCase>()
    private val torchBrightnessRepository = FakeTorchBrightnessRepository()

    @BeforeEach
    fun setUp() {
        coEvery { sendMorseMessage(any(), any(), any()) } returns PlaybackResult.Completed
    }

    @Test
    fun `initial state reflects persisted theme mode`() =
        runTest {
            themePreferenceRepository.seed(ThemeMode.DARK)
            val viewModel = newViewModel()
            runCurrent()

            assertEquals(ThemeMode.DARK, viewModel.uiState.value.themeMode)
        }

    @Test
    fun `ThemeModeChanged persists and updates state`() =
        runTest {
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(SettingsUiAction.ThemeModeChanged(ThemeMode.LIGHT))
            runCurrent()

            assertEquals(ThemeMode.LIGHT, viewModel.uiState.value.themeMode)
            assertEquals(ThemeMode.LIGHT, themePreferenceRepository.current())
        }

    @Test
    fun `EmergencyMessageChanged updates state`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(SettingsUiAction.EmergencyMessageChanged("HELP"))

            assertEquals("HELP", viewModel.uiState.value.emergencyMessage)
        }

    @Test
    fun `StrobeToggled loops until toggled off again`() =
        runTest {
            coEvery { sendMorseMessage(any(), any(), any()) } coAnswers {
                delay(1)
                PlaybackResult.Completed
            }
            val viewModel = newViewModel()

            viewModel.onAction(SettingsUiAction.StrobeToggled)
            advanceTimeBy(LOOP_ITERATIONS_TO_OBSERVE)
            runCurrent()

            assertTrue(viewModel.uiState.value.isStrobeActive)
            coVerify(atLeast = 2) { sendMorseMessage(any(), any(), any()) }

            viewModel.onAction(SettingsUiAction.StrobeToggled)
            runCurrent()

            assertFalse(viewModel.uiState.value.isStrobeActive)
        }

    @Test
    fun `StrobeToggled with a blank message is a no-op`() =
        runTest {
            val viewModel = newViewModel()
            viewModel.onAction(SettingsUiAction.EmergencyMessageChanged(""))

            viewModel.onAction(SettingsUiAction.StrobeToggled)
            runCurrent()

            assertFalse(viewModel.uiState.value.isStrobeActive)
            coVerify(exactly = 0) { sendMorseMessage(any(), any(), any()) }
        }

    @Test
    fun `initial state reflects persisted dimmer level`() =
        runTest {
            torchBrightnessRepository.seed(initialLevel = 3, max = 5)
            val viewModel = newViewModel()
            runCurrent()

            assertEquals(3, viewModel.uiState.value.dimmerLevel)
            assertEquals(5, viewModel.uiState.value.dimmerMaxLevel)
        }

    @Test
    fun `DimmerLevelChanged persists and updates state`() =
        runTest {
            torchBrightnessRepository.seed(initialLevel = 1, max = 5)
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(SettingsUiAction.DimmerLevelChanged(4))
            runCurrent()

            assertEquals(4, viewModel.uiState.value.dimmerLevel)
            assertEquals(4, torchBrightnessRepository.current())
        }

    private fun newViewModel() =
        SettingsViewModel(themePreferenceRepository, sendMorseMessage, torchBrightnessRepository)

    private class FakeThemePreferenceRepository : ThemePreferenceRepository {
        private val mode = MutableStateFlow(ThemeMode.AUTO)

        fun seed(initial: ThemeMode) {
            mode.value = initial
        }

        fun current(): ThemeMode = mode.value

        override fun observeThemeMode(): Flow<ThemeMode> = mode

        override suspend fun setThemeMode(mode: ThemeMode) {
            this.mode.value = mode
        }
    }

    private class FakeTorchBrightnessRepository : TorchBrightnessRepository {
        private val level = MutableStateFlow(1)
        private var maxLevel = 1

        fun seed(
            initialLevel: Int,
            max: Int,
        ) {
            level.value = initialLevel
            maxLevel = max
        }

        fun current(): Int = level.value

        override fun maxStrengthLevel(): Int = maxLevel

        override fun observeStrengthLevel(): Flow<Int> = level

        override suspend fun setStrengthLevel(level: Int) {
            this.level.value = level
        }
    }

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()

        private const val LOOP_ITERATIONS_TO_OBSERVE = 5L
    }
}
