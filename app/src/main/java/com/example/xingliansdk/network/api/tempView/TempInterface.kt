package com.example.xingliansdk.network.api.tempView

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.*

interface TempInterface {
    @GET("/health/get_temperature")
    suspend fun getTemp(
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): BaseResult<TemperatureVoBean>
}