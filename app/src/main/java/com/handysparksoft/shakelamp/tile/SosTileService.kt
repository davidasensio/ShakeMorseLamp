package com.handysparksoft.shakelamp.tile

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat
import com.handysparksoft.shakelamp.core.designsystem.R
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import com.handysparksoft.shakelamp.sos.SosTransmissionService
import com.handysparksoft.shakelamp.sos.SosTransmissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * One tap starts [SosTransmissionService] looping the configured emergency message; a second tap
 * stops it. Delegates entirely to that foreground service so the loop survives a screen lock.
 */
class SosTileService : TileService() {
    private val flashlightRepository: FlashlightRepository by inject()
    private val transmissionState: SosTransmissionState by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var observeJob: Job? = null

    override fun onStartListening() {
        super.onStartListening()
        if (!flashlightRepository.isFlashAvailable()) {
            qsTile?.apply {
                state = Tile.STATE_UNAVAILABLE
                updateTile()
            }
            return
        }
        observeJob =
            scope.launch {
                transmissionState.isRunning.collect { isRunning -> updateTile(isRunning) }
            }
    }

    override fun onStopListening() {
        observeJob?.cancel()
        observeJob = null
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()
        if (transmissionState.isRunning.value) {
            startService(Intent(this, SosTransmissionService::class.java).setAction(SosTransmissionService.ACTION_STOP))
        } else {
            ContextCompat.startForegroundService(this, Intent(this, SosTransmissionService::class.java))
        }
    }

    private fun updateTile(isRunning: Boolean) {
        qsTile?.apply {
            state = if (isRunning) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            icon = Icon.createWithResource(this@SosTileService, R.drawable.ic_sos)
            label = "SOS"
            updateTile()
        }
    }
}
