package com.handysparksoft.shakelamp.autooff

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.handysparksoft.shakelamp.feature.flashlight.domain.AutoOffServiceController
import org.koin.core.annotation.Single

@Single
class AndroidAutoOffServiceController(
    private val context: Context,
) : AutoOffServiceController {
    override fun start() {
        ContextCompat.startForegroundService(context, Intent(context, AutoOffKeepAliveService::class.java))
    }

    override fun stop() {
        context.stopService(Intent(context, AutoOffKeepAliveService::class.java))
    }
}
