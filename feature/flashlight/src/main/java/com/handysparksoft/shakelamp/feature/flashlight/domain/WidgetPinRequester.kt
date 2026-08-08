package com.handysparksoft.shakelamp.feature.flashlight.domain

interface WidgetPinRequester {
    /** False when the current default launcher doesn't support the one-tap pin request flow. */
    fun isSupported(): Boolean

    /**
     * Asks the launcher to place the widget directly - if it has a configure activity (it does),
     * the system launches that automatically once the user confirms placement. Fire-and-forget:
     * the platform only calls back on success, so there's nothing meaningful to await here.
     */
    fun requestPin()
}
