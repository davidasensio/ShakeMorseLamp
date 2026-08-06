package com.handysparksoft.shakelamp.core.morse.domain

sealed interface PlaybackResult {
    data object Completed : PlaybackResult

    data object Failed : PlaybackResult
}
