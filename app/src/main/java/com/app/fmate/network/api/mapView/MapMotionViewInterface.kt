package com.app.fmate.network.api.mapView

import com.app.fmate.network.BaseResult
import retrofit2.http.*

interface MapMotionViewInterface {
    @GET("/motion_info/get_motion_distance")
    suspend fun getMotionDistance(
        @Query("type") type: Int
    ): BaseResult<Any>
}