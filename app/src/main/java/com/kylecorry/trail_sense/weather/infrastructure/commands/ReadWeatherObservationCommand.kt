package com.kylecorry.trail_sense.weather.infrastructure.commands

import com.kylecorry.sol.units.Reading
import com.kylecorry.trail_sense.shared.alerts.IValueAlerter
import com.kylecorry.trail_sense.shared.commands.*
import com.kylecorry.trail_sense.shared.database.IReadingRepo
import com.kylecorry.trail_sense.weather.domain.RawWeatherObservation
import com.kylecorry.trail_sense.weather.infrastructure.CurrentWeather
import com.kylecorry.trail_sense.weather.infrastructure.subsystem.IWeatherSubsystem

internal class ReadWeatherObservationCommand(private val observer: IWeatherObserver) :
    ValueCommand<Unit, Reading<RawWeatherObservation>?> {
    override suspend fun execute(value: Unit): Reading<RawWeatherObservation>? {
        return observer.getWeatherObservation()
    }
}

internal class StoreWeatherObservationCommand(private val repo: IReadingRepo<RawWeatherObservation>) :
    ValueCommand<Reading<RawWeatherObservation>, Long> {
    override suspend fun execute(value: Reading<RawWeatherObservation>): Long {
        return repo.add(value)
    }
}

internal class GetWeatherCommand(private val subsystem: IWeatherSubsystem) :
    ValueCommand<Unit, CurrentWeather> {
    override suspend fun execute(value: Unit): CurrentWeather {
        return subsystem.getWeather()
    }
}

internal class NotifyWeatherCommand(private val alerter: IValueAlerter<CurrentWeather>) :
    ValueCommand<CurrentWeather, Unit> {
    override suspend fun execute(value: CurrentWeather) {
        alerter.alert(value)
    }
}

internal class LogWeatherCommand(
    private val getReading: ValueCommand<Unit, Reading<RawWeatherObservation>?>,
    private val storeReading: ValueCommand<Reading<RawWeatherObservation>, Long>,
    private val getWeather: ValueCommand<Unit, CurrentWeather>,
    private val notify: ValueCommand<CurrentWeather, Unit>
) : ValueCommand<Unit, Unit> {
    override suspend fun execute(value: Unit) {
        getReading.whenNotNull(
            storeReading
                .switchTo(getWeather)
                .then(notify)
        ).execute()
    }
}