package com.handysparksoft.shakelamp.autooff

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

/** Shared in-process between [AutoOffKeepAliveService] and [AndroidAutoOffServiceController]. */
@Single
class AutoOffState {
    private val remainingMillis = MutableStateFlow<Long?>(null)
    val observeRemainingMillis: StateFlow<Long?> = remainingMillis.asStateFlow()

    fun update(value: Long?) {
        remainingMillis.value = value
    }
}
