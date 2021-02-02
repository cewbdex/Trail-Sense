package com.kylecorry.trail_sense.tools.nws.infrastructure

import android.content.Context
import android.content.Intent
import com.kylecorry.trailsensecore.infrastructure.system.PackageUtils

object NWSWeather {

    fun open(context: Context) {
        if (!isInstalled(context)) return
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun isInstalled(context: Context): Boolean {
        return PackageUtils.isPackageInstalled(context, PACKAGE)
    }

    private const val PACKAGE = "com.kylecorry.nwsweather"
}