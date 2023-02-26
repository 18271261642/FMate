package com.app.fmate.network.api.weather

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult
import com.app.fmate.network.api.weather.bean.ServerWeatherBean

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