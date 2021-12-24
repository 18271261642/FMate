package com.example.xingliansdk.network.api.pressureView

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.*

interface PressureInterface {
    @GET("/health/get_pressure")
    suspend fun getPressure(
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): BaseResult<PressureVoBean>
}