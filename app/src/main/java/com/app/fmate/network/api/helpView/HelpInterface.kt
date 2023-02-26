package com.app.fmate.network.api.helpView

import com.app.fmate.network.BaseResult
import retrofit2.http.*
import java.util.*
import kotlin.collections.HashMap

interface HelpInterface {
    @GET("/health/get_blood_pressure")
    suspend fun getBloodPressure(
        @Query("date") date: String
    ): BaseResult<Any>

    @FormUrlEncoded
    @POST("/feedback/save")
    suspend fun saveFeedback(
        @FieldMap data: HashMap<String,Any>
    ): BaseResult<Any>

    @POST("/health/delete_blood_pressure")
    suspend fun deleteBloodPressure(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<Any>
}