package com.app.fmate.network.api.dailyActiveBean

import com.app.fmate.network.BaseResult
import retrofit2.http.*
import java.util.*

interface DailyActiveInterface {
    @GET("/health/get_daily_active")
    suspend fun getDailyActive(
        @Query("type") type: String,
        @Query("date") date: String
    ): BaseResult<Any>

}