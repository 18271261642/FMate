package com.app.fmate.network.api.sleepView

import com.app.fmate.network.BaseResult
import com.app.fmate.bean.NetSleepBean
import retrofit2.http.*
import java.util.*

interface SleepInterface {
    @GET("/health/get_sleep")
    suspend fun getSleep(
        @Query("startTime") startTime: String
    ): BaseResult<NetSleepBean>

    @POST("/user/update")
    suspend fun userUpdate(
        @QueryMap value: HashMap<String, String>
    ) : BaseResult<Any>
}