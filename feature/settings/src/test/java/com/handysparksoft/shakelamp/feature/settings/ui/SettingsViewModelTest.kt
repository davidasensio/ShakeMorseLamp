package com.handysparksoft.shakelamp.feature.settings.ui

import app.cash.turbine.test
import com.handysparksoft.shakelamp.core.common.domain.TorchBrightnessRepository
import com.handysparksoft.shakelamp.core.common.testing.MainDispatcherExtension
import com.handysparksoft.shakelamp.core.morse.domain.PlaybackResult
import com.handysparksoft.shakelamp.core.morse.domain.SendMorseMessageUseCase
import com.handysparksoft.shakelamp.feature.settings.domain.EmergencyMessageRepository
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeSettings
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeSettingsRepository
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ThemePreferenceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
    private val emergencyMessageRepository = FakeEmergencyMessageRepository()
    private val shakeSettingsRepository = FakeShakeSettingsRepository()

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
    fun `EmergencyMessageChanged persists on change`() =
        runTest {
            val viewModel = newViewModel()

            viewModel.onAction(SettingsUiAction.EmergencyMessageChanged("HELP"))
            runCurrent()

            assertEquals("HELP", viewModel.uiState.value.emergencyMessage)
            assertEquals("HELP", emergencyMessageRepository.current())
        }

    @Test
    fun `reflects persisted emergency message`() =
        runTest {
            emergencyMessageRepository.seed("MAYDAY")
            val viewModel = newViewModel()
            runCurrent()

            assertEquals("MAYDAY", viewModel.uiState.value.emergencyMessage)
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

    @Test
    fun `initial state reflects persisted shake settings`() =
        runTest {
            shakeSettingsRepository.seed(
                ShakeSettings(enabled = true, sensitivityLevel = 3, mode = ShakeMode.EMERGENCY),
            )
            val viewModel = newViewModel()
            runCurrent()

            assertTrue(viewModel.uiState.value.isShakeEnabled)
            assertEquals(3, viewModel.uiState.value.shakeSensitivity)
            assertEquals(ShakeMode.EMERGENCY, viewModel.uiState.value.shakeMode)
        }

    @Test
    fun `ShakeSensitivityChanged updates and persists`() =
        runTest {
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(SettingsUiAction.ShakeSensitivityChanged(1))
            runCurrent()

            assertEquals(1, viewModel.uiState.value.shakeSensitivity)
            assertEquals(1, shakeSettingsRepository.current().sensitivityLevel)
        }

    @Test
    fun `EMERGENCY mode with message emits no snackbar`() =
        runTest {
            val viewModel = newViewModel()
            viewModel.onAction(SettingsUiAction.EmergencyMessageChanged("SOS"))
            runCurrent()

            viewModel.uiEvent.test {
                viewModel.onAction(SettingsUiAction.ShakeModeChanged(ShakeMode.EMERGENCY))
                runCurrent()

                assertEquals(ShakeMode.EMERGENCY, viewModel.uiState.value.shakeMode)
                assertEquals(ShakeMode.EMERGENCY, shakeSettingsRepository.current().mode)
                expectNoEvents()
            }
        }

    @Test
    fun `blank message in EMERGENCY mode emits snackbar`() =
        runTest {
            val viewModel = newViewModel()
            viewModel.onAction(SettingsUiAction.EmergencyMessageChanged(""))
            runCurrent()

            viewModel.uiEvent.test {
                viewModel.onAction(SettingsUiAction.ShakeModeChanged(ShakeMode.EMERGENCY))
                runCurrent()

                assertEquals(SettingsUiEvent.ShowMissingEmergencyMessageSnackbar, awaitItem())
                assertEquals(ShakeMode.EMERGENCY, viewModel.uiState.value.shakeMode)
                assertEquals(ShakeMode.EMERGENCY, shakeSettingsRepository.current().mode)
            }
        }

    private fun newViewModel() =
        SettingsViewModel(
            themePreferenceRepository,
            sendMorseMessage,
            torchBrightnessRepository,
            emergencyMessageRepository,
            shakeSettingsRepository,
        )

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

    private class FakeEmergencyMessageRepository : EmergencyMessageRepository {
        private val message = MutableStateFlow("SOS")

        fun seed(initial: String) {
            message.value = initial
        }

        fun current(): String = message.value

        override fun observeMessage(): Flow<String> = message

        override suspend fun setMessage(message: String) {
            this.message.value = message
        }
    }

    private class FakeShakeSettingsRepository : ShakeSettingsRepository {
        private val settings = MutableStateFlow(ShakeSettings())

        fun seed(initial: ShakeSettings) {
            settings.value = initial
        }

        fun current(): ShakeSettings = settings.value

        override fun observeSettings(): Flow<ShakeSettings> = settings

        override suspend fun setEnabled(enabled: Boolean) {
            settings.update { it.copy(enabled = enabled) }
        }

        override suspend fun setSensitivity(level: Int) {
            settings.update { it.copy(sensitivityLevel = level) }
        }

        override suspend fun setMode(mode: ShakeMode) {
            settings.update { it.copy(mode = mode) }
        }
    }

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()

        private const val LOOP_ITERATIONS_TO_OBSERVE = 5L
    }
}
