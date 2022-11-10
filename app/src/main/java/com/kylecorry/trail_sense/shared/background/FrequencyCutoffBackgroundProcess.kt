package com.kylecorry.trail_sense.shared.background

import java.time.Duration

class FrequencyCutoffBackgroundProcess(
    private val under: BackgroundProcess,
    private val over: BackgroundProcess,
    private val threshold: Duration,
    private val frequency: Duration
) : BackgroundProcess {
    override fun start() {
        val isUnder = frequency < threshold
        if (isUnder) {
            over.stop()
            under.start()
        } else {
            under.stop()
            over.start()
        }
    }

    override fun stop() {
        under.stop()
        over.stop()
    }
}