package com.example.xingliansdk.network.api.bloodOxygen

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

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