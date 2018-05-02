package com.mxmariner.tides.main.factory

import android.content.Context
import android.support.v4.content.ContextCompat
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.main.model.StationListViewPresentation
import com.mxmariner.tides.settings.Preferences
import com.mxmariner.tides.ui.UnitFormats
import org.joda.time.DateTime
import org.joda.time.Duration

class StationListViewPresentationFactory(kodein: Kodein) {

    private val preferences: Preferences = kodein.instance()
    private val unitFormats: UnitFormats = kodein.instance()
    private val context: Context = kodein.instance()

    fun  createPresentation(station: IStation) : StationListViewPresentation {
        val measureUnit: MeasureUnit
        val abbreviation = if (station.type == StationType.TIDES) {
            measureUnit = preferences.predictionLevels
            unitFormats.levelPostFix
        } else {
            measureUnit = preferences.predictionSpeed
            unitFormats.speedPostFix
        }
        val prediction = station.getPredictionRaw(DateTime.now().minusHours(3),
                Duration.standardHours(6), measureUnit)
        val position = "${station.latitude}, ${station.longitude}"
        val rez = when (station.type) {
            StationType.TIDES -> ContextCompat.getColor(context, com.mxmariner.tides.R.color.tideColor) to com.mxmariner.tides.R.drawable.ic_tide
            StationType.CURRENTS -> ContextCompat.getColor(context, com.mxmariner.tides.R.color.currentColor) to com.mxmariner.tides.R.drawable.ic_current
        }
        return StationListViewPresentation(prediction, station.name, position, station.timeZone,
                rez.first, rez.second, abbreviation)
    }
}