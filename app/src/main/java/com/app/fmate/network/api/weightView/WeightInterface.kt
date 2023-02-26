package com.app.fmate.network.api.weightView

import com.app.fmate.network.BaseResult
import com.app.fmate.bean.UpdateWeight
import retrofit2.http.*
import java.util.*

interface WeightInterface {
    @GET("/health/get_weight")
    suspend fun getWeight(
        @Query("type") type: String,
        @Query("date") date: String
    ): BaseResult<WeightModeBean>



    @FormUrlEncoded
    @POST("/health/update_weight")
    suspend fun updateWeight(
        @Field("weightList") value:String
    ):BaseResult<UpdateWeight>

    @POST("/health/delete_weight")
    suspend fun deleteWeight(
        @QueryMap value: HashMap<String, String>
    ) : BaseResult<Any>

}