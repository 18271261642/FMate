package com.app.fmate.ui.fragment.service

import com.amap.api.maps.model.LatLng

class SensorImpl : OnSensorStepListener {

    override fun onSensorUpdateSportData(
        distances: String?,
        calories: String?,
        hourSpeed: String?,
        pace: String?,
        latLngs: List<LatLng?>?
    ) {

    }
}