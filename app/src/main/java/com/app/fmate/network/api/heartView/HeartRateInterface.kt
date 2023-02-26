package com.app.fmate.network.api.heartView

import com.app.fmate.network.BaseResult
import retrofit2.http.*

interface HeartRateInterface {
    @GET("/health/get_heart_rate")
    suspend fun getHeartRate(
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): BaseResult<Any>
}