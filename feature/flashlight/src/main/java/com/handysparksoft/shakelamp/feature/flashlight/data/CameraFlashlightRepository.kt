package com.handysparksoft.shakelamp.feature.flashlight.data

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import com.handysparksoft.shakelamp.core.common.domain.TorchBrightnessRepository
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import timber.log.Timber

@Single
class CameraFlashlightRepository(
    context: Context,
    private val torchBrightnessRepository: TorchBrightnessRepository,
) : FlashlightRepository {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val cameraId: String? by lazy {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager
                .getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    private val supportsStrengthControl: Boolean by lazy { torchBrightnessRepository.maxStrengthLevel() > 1 }

    @Volatile
    private var isTorchOn = false

    init {
        repositoryScope.launch {
            torchBrightnessRepository.observeStrengthLevel().collect { level ->
                if (isTorchOn) applyStrength(level)
            }
        }
    }

    override fun isFlashAvailable(): Boolean = cameraId != null

    override suspend fun setTorchEnabled(enabled: Boolean): Boolean {
        val id = cameraId ?: return false
        return withContext(Dispatchers.IO) {
            try {
                if (enabled) {
                    turnOnTorch(id)
                } else {
                    cameraManager.setTorchMode(id, false)
                }
                isTorchOn = enabled
                true
            } catch (e: CameraAccessException) {
                Timber.e(e, "Failed to set torch mode")
                false
            }
        }
    }

    private suspend fun turnOnTorch(id: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && supportsStrengthControl) {
            val level = torchBrightnessRepository.observeStrengthLevel().first()
            cameraManager.turnOnTorchWithStrengthLevel(id, level)
        } else {
            cameraManager.setTorchMode(id, true)
        }
    }

    /** Applies a brightness change live while the torch is already on, e.g. dragging the Settings slider. */
    private fun applyStrength(level: Int) {
        val id = cameraId ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && supportsStrengthControl) {
            runCatching { cameraManager.turnOnTorchWithStrengthLevel(id, level) }
                .onFailure { Timber.e(it, "Failed to update torch strength live") }
        }
    }
}
