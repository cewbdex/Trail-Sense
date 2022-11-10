package com.kylecorry.trail_sense.shared.background

import com.kylecorry.andromeda.jobs.IPeriodicTaskScheduler
import java.time.Duration

class PeriodicBackgroundProcess(
    private val scheduler: IPeriodicTaskScheduler,
    private val period: Duration
) : BackgroundProcess {

    override fun start() {
        scheduler.interval(period)
    }

    override fun stop() {
        scheduler.cancel()
    }
}