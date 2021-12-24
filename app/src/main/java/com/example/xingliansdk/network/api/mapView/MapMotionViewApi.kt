package com.example.xingliansdk.network.api.mapView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

class MapMotionViewApi private constructor() : AppApi<MapMotionViewInterface>() {
    companion object {
        val mMapMotionViewApi: MapMotionViewApi by lazy { MapMotionViewApi() }
    }

    suspend fun getMotionDistance(type:Int): BaseResult<Any> {
        return apiInterface?.getMotionDistance(type)!!
    }

}