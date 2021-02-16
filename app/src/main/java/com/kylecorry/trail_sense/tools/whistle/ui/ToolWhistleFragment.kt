package com.kylecorry.trail_sense.tools.whistle.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kylecorry.trail_sense.databinding.FragmentToolWhistleBinding
import com.kylecorry.trail_sense.shared.MorseSymbol
import com.kylecorry.trail_sense.shared.SOS
import com.kylecorry.trail_sense.tools.whistle.infrastructure.Signal
import com.kylecorry.trail_sense.tools.whistle.infrastructure.SignalPlayer
import com.kylecorry.trail_sense.tools.whistle.infrastructure.WhistleSignalingDevice
import com.kylecorry.trailsensecore.infrastructure.audio.ISoundPlayer
import com.kylecorry.trailsensecore.infrastructure.audio.Whistle
import com.kylecorry.trailsensecore.infrastructure.time.Intervalometer
import java.time.Duration

class ToolWhistleFragment : Fragment() {

    private var _binding: FragmentToolWhistleBinding? = null
    private val binding get() = _binding!!

    private lateinit var whistle: ISoundPlayer

    private val morseDurationMs = 400L

    private var state = WhistleState.Off

    private val emergencySignal = listOf(
        Signal.on(Duration.ofSeconds(2)),
        Signal.off(Duration.ofSeconds(1)),
        Signal.on(Duration.ofSeconds(2)),
        Signal.off(Duration.ofSeconds(1)),
        Signal.on(Duration.ofSeconds(2)),
        Signal.off(Duration.ofSeconds(3))
    )

    private val sosSignal = SOS.map {
        if (it == MorseSymbol.Dash || it == MorseSymbol.Dot){
            Signal.on(Duration.ofMillis(morseDurationMs).multipliedBy(it.durationMultiplier.toLong()))
        } else {
            Signal.off(Duration.ofMillis(morseDurationMs).multipliedBy(it.durationMultiplier.toLong()))
        }
    }

    private val signalWhistle by lazy { SignalPlayer(WhistleSignalingDevice(whistle)) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToolWhistleBinding.inflate(inflater, container, false)

        binding.whistleEmergencyBtn.setOnClickListener {
            state = if (state == WhistleState.Emergency) {
                signalWhistle.cancel()
                WhistleState.Off
            } else {
                whistle.off()
                signalWhistle.play(emergencySignal, true)
                WhistleState.Emergency
            }
            updateUI()
        }

        binding.whistleSosBtn.setOnClickListener {
            state = if (state == WhistleState.Sos) {
                signalWhistle.cancel()
                WhistleState.Off
            } else {
                whistle.off()
                signalWhistle.play(sosSignal, true)
                WhistleState.Sos
            }
            updateUI()
        }


        binding.whistleBtn.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                signalWhistle.cancel()
                whistle.on()
                state = WhistleState.On
            } else if (event.action == MotionEvent.ACTION_UP) {
                whistle.off()
                state = WhistleState.Off
            }
            updateUI()
            true
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whistle = Whistle()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        whistle.release()
    }

    override fun onPause() {
        super.onPause()
        whistle.off()
        signalWhistle.cancel()
    }


    private fun updateUI(){
        binding.whistleEmergencyBtn.setState(state == WhistleState.Emergency)
        binding.whistleSosBtn.setState(state == WhistleState.Sos)
        binding.whistleBtn.setState(state == WhistleState.On)
    }


    private enum class WhistleState {
        On,
        Off,
        Emergency,
        Sos
    }

}