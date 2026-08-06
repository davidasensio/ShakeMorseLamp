package com.handysparksoft.shakelamp.core.morse.data

import com.handysparksoft.shakelamp.core.morse.domain.MorseOutputDriver
import com.handysparksoft.shakelamp.core.morse.domain.MorsePlaybackPlan
import com.handysparksoft.shakelamp.core.morse.domain.MorsePlaybackStep
import com.handysparksoft.shakelamp.core.morse.domain.TorchController
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class TorchOutputDriver(
    private val torchController: TorchController,
) : MorseOutputDriver {
    override fun isAvailable(): Boolean = torchController.isAvailable()

    override suspend fun emit(
        plan: MorsePlaybackPlan,
        onStepCompleted: (MorsePlaybackStep) -> Unit,
    ) {
        try {
            plan.steps.forEach { step ->
                currentCoroutineContext().ensureActive()
                torchController.setTorchEnabled(step.isSignalOn)
                delay(step.durationMillis)
                onStepCompleted(step)
            }
        } finally {
            // Cancellation (loop stopped, message changed mid-transmission) must still
            // guarantee the torch ends up off — a plain suspend call here would otherwise
            // immediately rethrow the CancellationException instead of running.
            withContext(NonCancellable) {
                torchController.setTorchEnabled(false)
            }
        }
    }

    override fun stop() {
        // No synchronous hardware call here: the caller cancels the coroutine running
        // emit(), and that function's own `finally` (above) is what reliably turns the
        // torch off, whether emit() completes normally or is cancelled mid-step.
    }
}
