package com.handysparksoft.shakelamp.autooff

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.handysparksoft.shakelamp.feature.flashlight.domain.AutoOffServiceController
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class AndroidAutoOffServiceController(
    private val context: Context,
    private val autoOffState: AutoOffState,
) : AutoOffServiceController {
    override fun start(minutes: Int) {
        val intent =
            Intent(context, AutoOffKeepAliveService::class.java)
                .putExtra(AutoOffKeepAliveService.EXTRA_MINUTES, minutes)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stop() {
        context.stopService(Intent(context, AutoOffKeepAliveService::class.java))
    }

    override fun observeRemainingMillis(): Flow<Long?> = autoOffState.observeRemainingMillis
}
