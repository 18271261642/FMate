package com.app.fmate.network.api.bloodOxygen

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

open class BloodOxygenApi  private constructor(): AppApi<BloodOxygenInterface>(){
    companion object{
        val bloodOxygenApi: BloodOxygenApi by lazy { BloodOxygenApi() }
    }
    suspend fun getBloodOxygen(startTime: String,endTime: String):BaseResult<BloodOxygenVoBean>
    {
        val data= apiInterface?.getBloodOxygen(startTime,endTime)
        return data!!
    }
}