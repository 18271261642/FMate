package com.example.xingliansdk.network.api.heartView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

open class HeartRateApi  private constructor(): AppApi<HeartRateInterface>(){
    companion object{
        val heartRateApi: HeartRateApi by lazy { HeartRateApi() }
    }
    suspend fun getHeartRate(startTime: String,endTime: String):BaseResult<Any>
    {
        val data= apiInterface?.getHeartRate(startTime,endTime)
        return data!!
    }

}