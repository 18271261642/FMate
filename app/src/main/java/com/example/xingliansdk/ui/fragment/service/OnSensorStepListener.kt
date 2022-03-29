package com.example.xingliansdk.ui.fragment.service

import com.amap.api.maps.model.LatLng

interface OnSensorStepListener {
    fun onSensorUpdateSportData(
        distances: String?,
        calories: String?,
        hourSpeed: String?,
        pace: String?,
        latLngs: List<LatLng?>?
    )

}