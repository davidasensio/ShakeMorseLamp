package com.handysparksoft.shakelamp.core.common.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import com.handysparksoft.shakelamp.core.common.domain.ShakeDetector
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.annotation.Single
import kotlin.math.sqrt

@Single
class AccelerometerShakeDetector(
    private val context: Context,
) : ShakeDetector {
    override fun observeShakes(sensitivityLevel: Int): Flow<Unit> =
        callbackFlow {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometer == null) {
                close()
                return@callbackFlow
            }

            val threshold = SENSITIVITY_THRESHOLDS[sensitivityLevel] ?: SENSITIVITY_THRESHOLDS.getValue(DEFAULT_LEVEL)
            val pulseTracker = ShakePulseTracker(threshold)

            val listener =
                object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        val now = SystemClock.elapsedRealtime()
                        if (pulseTracker.registerSample(now, gForceOf(event))) trySend(Unit)
                    }

                    override fun onAccuracyChanged(
                        sensor: Sensor?,
                        accuracy: Int,
                    ) = Unit
                }

            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            awaitClose { sensorManager.unregisterListener(listener) }
        }

    private fun gForceOf(event: SensorEvent): Float {
        val gX = event.values[0] / SensorManager.GRAVITY_EARTH
        val gY = event.values[1] / SensorManager.GRAVITY_EARTH
        val gZ = event.values[2] / SensorManager.GRAVITY_EARTH
        return sqrt(gX * gX + gY * gY + gZ * gZ)
    }

    /** Tracks accelerometer pulses and decides when enough of them within a window mean "double shake". */
    private class ShakePulseTracker(
        private val threshold: Float,
    ) {
        private var lastPulseTimeMs = 0L
        private var lastFireTimeMs = 0L
        private val pulseTimestamps = ArrayDeque<Long>()

        fun registerSample(
            now: Long,
            gForce: Float,
        ): Boolean {
            if (now - lastFireTimeMs < POST_DETECTION_COOLDOWN_MS) return false
            if (gForce < threshold) return false
            if (now - lastPulseTimeMs < MIN_PULSE_INTERVAL_MS) return false
            lastPulseTimeMs = now

            pulseTimestamps.addLast(now)
            while (pulseTimestamps.isNotEmpty() && now - pulseTimestamps.first() > SHAKE_WINDOW_MS) {
                pulseTimestamps.removeFirst()
            }
            if (pulseTimestamps.size < SHAKE_COUNT_THRESHOLD) return false

            pulseTimestamps.clear()
            lastFireTimeMs = now
            return true
        }
    }

    private companion object {
        const val SHAKE_COUNT_THRESHOLD = 2
        const val SHAKE_WINDOW_MS = 1500L
        const val MIN_PULSE_INTERVAL_MS = 150L
        const val POST_DETECTION_COOLDOWN_MS = 1000L
        const val DEFAULT_LEVEL = 2
        val SENSITIVITY_THRESHOLDS = mapOf(1 to 2.7f, 2 to 1.8f, 3 to 1.2f)
    }
}
