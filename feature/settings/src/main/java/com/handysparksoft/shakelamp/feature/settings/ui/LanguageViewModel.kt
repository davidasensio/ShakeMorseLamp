package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handysparksoft.shakelamp.feature.settings.domain.LocaleDisplayFormatter
import com.handysparksoft.shakelamp.feature.settings.domain.LocaleOption
import com.handysparksoft.shakelamp.feature.settings.domain.LocalePreferenceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _uiEvent = MutableSharedFlow<LanguageUiEvent>()
    val uiEvent: SharedFlow<LanguageUiEvent> = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            localePreferenceRepository.observeSelectedLocaleTag().collect { tag ->
                _uiState.update { it.copy(selectedTag = tag, options = localeOptions(tag)) }
            }
        }
    }

    fun onAction(action: LanguageUiAction) {
        when (action) {
            is LanguageUiAction.LocaleSelected -> selectLocale(action.tag)
        }
    }

    /**
     * Persistence must finish - and only then emit the navigate-back event - before the UI pops
     * this screen: popping the back stack clears this ViewModel and cancels viewModelScope, so a
     * fire-and-forget launch here would risk the locale change never actually being applied.
     */
    private fun selectLocale(tag: String?) {
        viewModelScope.launch {
            localePreferenceRepository.setLocaleTag(tag)
            _uiEvent.emit(LanguageUiEvent.NavigateBack)
        }
    }

    /**
     * Names each language in the locale the app is currently rendering in - taken from the
     * repository rather than [Locale.getDefault], which lags behind a per-app language switch.
     *
     * Recomputed on every tag change, not once at construction: changing the locale recreates the
     * Activity but *retains* this ViewModel, so labels built in the constructor stay in the
     * previous language until the app is restarted - the picker then reads as if nothing happened.
     * [selectedTag] is preferred over the repository because it is already the newly chosen value,
     * which avoids depending on the platform having published the override by the time this runs.
     */
    private fun localeOptions(selectedTag: String? = null): List<LocaleOption> {
        val displayLocale =
            Locale.forLanguageTag(selectedTag ?: localePreferenceRepository.currentDisplayLocaleTag())
        return localePreferenceRepository.supportedLocaleTags().map { tag ->
            LocaleOption(
                tag = tag,
                label = LocaleDisplayFormatter.displayNameIn(tag, displayLocale),
                nativeLabel = LocaleDisplayFormatter.displayNameIn(tag, Locale.forLanguageTag(tag)),
                flagEmoji = LocaleDisplayFormatter.flagEmojiFor(tag),
            )
        }
    }
}
