package com.kylecorry.trail_sense.tools.maps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kylecorry.andromeda.alerts.toast
import com.kylecorry.andromeda.core.coroutines.BackgroundMinimumState
import com.kylecorry.andromeda.core.coroutines.onMain
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.andromeda.fragments.inBackground
import com.kylecorry.andromeda.pickers.Pickers
import com.kylecorry.andromeda.print.Printer
import com.kylecorry.sol.math.SolMath
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.databinding.FragmentMapsBinding
import com.kylecorry.trail_sense.shared.FormatService
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.tools.guide.infrastructure.UserGuideUtils
import com.kylecorry.trail_sense.tools.maps.domain.MapProjectionType
import com.kylecorry.trail_sense.tools.maps.domain.PhotoMap
import com.kylecorry.trail_sense.tools.maps.infrastructure.MapRepo
import com.kylecorry.trail_sense.tools.maps.infrastructure.MapService
import com.kylecorry.trail_sense.tools.maps.infrastructure.calibration.MapRotationCalculator
import com.kylecorry.trail_sense.tools.maps.infrastructure.commands.PrintMapCommand
import com.kylecorry.trail_sense.tools.maps.ui.commands.DeleteMapCommand
import com.kylecorry.trail_sense.tools.maps.ui.commands.RenameMapCommand
import kotlin.math.absoluteValue


class MapsFragment : BoundFragment<FragmentMapsBinding>() {

    private val mapRepo by lazy { MapRepo.getInstance(requireContext()) }
    private val mapService by lazy { MapService.getInstance(requireContext()) }
    private val formatter by lazy { FormatService.getInstance(requireContext()) }
    private val prefs by lazy { UserPreferences(requireContext()) }

    private var mapId = 0L
    private var map: PhotoMap? = null
    private var currentFragment: Fragment? = null

    private val exportService by lazy { FragmentMapExportService(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapId = requireArguments().getLong("mapId")
    }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMapsBinding {
        return FragmentMapsBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recenterBtn.isVisible = false

        inBackground {
            loadMap()
        }

        binding.recenterBtn.setOnClickListener {
            recenter()
        }

        binding.menuBtn.setOnClickListener {
            val fragment = currentFragment
            val isMapView = fragment != null && fragment is ViewMapFragment

            val actions = listOf(
                MapContextualAction.Calibrate to if (isMapView) getString(R.string.calibrate) else null,
                MapContextualAction.Guide to getString(R.string.tool_user_guide_title),
                MapContextualAction.Rename to getString(R.string.rename),
                MapContextualAction.ChangeProjection to if (isMapView) getString(R.string.change_map_projection) else null,
                MapContextualAction.Measure to if (isMapView) getString(R.string.measure) else null,
                MapContextualAction.CreatePath to if (isMapView) getString(R.string.create_path) else null,
                MapContextualAction.Export to if (isMapView) getString(R.string.export) else null,
                MapContextualAction.Print to if (isMapView && Printer.canPrint()) getString(R.string.print) else null,
                MapContextualAction.Delete to getString(R.string.delete)
            )


            Pickers.menu(
                it,
                actions.map { action -> action.second }
            ) { index ->
                when (actions[index].first) {
                    MapContextualAction.Calibrate -> calibrate()
                    MapContextualAction.Guide -> openGuide()
                    MapContextualAction.Rename -> rename()
                    MapContextualAction.ChangeProjection -> changeProjection()
                    MapContextualAction.Measure, MapContextualAction.CreatePath -> measure()
                    MapContextualAction.Export -> export()
                    MapContextualAction.Print -> print()
                    MapContextualAction.Delete -> delete()
                }
                true
            }
        }

    }

    private fun openGuide() {
        UserGuideUtils.openGuide(this, R.raw.importing_maps)
    }

    private fun calibrate() {
        binding.recenterBtn.isVisible = true
        val fragment = MapCalibrationFragment.create(mapId) {
            inBackground {
                autoRotate()
                loadMap()
            }
        }
        setFragment(fragment)
    }

    private fun delete() {
        inBackground {
            map?.let {
                DeleteMapCommand(requireContext(), mapService).execute(it)
                findNavController().popBackStack()
            }
        }
    }

    private fun print() {
        val command = PrintMapCommand(requireContext())
        inBackground(BackgroundMinimumState.Created) {
            map?.let {
                command.execute(it)
            }
        }
    }

    private fun export() {
        inBackground {
            map?.let {
                mapRepo.getMap(it.id)?.let { updated ->
                    exportService.export(updated)
                }
            }
        }
    }

    private fun measure() {
        val fragment = currentFragment
        if (fragment != null && fragment is ViewMapFragment) {
            fragment.startDistanceMeasurement()
        }
    }

    private fun rename() {
        inBackground {
            map?.let {
                mapRepo.getMap(it.id)?.let { updated ->
                    RenameMapCommand(requireContext(), mapService).execute(updated)
                    map = mapRepo.getMap(updated.id)
                    binding.mapName.text = map?.name
                }
            }
        }
    }

    private fun changeProjection() {
        val projections = MapProjectionType.values()
        val projectionNames = projections.map { formatter.formatMapProjection(it) }
        Pickers.item(
            requireContext(),
            getString(R.string.change_map_projection),
            projectionNames,
            projections.indexOf(map?.metadata?.projection)
        ) {
            it ?: return@item
            map?.let { m ->
                val newProjection = projections[it]
                inBackground {
                    val updated = mapRepo.getMap(m.id) ?: return@inBackground
                    map = mapService.setProjection(updated, newProjection)
                    onMain {
                        reload()
                    }
                }
            }
        }
    }

    private fun recenter() {
        val fragment = currentFragment
        if (fragment != null && fragment is ViewMapFragment) {
            fragment.recenter()
        }

        if (fragment != null && fragment is MapCalibrationFragment) {
            fragment.recenter()
        }
    }

    private fun reload() {
        val fragment = currentFragment
        if (fragment != null && fragment is ViewMapFragment) {
            fragment.reloadMap()
        }

        if (fragment != null && fragment is MapCalibrationFragment) {
            fragment.reloadMap()
        }
    }

    private suspend fun loadMap() {
        map = mapRepo.getMap(mapId)
        onMain {
            map?.let(::onMapLoad)
        }
    }

    private fun onMapLoad(map: PhotoMap) {
        this.map = map
        binding.mapName.text = map.name
        when {
            !map.calibration.warped -> warp()
            !map.isCalibrated -> calibrate()
            else -> view()
        }
    }

    private fun warp() {
        val fragment = WarpMapFragment().apply {
            arguments = bundleOf("mapId" to mapId)
        }.also {
            binding.recenterBtn.isVisible = false
            it.setOnCompleteListener {
                inBackground {
                    loadMap()
                }
            }
        }

        setFragment(fragment)
    }

    private suspend fun autoRotate() {
        val updatedMap = mapRepo.getMap(mapId) ?: return
        if (!updatedMap.isCalibrated) return
        val newRotation = MapRotationCalculator().calculate(updatedMap)

        val delta = SolMath.deltaAngle(newRotation, updatedMap.calibration.rotation).absoluteValue
        if (delta > 1f){
            toast(getString(R.string.map_auto_rotated))
        }

        map = updatedMap.copy(
            calibration = updatedMap.calibration.copy(
                rotation = newRotation,
                rotated = true
            )
        )

        map?.let {
            mapRepo.addMap(it)
        }
    }


    private fun view() {
        binding.recenterBtn.isVisible = true
        val fragment = ViewMapFragment.create(mapId)
        setFragment(fragment)
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        currentFragment = fragment
        transaction.replace(binding.mapFragment.id, fragment)
            .addToBackStack(null).commit()
    }
}