package com.example.xingliansdk.network.api.heartView

import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.login.LoginBean
import retrofit2.http.*

interface HeartRateInterface {
    @GET("/health/get_heart_rate")
    suspend fun getHeartRate(
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): BaseResult<Any>
}