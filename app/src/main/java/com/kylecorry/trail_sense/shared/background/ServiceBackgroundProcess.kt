package com.kylecorry.trail_sense.shared.background

import android.content.Context
import android.content.Intent
import com.kylecorry.andromeda.core.system.Intents

// TODO: Replace this with always on
class ServiceBackgroundProcess(
    private val context: Context,
    private val intent: Intent,
    private val foreground: Boolean = false
) : BackgroundProcess {

    override fun start() {
        Intents.startService(context, intent, foreground)
    }

    override fun stop() {
        context.stopService(intent)
    }
}