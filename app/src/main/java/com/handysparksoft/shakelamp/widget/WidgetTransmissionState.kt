package com.handysparksoft.shakelamp.widget

import androidx.glance.GlanceId
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.annotation.Single

/** Tracks which widget instance (if any) currently owns the single shared [WidgetTransmissionService] run. */
@Single
class WidgetTransmissionState {
    private val activeGlanceId = MutableStateFlow<GlanceId?>(null)

    fun isActive(id: GlanceId): Boolean = activeGlanceId.value == id

    fun start(id: GlanceId) {
        activeGlanceId.value = id
    }

    fun clear() {
        activeGlanceId.value = null
    }
}
