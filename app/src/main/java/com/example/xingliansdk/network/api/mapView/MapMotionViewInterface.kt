package com.example.xingliansdk.network.api.mapView

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.*
import java.util.HashMap

interface MapMotionViewInterface {
    @GET("/motion_info/get_motion_distance")
    suspend fun getMotionDistance(
        @Query("type") type: Int
    ): BaseResult<Any>
}