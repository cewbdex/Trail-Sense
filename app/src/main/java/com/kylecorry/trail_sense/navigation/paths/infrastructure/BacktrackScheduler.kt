package com.kylecorry.trail_sense.navigation.paths.infrastructure

import android.content.Context
import com.kylecorry.trail_sense.navigation.paths.infrastructure.persistence.PathService
import com.kylecorry.trail_sense.navigation.paths.infrastructure.services.BacktrackAlwaysOnService
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.shared.background.BackgroundProcess
import com.kylecorry.trail_sense.shared.background.FrequencyCutoffBackgroundProcess
import com.kylecorry.trail_sense.shared.permissions.AllowForegroundWorkersCommand
import kotlinx.coroutines.runBlocking
import java.time.Duration

object BacktrackScheduler {

    fun restart(context: Context) {
        val prefs = UserPreferences(context)
        if (prefs.backtrackEnabled) {
            stop(context)
            start(context, false)
        }
    }

    fun start(context: Context, startNewPath: Boolean) {
        if (startNewPath) {
            runBlocking {
                PathService.getInstance(context).endBacktrackPath()
            }
        }

        if (!BacktrackIsAvailable().isSatisfiedBy(context)) {
            return
        }

        AllowForegroundWorkersCommand(context).execute()

        getProcess(context).start()
    }

    fun stop(context: Context) {
        getProcess(context).stop()
        AllowForegroundWorkersCommand(context).execute()
    }

    fun isOn(context: Context): Boolean {
        return BacktrackIsEnabled().isSatisfiedBy(context)
    }

    fun isDisabled(context: Context): Boolean {
        return BacktrackIsAvailable().not().isSatisfiedBy(context)
    }

    private fun getProcess(context: Context): BackgroundProcess {
        val prefs = UserPreferences(context)
        return FrequencyCutoffBackgroundProcess(
            BacktrackAlwaysOnService.process(context),
            BacktrackWorker.process(context),
            Duration.ofMinutes(15),
            prefs.backtrackRecordFrequency
        )
    }

}