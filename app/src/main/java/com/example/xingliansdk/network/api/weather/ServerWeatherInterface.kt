package com.example.xingliansdk.network.api.weather

import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean
import retrofit2.http.*

interface ServerWeatherInterface {

    @GET("/weather/find_weather")
    suspend fun getServerWeatherApi(@Query("location") location : String ) : BaseResult<ServerWeatherBean>

    @FormUrlEncoded
    @POST(("/watch_data/save_sleep"))
    suspend fun postSleepSourceApi(@Field("remark") remark: String,
    @Field("startTime") startTime : Long,
    @Field("endTime") endTime : Long,
    @Field("avgActive") avgActive : IntArray,
    @Field("avgHeartRate") avgHeartRate : IntArray) : BaseResult<Any>
}