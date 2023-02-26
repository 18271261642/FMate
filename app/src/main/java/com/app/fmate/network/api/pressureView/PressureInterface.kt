package com.app.fmate.network.api.pressureView

import com.app.fmate.network.BaseResult
import retrofit2.http.*

interface PressureInterface {
    @GET("/health/get_pressure")
    suspend fun getPressure(
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): BaseResult<PressureVoBean>
}