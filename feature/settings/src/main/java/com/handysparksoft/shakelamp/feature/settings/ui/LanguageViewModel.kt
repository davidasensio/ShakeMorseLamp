package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handysparksoft.shakelamp.feature.settings.domain.LocaleOption
import com.handysparksoft.shakelamp.feature.settings.domain.LocalePreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import java.util.Locale

@KoinViewModel
class LanguageViewModel(
    private val localePreferenceRepository: LocalePreferenceRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LanguageUiState(options = localeOptions()))
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            localePreferenceRepository.observeSelectedLocaleTag().collect { tag ->
                _uiState.update { it.copy(selectedTag = tag) }
            }
        }
    }

    fun onAction(action: LanguageUiAction) {
        when (action) {
            is LanguageUiAction.LocaleSelected -> selectLocale(action.tag)
        }
    }

    private fun selectLocale(tag: String?) {
        viewModelScope.launch { localePreferenceRepository.setLocaleTag(tag) }
    }

    private fun localeOptions(): List<LocaleOption> =
        localePreferenceRepository.supportedLocaleTags().map { tag ->
            val locale = Locale.forLanguageTag(tag)
            LocaleOption(tag = tag, label = locale.nativeDisplayName())
        }

    private fun Locale.nativeDisplayName(): String {
        val name = getDisplayName(this)
        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(this) else it.toString() }
    }
}
