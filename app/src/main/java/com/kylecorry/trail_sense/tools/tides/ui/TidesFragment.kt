package com.kylecorry.trail_sense.tools.tides.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.kylecorry.andromeda.alerts.Alerts
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.andromeda.list.ListView
import com.kylecorry.andromeda.pickers.Pickers
import com.kylecorry.sol.science.oceanography.TidalRange
import com.kylecorry.sol.science.oceanography.TideType
import com.kylecorry.sol.units.Distance
import com.kylecorry.sol.units.Reading
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.databinding.FragmentTideBinding
import com.kylecorry.trail_sense.databinding.ListItemTideBinding
import com.kylecorry.trail_sense.shared.CustomUiUtils
import com.kylecorry.trail_sense.shared.FormatService
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.tools.tides.domain.TideService
import com.kylecorry.trail_sense.tools.tides.domain.TideTable
import com.kylecorry.trail_sense.tools.tides.domain.loading.TideLoaderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.*


class TidesFragment : BoundFragment<FragmentTideBinding>() {

    private val formatService by lazy { FormatService(requireContext()) }
    private var displayDate = LocalDate.now()
    private val tideService = TideService()
    private lateinit var tideList: ListView<Triple<String, String, Boolean>>
    private var table: TideTable? = null
    private lateinit var chart: TideChart
    private var waterLevels = listOf<Reading<Float>>()
    private val prefs by lazy { UserPreferences(requireContext()) }
    private val units by lazy { prefs.baseDistanceUnits }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTideBinding {
        return FragmentTideBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chart = TideChart(binding.chart)
        tideList = ListView(binding.tideList, R.layout.list_item_tide) { itemView, tide ->
            val tideBinding = ListItemTideBinding.bind(itemView)
            tideBinding.tideType.text = tide.first + if (tide.third) {
                " (${getString(R.string.calculated)})"
            } else {
                ""
            }
            tideBinding.tideTime.text = tide.second
        }

        binding.tideListDateText.text = formatService.formatRelativeDate(displayDate)

        CustomUiUtils.setButtonState(binding.tideCalibration, false)
        binding.tideCalibration.setOnClickListener {
            findNavController().navigate(R.id.action_tides_to_tideList)
        }
        binding.tideListDatePicker.setOnClickListener {
            Pickers.date(requireContext(), displayDate) {
                if (it != null) {
                    displayDate = it
                    onDisplayDateChanged()
                }
            }
        }

        binding.tideListDatePicker.setOnLongClickListener {
            displayDate = LocalDate.now()
            onDisplayDateChanged()
            true
        }

        binding.loading.isVisible = true
        runInBackground {
            val loader = TideLoaderFactory().getTideLoader(requireContext())
            table = loader.getTideTable()
            withContext(Dispatchers.Main) {
                if (isBound) {
                    binding.loading.isVisible = false
                    if (table == null) {
                        Alerts.dialog(
                            requireContext(),
                            getString(R.string.no_tides),
                            getString(R.string.calibrate_new_tide)
                        ) { cancelled ->
                            if (!cancelled) {
                                findNavController().navigate(R.id.action_tides_to_tideList)
                            }
                        }
                    } else {
                        onTideLoaded()
                    }
                }
            }
        }

        binding.nextDate.setOnClickListener {
            displayDate = displayDate.plusDays(1)
            onDisplayDateChanged()
        }

        binding.prevDate.setOnClickListener {
            displayDate = displayDate.minusDays(1)
            onDisplayDateChanged()
        }


        scheduleUpdates(Duration.ofSeconds(1))
    }

    private fun onTideLoaded() {
        if (!isBound) {
            return
        }
        val tide = table ?: return
        binding.tideLocation.text = tide.name
            ?: if (tide.location != null) formatService.formatLocation(tide.location!!) else getString(
                android.R.string.untitled
            )
        updateTideChart()
        updateTideTable()
        updateCurrentTide()
    }

    private fun onDisplayDateChanged() {
        if (!isBound) {
            return
        }
        updateTideChart()
        updateTideTable()
        updateCurrentTide()
        binding.tideListDateText.text = formatService.formatRelativeDate(displayDate)
    }

    private fun updateTideChart() {
        val tide = table ?: return
        runInBackground {
            waterLevels = withContext(Dispatchers.Default) {
                tideService.getWaterLevels(tide, displayDate)
            }
            withContext(Dispatchers.Main) {
                if (!isBound) {
                    return@withContext
                }
                chart.plot(waterLevels, tideService.getRange(tide))
            }
        }

    }

    private fun updateTideTable() {
        val tide = table ?: return
        runInBackground {
            val tides = withContext(Dispatchers.Default) {
                tideService.getTides(tide, displayDate)
            }

            withContext(Dispatchers.Main) {
                if (!isBound) {
                    return@withContext
                }
                val tideStrings = tides.map {
                    val type = if (it.type == TideType.High) {
                        getString(R.string.high_tide)
                    } else {
                        getString(R.string.low_tide)
                    }
                    val isCalculated = tide.tides.none { t -> t.time == it.time }
                    // TODO: If height is not set in the tide table, don't show it
                    val time = if (isCalculated) {
                        formatService.formatTime(it.time.toLocalTime(), false)
                    } else {
                        val height = Distance.meters(it.height).convertTo(units)
                        val formattedHeight = formatService.formatDistance(height, 2, true)
                        formatService.formatTime(
                            it.time.toLocalTime(),
                            false
                        ) + " (${formattedHeight})"
                    }
                    Triple(type, time, isCalculated)
                }
                tideList.setData(tideStrings)
            }
        }
    }

    private fun updateCurrentTide() {
        val tide = table ?: return
        runInBackground {
            val current = withContext(Dispatchers.Default) {
                tideService.getCurrentTide(tide)
            }
            val isRising = withContext(Dispatchers.Default) {
                tideService.isRising(tide)
            }
            val height = withContext(Dispatchers.Default) {
                tideService.getWaterLevel(tide, ZonedDateTime.now())
            }
            withContext(Dispatchers.Main) {
                if (!isBound) {
                    return@withContext
                }
                binding.tideHeight.text =
                    getTideTypeName(current) + if (tideService.isWithinTideTable(tide)) {
                        val height = Distance.meters(height).convertTo(units)
                        " (${formatService.formatDistance(height, 2, true)})"
                    } else {
                        ""
                    }
                // TODO: Draw position on chart
                val currentLevel = waterLevels.minByOrNull {
                    Duration.between(Instant.now(), it.time).abs()
                }
                val currentIdx = waterLevels.indexOf(currentLevel)
                val point = chart.getPoint(currentIdx)
                binding.position.isInvisible =
                    point.x == binding.chart.x && point.y == binding.chart.y || displayDate != LocalDate.now()
                binding.position.x = point.x - binding.position.width / 2f
                binding.position.y = point.y - binding.position.height / 2f
                binding.tideTendency.isVisible = true
                binding.tideTendency.setImageResource(if (isRising) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
            }
        }
    }

    override fun onUpdate() {
        super.onUpdate()
        updateCurrentTide()
    }

    private fun getTidalRangeName(range: TidalRange): String {
        return when (range) {
            TidalRange.Neap -> getString(R.string.tide_neap)
            TidalRange.Spring -> getString(R.string.tide_spring)
            TidalRange.Normal -> getString(R.string.tide_normal)
        }
    }

    private fun getTideTypeName(tideType: TideType?): String {
        return when (tideType) {
            TideType.High -> getString(R.string.high_tide)
            TideType.Low -> getString(R.string.low_tide)
            null -> getString(R.string.half_tide)
        }
    }

}