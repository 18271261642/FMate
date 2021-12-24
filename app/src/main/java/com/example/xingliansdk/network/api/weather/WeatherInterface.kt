package com.example.xingliansdk.network.api.weather

import com.example.xingliansdk.network.api.weather.bean.WeatherBean
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {

    @GET("weather/3d")
    suspend fun load3d(@Query("key") key:String
                       ,@Query("location") location:String
                       ,@Query("lang") lang:String
                       ,@Query("unit") unit:String
    ): WeatherBean
}

