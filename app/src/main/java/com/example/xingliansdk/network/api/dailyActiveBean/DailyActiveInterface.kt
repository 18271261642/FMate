package com.example.xingliansdk.network.api.dailyActiveBean

import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.bean.UpdateWeight
import retrofit2.http.*
import java.util.*

interface DailyActiveInterface {
    @GET("/health/get_daily_active")
    suspend fun getDailyActive(
        @Query("type") type: String,
        @Query("date") date: String
    ): BaseResult<Any>

}