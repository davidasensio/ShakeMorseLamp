package com.handysparksoft.shakelamp.tile

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.feature.settings.domain.SosTileRequestResult
import com.handysparksoft.shakelamp.feature.settings.domain.SosTileRequester
import org.koin.core.annotation.Single

@Single
class AndroidSosTileRequester(
    private val context: Context,
) : SosTileRequester {
    override fun isSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    override fun requestAddTile(onResult: (SosTileRequestResult) -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onResult(SosTileRequestResult.UNSUPPORTED)
            return
        }
        val statusBarManager = context.getSystemService(StatusBarManager::class.java)
        if (statusBarManager == null) {
            onResult(SosTileRequestResult.DECLINED)
            return
        }
        statusBarManager.requestAddTileService(
            ComponentName(context, SosTileService::class.java),
            TILE_LABEL,
            Icon.createWithResource(context, R.drawable.ic_sos_flashlight_horizontal),
            context.mainExecutor,
        ) { result ->
            val mapped =
                when (result) {
                    StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> SosTileRequestResult.ADDED
                    StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> SosTileRequestResult.ALREADY_ADDED
                    else -> SosTileRequestResult.DECLINED
                }
            onResult(mapped)
        }
    }

    private companion object {
        const val TILE_LABEL = "SOS"
    }
}
