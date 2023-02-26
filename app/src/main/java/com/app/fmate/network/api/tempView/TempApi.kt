package com.app.fmate.network.api.tempView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

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