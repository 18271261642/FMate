package com.app.fmate.network.api.bloodPressureView

import com.app.fmate.network.BaseResult
import retrofit2.http.*
import java.util.*

interface BloodPressureInterface {
    @GET("/health/get_blood_pressure")
    suspend fun getBloodPressure(
        @Query("date") date: String
    ): BaseResult<BloodPressureVoBean>

    @FormUrlEncoded
    @POST("/health/save_blood_pressure")
    suspend fun saveBloodPressure(
        @Field("createTime") createTime: Long,
        @Field("systolicPressure") systolicPressure: Int,
        @Field("diastolicPressure") diastolicPressure: Int
    ): BaseResult<Any>

    @POST("/health/delete_blood_pressure")
    suspend fun deleteBloodPressure(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<Any>
}