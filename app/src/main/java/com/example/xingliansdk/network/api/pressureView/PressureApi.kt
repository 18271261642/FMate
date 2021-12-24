package com.example.xingliansdk.network.api.pressureView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

open class PressureApi  private constructor(): AppApi<PressureInterface>(){
    companion object{
        val pressureApi: PressureApi by lazy { PressureApi() }
    }
    suspend fun getPressure(startTime: String,endTime: String):BaseResult<PressureVoBean>
    {
        val data= apiInterface?.getPressure(startTime,endTime)
        return data!!
    }
}