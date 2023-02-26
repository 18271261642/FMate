package com.app.fmate.network.api.heartView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

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