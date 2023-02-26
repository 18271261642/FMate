package com.app.fmate.network.api.mapView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

class MapMotionViewApi private constructor() : AppApi<MapMotionViewInterface>() {
    companion object {
        val mMapMotionViewApi: MapMotionViewApi by lazy { MapMotionViewApi() }
    }

    suspend fun getMotionDistance(type:Int): BaseResult<Any> {
        return apiInterface?.getMotionDistance(type)!!
    }

}