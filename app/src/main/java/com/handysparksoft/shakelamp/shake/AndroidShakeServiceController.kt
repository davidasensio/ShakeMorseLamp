package com.handysparksoft.shakelamp.shake

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.handysparksoft.shakelamp.feature.settings.domain.ShakeServiceController
import org.koin.core.annotation.Single

@Single
class AndroidShakeServiceController(
    private val context: Context,
) : ShakeServiceController {
    override fun start() {
        ContextCompat.startForegroundService(context, Intent(context, ShakeDetectionService::class.java))
    }

    override fun stop() {
        context.stopService(Intent(context, ShakeDetectionService::class.java))
    }
}
