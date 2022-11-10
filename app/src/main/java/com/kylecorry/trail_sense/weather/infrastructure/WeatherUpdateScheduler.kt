package com.kylecorry.trail_sense.weather.infrastructure

import android.content.Context
import com.kylecorry.andromeda.notify.Notify
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.shared.background.BackgroundProcess
import com.kylecorry.trail_sense.shared.background.FrequencyCutoffBackgroundProcess
import com.kylecorry.trail_sense.shared.permissions.AllowForegroundWorkersCommand
import java.time.Duration

object WeatherUpdateScheduler {

    fun restart(context: Context) {
        if (WeatherMonitorIsEnabled().isSatisfiedBy(context)) {
            stop(context)
            start(context)
        }
    }

    fun start(context: Context) {
        if (!WeatherMonitorIsAvailable().isSatisfiedBy(context)) {
            return
        }

        AllowForegroundWorkersCommand(context).execute()

        getProcess(context).start()
    }

    fun stop(context: Context) {
        getProcess(context).stop()
        Notify.cancel(context, WEATHER_NOTIFICATION_ID)
        AllowForegroundWorkersCommand(context).execute()
    }

    private fun getProcess(context: Context): BackgroundProcess {
        val prefs = UserPreferences(context)
        return FrequencyCutoffBackgroundProcess(
            WeatherMonitorAlwaysOnService.process(context),
            WeatherUpdateWorker.process(context),
            Duration.ofMinutes(15),
            prefs.weather.weatherUpdateFrequency
        )
    }

    const val WEATHER_NOTIFICATION_ID = 1
}