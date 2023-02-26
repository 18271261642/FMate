package com.app.fmate.network.api.pressureView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

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