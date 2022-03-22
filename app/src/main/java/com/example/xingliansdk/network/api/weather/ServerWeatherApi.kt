package com.example.xingliansdk.network.api.weather

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean

class ServerWeatherApi private constructor() : AppApi<ServerWeatherInterface>() {
    companion object{
        val serverWeatherApi : ServerWeatherApi by lazy { ServerWeatherApi() }
    }


    suspend fun getServerWeatherData(locationStr: String): BaseResult<ServerWeatherBean> {
        val data =  apiInterface?.getServerWeatherApi(locationStr)
        return data!!
    }

    suspend fun postSleepSourcesData(remark : String,startTime : Long,endTime : Long,avgActive : IntArray,avgHeartRate : IntArray) : BaseResult<Any> {
        val data = apiInterface?.postSleepSourceApi(remark,startTime,endTime,avgActive,avgHeartRate)
        return data!!

    }
}