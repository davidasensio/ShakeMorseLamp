package com.handysparksoft.shakelamp.core.common.domain

import kotlinx.coroutines.flow.Flow

/** Emits one Unit event per detected "double shake" gesture. */
interface ShakeDetector {
    fun observeShakes(sensitivityLevel: Int): Flow<Unit>
}
