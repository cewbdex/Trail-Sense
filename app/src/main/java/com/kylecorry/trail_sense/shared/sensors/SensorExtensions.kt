package com.kylecorry.trail_sense.shared.sensors

import com.kylecorry.andromeda.core.sensors.ISensor
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun ISensor.readUntilValid() = suspendCancellableCoroutine { cont ->
    val callback: () -> Boolean = {
        if (hasValidReading) {
            cont.resume(Unit)
            false
        } else {
            true
        }
    }
    cont.invokeOnCancellation {
        unsubscribe(callback)
    }
    subscribe(callback)
}