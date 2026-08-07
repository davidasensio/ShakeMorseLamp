package com.handysparksoft.shakelamp.feature.settings.ui

import app.cash.turbine.test
import com.handysparksoft.shakelamp.core.common.domain.HapticFeedbackRepository
import com.handysparksoft.shakelamp.core.common.domain.LoopPauseRepository
import com.handysparksoft.shakelamp.core.common.domain.TorchBrightnessRepository
import com.handysparksoft.shakelamp.core.common.domain.TransmissionSpeedRepository
import com.handysparksoft.shakelamp.core.common.testing.MainDispatcherExtension
import com.handysparksoft.shakelamp.core.morse.domain.MorseTimingDefaults
import com.handysparksoft.shakelamp.core.morse.domain.PlaybackResult
import com.handysparksoft.shakelamp.core.morse.domain.SendMorseMessageUseCase
import com.handysparksoft.shakelamp.feature.settings.domain.EmergencyMessageRepository
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeServiceController
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeSettings
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeSettingsRepository
import com.handysparksoft.shakelamp.feature.settings.domain.SosTileRequestResult
import com.handysparksoft.shakelamp.feature.settings.domain.SosTileRequester
import com.handysparksoft.shakelamp.feature.settings.domain.ThemeMode
import com.handysparksoft.shakelamp.feature.settings.domain.ThemePreferenceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    private val shakeServiceController = mockk<ShakeServiceController>(relaxUnitFun = true)
    private val hapticFeedbackRepository = FakeHapticFeedbackRepository()
    private val loopPauseRepository = FakeLoopPauseRepository()
    private val transmissionSpeedRepository = FakeTransmissionSpeedRepository()
    private val sosTileRequester = mockk<SosTileRequester>()

    @BeforeEach
    fun setUp() {
        coEvery { sendMorseMessage(any(), any(), any()) } returns PlaybackResult.Completed
        every { sosTileRequester.isSupported() } returns true
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
            loopPauseRepository.seed(0L)
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

    @Test
    fun `ShakeEnabledToggled starts service and persists`() =
        runTest {
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(SettingsUiAction.ShakeEnabledToggled)
            runCurrent()

            assertTrue(viewModel.uiState.value.isShakeEnabled)
            assertTrue(shakeSettingsRepository.current().enabled)
            verify { shakeServiceController.start() }

            viewModel.onAction(SettingsUiAction.ShakeEnabledToggled)
            runCurrent()

            assertFalse(viewModel.uiState.value.isShakeEnabled)
            assertFalse(shakeSettingsRepository.current().enabled)
            verify { shakeServiceController.stop() }
        }

    @Test
    fun `HapticFeedbackToggled flips and persists`() =
        runTest {
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(SettingsUiAction.HapticFeedbackToggled)
            runCurrent()

            assertFalse(viewModel.uiState.value.isHapticFeedbackEnabled)
            assertFalse(hapticFeedbackRepository.current())

            viewModel.onAction(SettingsUiAction.HapticFeedbackToggled)
            runCurrent()

            assertTrue(viewModel.uiState.value.isHapticFeedbackEnabled)
            assertTrue(hapticFeedbackRepository.current())
        }

    @Test
    fun `TransmissionSpeedChanged persists and updates`() =
        runTest {
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(SettingsUiAction.TransmissionSpeedChanged(TRANSMISSION_SPEED_WPM))
            runCurrent()

            assertEquals(TRANSMISSION_SPEED_WPM, viewModel.uiState.value.transmissionSpeedWpm)
            assertEquals(TRANSMISSION_SPEED_WPM, transmissionSpeedRepository.current())
        }

    @Test
    fun `StrobeToggled uses the configured speed`() =
        runTest {
            transmissionSpeedRepository.seed(TRANSMISSION_SPEED_WPM)
            loopPauseRepository.seed(LOOP_PAUSE_MILLIS)
            val viewModel = newViewModel()
            viewModel.onAction(SettingsUiAction.EmergencyMessageChanged("SOS"))
            runCurrent()

            viewModel.onAction(SettingsUiAction.StrobeToggled)
            runCurrent()

            coVerify { sendMorseMessage("SOS", TRANSMISSION_SPEED_WPM, any()) }

            viewModel.onAction(SettingsUiAction.StrobeToggled)
            runCurrent()
        }

    @Test
    fun `LoopPauseChanged persists and updates state`() =
        runTest {
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(SettingsUiAction.LoopPauseChanged(LOOP_PAUSE_MILLIS))
            runCurrent()

            assertEquals(LOOP_PAUSE_MILLIS, viewModel.uiState.value.loopPauseMillis)
            assertEquals(LOOP_PAUSE_MILLIS, loopPauseRepository.current())
        }

    @Test
    fun `StrobeToggled waits the configured pause`() =
        runTest {
            loopPauseRepository.seed(LOOP_PAUSE_MILLIS)
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(SettingsUiAction.StrobeToggled)
            runCurrent()
            coVerify(exactly = 1) { sendMorseMessage(any(), any(), any()) }

            advanceTimeBy(LOOP_PAUSE_MILLIS - 1)
            runCurrent()
            coVerify(exactly = 1) { sendMorseMessage(any(), any(), any()) }

            advanceTimeBy(2)
            runCurrent()
            coVerify(exactly = 2) { sendMorseMessage(any(), any(), any()) }

            viewModel.onAction(SettingsUiAction.StrobeToggled)
            runCurrent()
        }

    @Test
    fun `initial state reflects tile support`() =
        runTest {
            every { sosTileRequester.isSupported() } returns false
            val viewModel = newViewModel()

            assertFalse(viewModel.uiState.value.isAddSosTileSupported)
        }

    @Test
    fun `AddSosTileRequested emits the result`() =
        runTest {
            every { sosTileRequester.requestAddTile(any()) } answers {
                firstArg<(SosTileRequestResult) -> Unit>().invoke(SosTileRequestResult.ADDED)
            }
            val viewModel = newViewModel()

            viewModel.uiEvent.test {
                viewModel.onAction(SettingsUiAction.AddSosTileRequested)
                runCurrent()

                assertEquals(SettingsUiEvent.ShowSosTileResult(SosTileRequestResult.ADDED), awaitItem())
            }
        }

    private fun newViewModel() =
        SettingsViewModel(
            themePreferenceRepository,
            sendMorseMessage,
            torchBrightnessRepository,
            emergencyMessageRepository,
            shakeSettingsRepository,
            shakeServiceController,
            hapticFeedbackRepository,
            loopPauseRepository,
            transmissionSpeedRepository,
            sosTileRequester,
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

    private class FakeHapticFeedbackRepository : HapticFeedbackRepository {
        private val enabled = MutableStateFlow(true)

        fun current(): Boolean = enabled.value

        override fun observeEnabled(): Flow<Boolean> = enabled

        override suspend fun setEnabled(enabled: Boolean) {
            this.enabled.value = enabled
        }
    }

    private class FakeLoopPauseRepository : LoopPauseRepository {
        private val pauseMillis = MutableStateFlow(2_000L)

        fun seed(initial: Long) {
            pauseMillis.value = initial
        }

        fun current(): Long = pauseMillis.value

        override fun observePauseMillis(): Flow<Long> = pauseMillis

        override suspend fun setPauseMillis(millis: Long) {
            pauseMillis.value = millis
        }
    }

    private class FakeTransmissionSpeedRepository : TransmissionSpeedRepository {
        private val speedWpm = MutableStateFlow(MorseTimingDefaults.DEFAULT_WPM)

        fun seed(initial: Int) {
            speedWpm.value = initial
        }

        fun current(): Int = speedWpm.value

        override fun observeSpeedWpm(): Flow<Int> = speedWpm

        override suspend fun setSpeedWpm(wpm: Int) {
            speedWpm.value = wpm
        }
    }

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()

        private const val LOOP_ITERATIONS_TO_OBSERVE = 5L
        private const val LOOP_PAUSE_MILLIS = 5_000L
        private const val TRANSMISSION_SPEED_WPM = 8
    }
}
