package com.handysparksoft.shakelamp.feature.flashlight.data

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.handysparksoft.shakelamp.feature.flashlight.domain.FlashlightRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import timber.log.Timber

@Single
class CameraFlashlightRepository(
    context: Context,
) : FlashlightRepository {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private val cameraId: String? by lazy {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager
                .getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    override fun isFlashAvailable(): Boolean = cameraId != null

    override suspend fun setTorchEnabled(enabled: Boolean): Boolean {
        val id = cameraId ?: return false
        return withContext(Dispatchers.IO) {
            try {
                cameraManager.setTorchMode(id, enabled)
                true
            } catch (e: CameraAccessException) {
                Timber.e(e, "Failed to set torch mode")
                false
            }
        }
    }
}
