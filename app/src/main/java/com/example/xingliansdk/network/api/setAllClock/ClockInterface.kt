package com.example.xingliansdk.network.api.setAllClock

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.*

interface ClockInterface {
    @POST("/user/save_alarm_clock")
    suspend fun saveAlarmClock(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<Any>

    @POST("/user/save_schedule")
    suspend fun saveSchedule(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<Any>

    @POST("/user/save_take_medicine")
    suspend fun saveTakeMedicine(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<Any>

    @GET("/user/get_remind")
    suspend fun getRemind(
        @Query("type") data: String,
    ): BaseResult<ClockListBean>
}