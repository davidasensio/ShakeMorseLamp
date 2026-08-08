package com.handysparksoft.shakelamp.feature.settings.ui

import androidx.compose.runtime.Immutable
import com.handysparksoft.shakelamp.feature.settings.domain.LocaleOption

@Immutable
data class LanguageUiState(
    val options: List<LocaleOption> = emptyList(),
    val selectedTag: String? = null,
)

sealed interface LanguageUiAction {
    data class LocaleSelected(
        val tag: String?,
    ) : LanguageUiAction
}

sealed interface LanguageUiEvent {
    data object NavigateBack : LanguageUiEvent
}
