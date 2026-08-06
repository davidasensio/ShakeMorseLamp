package com.handysparksoft.shakelamp.core.common.data

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.handysparksoft.shakelamp.core.common.domain.TorchBrightnessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val Context.torchBrightnessDataStore by preferencesDataStore(name = "torch_brightness")
private val STRENGTH_LEVEL_KEY = intPreferencesKey("strength_level")

@Single
class TorchBrightnessDataStoreRepository(
    private val context: Context,
) : TorchBrightnessRepository {
    private val cameraManager by lazy { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }

    private val cameraId: String? by lazy {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager
                .getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    override fun maxStrengthLevel(): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return DEFAULT_LEVEL
        val id = cameraId ?: return DEFAULT_LEVEL
        return runCatching {
            cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL)
        }.getOrNull() ?: DEFAULT_LEVEL
    }

    override fun observeStrengthLevel(): Flow<Int> =
        context.torchBrightnessDataStore.data.map { preferences ->
            preferences[STRENGTH_LEVEL_KEY] ?: maxStrengthLevel()
        }

    override suspend fun setStrengthLevel(level: Int) {
        context.torchBrightnessDataStore.edit { preferences ->
            preferences[STRENGTH_LEVEL_KEY] = level
        }
    }

    private companion object {
        const val DEFAULT_LEVEL = 1
    }
}
