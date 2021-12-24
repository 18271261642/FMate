package com.example.xingliansdk.network.api.setAllClock

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

open class SetAllClockApi  private constructor(): AppApi<ClockInterface>(){
    companion object{
        val clockApi: SetAllClockApi by lazy { SetAllClockApi() }
    }
    suspend fun getRemind(type: String="0"):BaseResult<ClockListBean>
    {
        val data= apiInterface?.getRemind(type)
        return data!!
    }
    suspend fun saveAlarmClock(data: HashMap<String,String>):BaseResult<Any>
    {
        return apiInterface?.saveAlarmClock(data)!!
    }
    suspend fun saveSchedule(data: HashMap<String,String>):BaseResult<Any>
    {
        return apiInterface?.saveSchedule(data)!!
    }
    suspend fun saveTakeMedicine(data: HashMap<String,String>):BaseResult<Any>
    {
        return apiInterface?.saveTakeMedicine(data)!!
    }
}