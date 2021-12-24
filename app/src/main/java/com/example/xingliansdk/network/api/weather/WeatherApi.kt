package com.example.xingliansdk.network.api.weather

import com.example.xingliansdk.network.api.weather.bean.WeatherBean
import com.shon.net.BaseApi

class WeatherApi private constructor():BaseApi<WeatherInterface>("https://devapi.qweather.com/v7/"){
    companion object{
        val weatherApi:WeatherApi by lazy { WeatherApi() }
    }
    suspend fun get3dWeather(key:String,location:String,lang:String="zh",unit:String="m") :WeatherBean?{
        return apiInterface?.load3d(key, location,lang,unit)
    }
}