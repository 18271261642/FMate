package com.example.xingliansdk.network.api.weather

import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerWeatherInterface {

    @GET("/weather/find_weather")
    suspend fun getServerWeatherApi(@Query("location") location : String ) : BaseResult<ServerWeatherBean>
}