package com.kylecorry.trail_sense.shared.sensors.altimeter

import com.kylecorry.andromeda.core.sensors.AbstractSensor
import com.kylecorry.andromeda.core.sensors.IAltimeter
import com.kylecorry.sol.math.filters.IFilter
import com.kylecorry.sol.math.filters.MedianFilter

class FilteredAltimeter(
    private val altimeter: IAltimeter,
    private val filter: IFilter = MedianFilter(1),
    private val minReadings: Int = 1
) : AbstractSensor(), IAltimeter {

    private var count = 0

    override fun startImpl() {
        count = 0
        altimeter.start(this::onReading)
    }

    override fun stopImpl() {
        altimeter.stop(this::onReading)
    }

    private fun onReading(): Boolean {
        count++
        altitude = filter.filter(altimeter.altitude)
        return true
    }

    override var altitude: Float = altimeter.altitude
        private set

    override val hasValidReading: Boolean
        get() = altimeter.hasValidReading && count >= minReadings
}