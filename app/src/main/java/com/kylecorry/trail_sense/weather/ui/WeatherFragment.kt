package com.kylecorry.trail_sense.weather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.kylecorry.andromeda.alerts.toast
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.andromeda.core.topics.asLiveData
import com.kylecorry.andromeda.core.topics.generic.asLiveData
import com.kylecorry.andromeda.core.topics.generic.replay
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.andromeda.sense.Sensors
import com.kylecorry.sol.science.meteorology.Meteorology
import com.kylecorry.sol.science.meteorology.Weather
import com.kylecorry.sol.units.Distance
import com.kylecorry.sol.units.Pressure
import com.kylecorry.sol.units.PressureUnits
import com.kylecorry.sol.units.Reading
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.databinding.ActivityWeatherBinding
import com.kylecorry.trail_sense.quickactions.WeatherQuickActionBinder
import com.kylecorry.trail_sense.shared.*
import com.kylecorry.trail_sense.shared.CustomUiUtils.setCompoundDrawables
import com.kylecorry.trail_sense.shared.alerts.ResettableLoadingIndicator
import com.kylecorry.trail_sense.shared.alerts.SnackbarLoadingIndicator
import com.kylecorry.trail_sense.shared.extensions.*
import com.kylecorry.trail_sense.shared.permissions.RequestRemoveBatteryRestrictionCommand
import com.kylecorry.trail_sense.weather.infrastructure.CurrentWeather
import com.kylecorry.trail_sense.weather.infrastructure.WeatherLogger
import com.kylecorry.trail_sense.weather.infrastructure.WeatherObservation
import com.kylecorry.trail_sense.weather.infrastructure.commands.ChangeWeatherFrequencyCommand
import com.kylecorry.trail_sense.weather.infrastructure.subsystem.WeatherSubsystem
import java.time.Duration
import java.time.Instant

class WeatherFragment : BoundFragment<ActivityWeatherBinding>() {

    private var useSeaLevelPressure = false
    private var units = PressureUnits.Hpa

    private val prefs by lazy { UserPreferences(requireContext()) }
    private val temperatureUnits by lazy { prefs.temperatureUnits }

    private lateinit var chart: PressureChart

    private val formatService by lazy { FormatService(requireContext()) }

    private var history: List<WeatherObservation> = listOf()
    private var rawHistory: List<Reading<Pressure>> = listOf()
    private val mapper by lazy { WeatherListItemMapper(requireContext()) }

    private val weatherSubsystem by lazy { WeatherSubsystem.getInstance(requireContext()) }
    private var weather: CurrentWeather? = null
    private val loadingIndicator by lazy {
        ResettableLoadingIndicator(
            SnackbarLoadingIndicator(
                this,
                binding.weatherPlayBar,
                getString(R.string.updating_weather)
            )
        )
    }

    private val logger by lazy {
        WeatherLogger(
            requireContext(),
            Duration.ofSeconds(30),
            Duration.ofMillis(500),
            loadingIndicator
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WeatherQuickActionBinder(
            this,
            binding,
            prefs.weather
        ).bind()

        chart = PressureChart(binding.chart) { timeAgo, pressure ->
            if (timeAgo == null || pressure == null) {
                binding.pressureMarker.isInvisible = true
            } else {
                val formatted = formatService.formatPressure(
                    Pressure(pressure, units),
                    Units.getDecimalPlaces(units)
                )
                binding.pressureMarker.text = getString(
                    R.string.pressure_reading_time_ago,
                    formatted,
                    formatService.formatDuration(timeAgo, false)
                )
                binding.pressureMarker.isVisible = true
            }
        }

        weatherSubsystem.weatherChanged.asLiveData().observe(viewLifecycleOwner) {
            updateWeather()
        }

        weatherSubsystem.weatherMonitorState.replay()
            .asLiveData().observe(viewLifecycleOwner) {
                updateStatusBar()
            }

        weatherSubsystem.weatherMonitorFrequency.replay()
            .asLiveData().observe(viewLifecycleOwner) {
                updateStatusBar()
            }


        binding.weatherPlayBar.setOnSubtitleClickListener {
            ChangeWeatherFrequencyCommand(requireContext()) { onUpdate() }.execute()
        }

        binding.weatherPlayBar.setOnPlayButtonClickListener {
            when (weatherSubsystem.weatherMonitorState.getOrNull()) {
                FeatureState.Unavailable -> toast(getString(R.string.weather_monitoring_disabled))
                FeatureState.On -> weatherSubsystem.disableMonitor()
                FeatureState.Off -> {
                    weatherSubsystem.enableMonitor()
                    RequestRemoveBatteryRestrictionCommand(requireContext()).execute()
                }
                null -> {}
            }
        }
    }

    private fun updateStatusBar() {
        binding.weatherPlayBar.setState(
            weatherSubsystem.weatherMonitorState.getOrNull() ?: FeatureState.Off,
            weatherSubsystem.weatherMonitorFrequency.getOrNull()
        )
    }

    override fun onResume() {
        super.onResume()
        loadingIndicator.reset()
        logger.start()
        useSeaLevelPressure = prefs.weather.useSeaLevelPressure
        units = prefs.pressureUnits

        updateWeather()
    }

    override fun onPause() {
        super.onPause()
        logger.stop()
        loadingIndicator.hide()
    }

    private fun updateList() {
        if (!isBound) return
        val weather = weather ?: return
        val observation = weather.observation ?: return

        val pressure = formatService.formatPressure(
            observation.pressure.convertTo(units),
            Units.getDecimalPlaces(units)
        )

        val temperature = formatService.formatTemperature(
            observation.temperature.convertTo(temperatureUnits)
        )

        val tendency = getString(
            R.string.pressure_tendency_format_2, formatService.formatPressure(
                Pressure.hpa(weather.pressureTendency.amount).convertTo(units),
                Units.getDecimalPlaces(units) + 1
            )
        )
        val tendencyIcon =
            PressureCharacteristicImageMapper().getImageResource(weather.pressureTendency.characteristic)

        val humidity = formatService.formatPercentage(observation.humidity ?: 0f)

        val items = listOfNotNull(
            WeatherListItem(1, R.drawable.cloud, getString(R.string.pressure), pressure),
            WeatherListItem(2, tendencyIcon, getString(R.string.pressure_tendency), tendency),
            WeatherListItem(
                3,
                R.drawable.thermometer,
                getString(R.string.temperature),
                temperature
            ) { showTemperatureChart() },
            if (Sensors.hasHygrometer(requireContext())){
                WeatherListItem(
                    4,
                    R.drawable.ic_category_water,
                    getString(R.string.humidity),
                    humidity
                ) { showHumidityChart() }
            } else {
                null
            }
        )

        binding.weatherList.setItems(items, mapper)

    }

    private fun updateWeather() {
        inBackground {
            onIO {
                history = weatherSubsystem.getHistory().filter {
                    Duration.between(it.time, Instant.now()) <= prefs.weather.pressureHistory
                }

                loadRawWeatherReadings()

                weather = weatherSubsystem.getWeather()
            }
            onMain {
                update()
            }
        }
    }

    private suspend fun loadRawWeatherReadings() {
        if (isDebug()) {
            if (prefs.weather.useSeaLevelPressure) {
                val showMeanShiftedReadings = prefs.weather.showMeanShiftedReadings
                val raw = weatherSubsystem.getRawHistory().filter {
                    Duration.between(
                        it.time,
                        Instant.now()
                    ) <= prefs.weather.pressureHistory
                }
                val averageAltitude =
                    if (!showMeanShiftedReadings) {
                        0f
                    } else {
                        raw.map { it.value.altitude }.average().toFloat()
                    }
                rawHistory = raw.map {
                    if (showMeanShiftedReadings) {
                        val seaLevel = Meteorology.getSeaLevelPressure(
                            Pressure.hpa(it.value.pressure),
                            Distance.meters(averageAltitude)
                        )
                        Reading(seaLevel, it.time)
                    } else {
                        Reading(it.value.seaLevel(false), it.time)
                    }
                }
            }
        }
    }

    private fun update() {
        if (!isBound) return
        displayPressureChart(history, rawHistory)
        updateList()
        inBackground {
            updateForecast()
        }
    }

    private fun displayPressureChart(
        readings: List<WeatherObservation>,
        rawReadings: List<Reading<Pressure>>
    ) {
        val displayReadings = readings.map { it.pressureReading() }

        if (displayReadings.isNotEmpty()) {
            val totalTime = Duration.between(
                displayReadings.first().time, Instant.now()
            )
            var hours = totalTime.toHours()
            val minutes = totalTime.toMinutes() % 60

            when (hours) {
                0L -> binding.pressureHistoryDuration.text = context?.resources?.getQuantityString(
                    R.plurals.last_minutes,
                    minutes.toInt(),
                    minutes
                )
                else -> {
                    if (minutes >= 30) hours++
                    binding.pressureHistoryDuration.text =
                        context?.resources?.getQuantityString(
                            R.plurals.last_hours,
                            hours.toInt(),
                            hours
                        )
                }
            }

        }

        if (displayReadings.isNotEmpty()) {
            chart.plot(displayReadings, rawReadings.ifEmpty { null })
        }
    }

    private suspend fun updateForecast() {
        if (!isBound) return
        val weather = weather ?: return
        val observation = weather.observation ?: return
        val prediction = weather.prediction
        onMain {
            binding.weatherTitle.title.text = formatService.formatWeather(prediction.hourly, false)
            binding.weatherTitle.title.setCompoundDrawables(
                size = Resources.dp(requireContext(), 24f).toInt(),
                left = formatService.getWeatherImage(prediction.hourly, observation.pressure)
            )
            val speed = formatService.formatWeatherSpeed(prediction.hourly)
            binding.weatherTitle.subtitle.text = speed
            binding.weatherTitle.subtitle.isVisible = speed.isNotEmpty()
            binding.dailyForecast.text = getLongTermWeatherDescription(prediction.daily)
        }
    }

    private fun getLongTermWeatherDescription(weather: Weather): String {
        return when (weather) {
            Weather.ImprovingFast, Weather.ImprovingSlow -> getString(R.string.forecast_improving)
            Weather.WorseningSlow, Weather.WorseningFast, Weather.Storm -> getString(R.string.forecast_worsening)
            else -> ""
        }
    }

    private fun showHumidityChart() {
        val readings =
            history.filter { it.humidity != null }.map { Reading(it.humidity!!, it.time) }
        if (readings.size < 2) {
            return
        }
        val readingDuration = Duration.between(readings.first().time, Instant.now())
        CustomUiUtils.showLineChart(
            this, getString(
                R.string.humidity_history,
                formatService.formatDuration(readingDuration, true)
            )
        ) {
            val chart = HumidityChart(it)
            chart.plot(readings)
        }
    }

    private fun showTemperatureChart() {
        val readings = history.map {
            Reading(it.temperature.convertTo(temperatureUnits).temperature, it.time)
        }
        if (readings.size < 2) {
            return
        }
        val readingDuration = Duration.between(readings.first().time, Instant.now())
        CustomUiUtils.showLineChart(
            this, getString(
                R.string.temperature_history,
                formatService.formatDuration(readingDuration, true)
            )
        ) {
            val chart = TemperatureChart(it)
            chart.plot(readings)
        }
    }


    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): ActivityWeatherBinding {
        return ActivityWeatherBinding.inflate(layoutInflater, container, false)
    }

}
