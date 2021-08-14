package com.kylecorry.trail_sense.shared.sensors

import com.kylecorry.trailsensecore.domain.network.CellSignal
import com.kylecorry.trailsensecore.infrastructure.sensors.AbstractSensor
import com.kylecorry.trailsensecore.infrastructure.sensors.network.ICellSignalSensor
import com.kylecorry.andromeda.core.time.Timer

class NullCellSignalSensor: AbstractSensor(), ICellSignalSensor {
    override val hasValidReading: Boolean
        get() = true
    override val signals: List<CellSignal>
        get() = listOf()

    private val intervalometer = Timer {
        notifyListeners()
    }

    override fun startImpl() {
        intervalometer.interval(20)
    }

    override fun stopImpl() {
        intervalometer.stop()
    }
}