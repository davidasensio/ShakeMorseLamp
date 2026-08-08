package com.handysparksoft.shakelamp.feature.settings.ui

import com.handysparksoft.shakelamp.core.common.testing.MainDispatcherExtension
import com.handysparksoft.shakelamp.feature.settings.domain.LocalePreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class LanguageViewModelTest {
    private val localePreferenceRepository = FakeLocalePreferenceRepository()

    @Test
    fun `initial state exposes an option per tag`() =
        runTest {
            val viewModel = newViewModel()
            runCurrent()

            val options = viewModel.uiState.value.options
            assertEquals(SUPPORTED_TAGS, options.map { it.tag })
        }

    @Test
    fun `initial state reflects persisted locale tag`() =
        runTest {
            localePreferenceRepository.seed("es")
            val viewModel = newViewModel()
            runCurrent()

            assertEquals("es", viewModel.uiState.value.selectedTag)
        }

    @Test
    fun `LocaleSelected persists and updates state`() =
        runTest {
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(LanguageUiAction.LocaleSelected("de"))
            runCurrent()

            assertEquals("de", viewModel.uiState.value.selectedTag)
            assertEquals("de", localePreferenceRepository.current())
        }

    @Test
    fun `null selection resets to system default`() =
        runTest {
            localePreferenceRepository.seed("fr")
            val viewModel = newViewModel()
            runCurrent()

            viewModel.onAction(LanguageUiAction.LocaleSelected(null))
            runCurrent()

            assertEquals(null, viewModel.uiState.value.selectedTag)
            assertEquals(null, localePreferenceRepository.current())
        }

    private fun newViewModel() = LanguageViewModel(localePreferenceRepository)

    private class FakeLocalePreferenceRepository : LocalePreferenceRepository {
        private val tag = MutableStateFlow<String?>(null)

        fun seed(initial: String?) {
            tag.value = initial
        }

        fun current(): String? = tag.value

        override fun isPerAppLanguageSupported(): Boolean = true

        override fun currentDisplayLocaleTag(): String = "en"

        override fun observeSelectedLocaleTag(): Flow<String?> = tag

        override fun supportedLocaleTags(): List<String> = SUPPORTED_TAGS

        override suspend fun setLocaleTag(tag: String?) {
            this.tag.value = tag
        }
    }

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()

        private val SUPPORTED_TAGS = listOf("en", "ca", "de", "es", "fr", "it", "pt")
    }
}
