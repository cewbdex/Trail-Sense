package com.kylecorry.trail_sense.shared.background

import com.kylecorry.andromeda.jobs.IOneTimeTaskScheduler

class OneTimeBackgroundProcess(private val scheduler: IOneTimeTaskScheduler) : BackgroundProcess {

    override fun start() {
        scheduler.once()
    }

    override fun stop() {
        scheduler.cancel()
    }
}