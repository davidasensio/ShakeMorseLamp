package com.handysparksoft.shakelamp.feature.settings.domain

interface SosTileRequester {
    /** False on API < 33, where there is no platform API to request a tile be added. */
    fun isSupported(): Boolean

    fun requestAddTile(onResult: (SosTileRequestResult) -> Unit)
}

enum class SosTileRequestResult {
    ADDED,
    ALREADY_ADDED,
    DECLINED,
    UNSUPPORTED,
}
