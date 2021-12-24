package com.example.xingliansdk.network.api.tempView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

open class TempApi  private constructor(): AppApi<TempInterface>(){
    companion object{
        val tempApi: TempApi by lazy { TempApi() }
    }
    suspend fun getTemp(startTime: String,endTime: String):BaseResult<TemperatureVoBean>
    {
        val data= apiInterface?.getTemp(startTime,endTime)
        return data!!
    }
}