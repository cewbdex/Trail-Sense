package com.kylecorry.trail_sense.weather.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.kylecorry.andromeda.camera.Camera
import com.kylecorry.andromeda.core.bitmap.BitmapUtils.toBitmap
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.trail_sense.databinding.FragmentCameraInputBinding

class CloudCameraFragment : BoundFragment<FragmentCameraInputBinding>() {

    private var onImage: ((Bitmap) -> Unit) = { it.recycle() }
    private var captureNextImage = false

    private val camera by lazy {
        Camera(
            requireContext(),
            viewLifecycleOwner,
            previewView = binding.preview,
            analyze = true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ok.setOnClickListener {
            captureNextImage = true
        }

        binding.zoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                camera.setZoom(progress / 100f)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        println("RESUME")
        camera.start(this::onCameraUpdate)
    }

    override fun onPause() {
        super.onPause()
        println("PAUSE")
        camera.stop(this::onCameraUpdate)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun onCameraUpdate(): Boolean {
        if (!isBound || !captureNextImage) {
            camera.image?.close()
            return true
        }
        try {
            val bitmap = camera.image?.image?.toBitmap()
            bitmap?.let(onImage)
            captureNextImage = false
        } catch(e: Exception) {
            // Do nothing
        } finally {
            camera.image?.close()
        }

        return false
    }

    fun setOnImageListener(listener: (image: Bitmap) -> Unit) {
        onImage = listener
    }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCameraInputBinding {
        return FragmentCameraInputBinding.inflate(layoutInflater, container, false)
    }

}