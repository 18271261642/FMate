package com.app.fmate.network.api.bloodOxygen

import com.app.fmate.network.BaseResult
import retrofit2.http.*

interface BloodOxygenInterface {
    @GET("/health/get_blood_oxygen")
    suspend fun getBloodOxygen(
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): BaseResult<BloodOxygenVoBean>
}