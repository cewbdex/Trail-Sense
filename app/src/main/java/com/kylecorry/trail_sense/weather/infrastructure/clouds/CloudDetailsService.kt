package com.kylecorry.trail_sense.weather.infrastructure.clouds

import android.content.Context
import androidx.annotation.DrawableRes
import com.kylecorry.sol.science.meteorology.clouds.CloudGenus
import com.kylecorry.trail_sense.R

class CloudDetailsService(private val context: Context) {

    fun getCloudDescription(type: CloudGenus?): String {
        return when (type) {
            CloudGenus.Cirrocumulus -> context.getString(R.string.cirrocumulus_desc)
            CloudGenus.Cirrostratus -> context.getString(R.string.cirrostratus_desc)
            CloudGenus.Altocumulus -> context.getString(R.string.altocumulus_desc)
            CloudGenus.Altostratus -> context.getString(R.string.altostratus_desc)
            CloudGenus.Nimbostratus -> context.getString(R.string.nimbostratus_desc)
            CloudGenus.Stratus -> context.getString(R.string.stratus_desc)
            CloudGenus.Stratocumulus -> context.getString(R.string.stratocumulus_desc)
            CloudGenus.Cumulus -> context.getString(R.string.cumulus_desc)
            CloudGenus.Cumulonimbus -> context.getString(R.string.cumulonimbus_desc)
            CloudGenus.Cirrus -> context.getString(R.string.cirrus_desc)
            null -> context.getString(R.string.no_clouds)
        }
    }

    fun getCloudForecast(type: CloudGenus?): String {
        return when (type) {
            CloudGenus.Cirrocumulus -> context.getString(
                R.string.cloud_precipitation_forecast_hour_range,
                8,
                12
            )
            CloudGenus.Cirrostratus -> context.getString(
                R.string.cloud_precipitation_forecast_hour_range,
                10,
                15
            )
            CloudGenus.Altocumulus -> context.getString(R.string.altocumulus_forecast, 12)
            CloudGenus.Altostratus -> context.getString(R.string.altostratus_forecast, 8)
            CloudGenus.Nimbostratus -> context.getString(R.string.nimbostratus_forecast, 4)
            CloudGenus.Stratus -> context.getString(R.string.cloud_stratus_forecast, 3)
            CloudGenus.Stratocumulus -> context.getString(R.string.cloud_fair_forecast_hours, 3)
            CloudGenus.Cumulus -> context.getString(R.string.cumulus_forecast, 3)
            CloudGenus.Cumulonimbus -> context.getString(R.string.cumulonimbus_forecast)
            CloudGenus.Cirrus -> context.getString(
                R.string.cloud_precipitation_forecast_hour_range,
                12,
                24
            )
            null -> "-"
        }
    }

    fun getCloudName(type: CloudGenus?): String {
        return when (type) {
            CloudGenus.Cirrus -> context.getString(R.string.cirrus)
            CloudGenus.Cirrocumulus -> context.getString(R.string.cirrocumulus)
            CloudGenus.Cirrostratus -> context.getString(R.string.cirrostratus)
            CloudGenus.Altocumulus -> context.getString(R.string.altocumulus)
            CloudGenus.Altostratus -> context.getString(R.string.altostratus)
            CloudGenus.Nimbostratus -> context.getString(R.string.nimbostratus)
            CloudGenus.Stratus -> context.getString(R.string.stratus)
            CloudGenus.Stratocumulus -> context.getString(R.string.stratocumulus)
            CloudGenus.Cumulus -> context.getString(R.string.cumulus)
            CloudGenus.Cumulonimbus -> context.getString(R.string.cumulonimbus)
            null -> context.getString(R.string.clouds_clear)
        }
    }

    @DrawableRes
    fun getCloudImage(type: CloudGenus?): Int {
        return when (type) {
            CloudGenus.Cirrus -> R.drawable.cirrus
            CloudGenus.Cirrocumulus -> R.drawable.cirrocumulus
            CloudGenus.Cirrostratus -> R.drawable.cirrostratus
            CloudGenus.Altocumulus -> R.drawable.altocumulus
            CloudGenus.Altostratus -> R.drawable.altostratus
            CloudGenus.Nimbostratus -> R.drawable.nimbostratus
            CloudGenus.Stratus -> R.drawable.stratus
            CloudGenus.Stratocumulus -> R.drawable.stratocumulus
            CloudGenus.Cumulus -> R.drawable.cumulus
            CloudGenus.Cumulonimbus -> R.drawable.cumulonimbus
            null -> R.drawable.rectangle
        }
    }

}