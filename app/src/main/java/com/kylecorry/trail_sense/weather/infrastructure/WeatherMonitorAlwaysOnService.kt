package com.kylecorry.trail_sense.weather.infrastructure

import android.app.Notification
import android.content.Context
import android.content.Intent
import com.kylecorry.andromeda.services.CoroutineIntervalService
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.shared.background.BackgroundProcess
import com.kylecorry.trail_sense.shared.background.ServiceBackgroundProcess
import com.kylecorry.trail_sense.weather.infrastructure.alerts.CurrentWeatherAlerter
import com.kylecorry.trail_sense.weather.infrastructure.subsystem.WeatherSubsystem
import java.time.Duration

class WeatherMonitorAlwaysOnService : CoroutineIntervalService(TAG) {

    private val prefs by lazy { UserPreferences(applicationContext) }

    override val foregroundNotificationId: Int
        get() = WeatherUpdateScheduler.WEATHER_NOTIFICATION_ID

    override val period: Duration
        get() = prefs.weather.weatherUpdateFrequency

    override suspend fun doWork() {
        WeatherSubsystem.getInstance(applicationContext).updateWeather(true)
    }

    override fun getForegroundNotification(): Notification {
        return CurrentWeatherAlerter.getDefaultNotification(applicationContext)
    }

    override fun onDestroy() {
        stopService(true)
        super.onDestroy()
    }

    companion object {
        const val TAG = "WeatherMonitorHighPriorityService"

        fun process(context: Context): BackgroundProcess {
            val intent = Intent(context, WeatherMonitorAlwaysOnService::class.java)
            return ServiceBackgroundProcess(context, intent, true)
        }

    }
}