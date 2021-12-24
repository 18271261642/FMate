package com.example.xingliansdk.network.api.homeView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.javaMapView.MapVoBean
import java.util.*

class HomeViewApi private constructor() : AppApi<HomeViewInterface>() {
    companion object {
        val mHomeViewApi: HomeViewApi by lazy { HomeViewApi() }
    }

    suspend fun setHeartRate(
        startTime: String,
        endTime: String,
        heartRateList: IntArray
    ): BaseResult<Any> {
        // TLog.error("数据=="+Gson().toJson(apiInterface?.saveHeartRate(startTime,endTime,heartRateList)!!))
        return apiInterface?.saveHeartRate(startTime, endTime, heartRateList)!!
    }

    suspend fun  saveDailyActive( startTime: Long,
                                  endTime: Long,
                                  data: String):BaseResult<Any>
    {
        return apiInterface?.saveDailyActive(startTime, endTime,data)!!
    }
    suspend fun setTemperature(
        startTime: String,
        endTime: String,
        heartRateList: IntArray
    ): BaseResult<Any> {
        // TLog.error("数据=="+Gson().toJson(apiInterface?.saveHeartRate(startTime,endTime,heartRateList)!!))
        return apiInterface?.saveTemperature(startTime, endTime, heartRateList)!!
    }
    suspend fun setPressure(
        startTime: String,
        endTime: String,
        data: String
    ): BaseResult<Any> {
        return apiInterface?.savePressure(startTime, endTime, data)!!
    }
    suspend fun saveBloodPressure(
        startTime: String,
        endTime: String,
        data: String
    ): BaseResult<Any> {
        return apiInterface?.saveBloodPressure(startTime, endTime, data)!!
    }


    suspend fun saveBloodOxygen(value: HashMap<String, Any>): BaseResult<Any> {
        return apiInterface?.saveBloodOxygen(value)!!
    }

    suspend fun saveBloodOxygen(
        startTime: String,
        endTime: String,
        list: IntArray
    ): BaseResult<Any> {
        return apiInterface?.saveBloodOxygen(startTime, endTime, list)!!
    }

    suspend fun saveSleep(value: HashMap<String, Any>): BaseResult<Any> {
        return apiInterface?.saveSleep(value)!!
    }

    suspend fun saveSleep(
        startTime: String,
        endTime: String,
        apneaTime: String,
        apneaSecond: String,
        avgHeartRate: String,
        minHeartRate: String,
        maxHeartRate: String,
        respiratoryQuality: String,
        sleepList: String
    ): BaseResult<Any> {
        return apiInterface?.saveSleep(
            startTime,
            endTime,
            apneaTime,
            apneaSecond,
            avgHeartRate,
            minHeartRate,
            maxHeartRate,
            respiratoryQuality,
            sleepList
        )!!
    }

    suspend fun saveUserEquip(value: HashMap<String, Any>):BaseResult<Any>{
        return apiInterface?.saveUserEquip(value)!!
    }
    suspend fun getPopular(value: HashMap<String, Any>):BaseResult<PopularScienceBean>{
        return apiInterface?.getPopular(value)!!
    }
    suspend fun getHomeCard():BaseResult<HomeCardVoBean>{
        return apiInterface?.getHomeCard()!!
    }
    suspend fun motionInfoSave(value: HashMap<String, Any>):BaseResult<Any>{
        return apiInterface?.motionInfoSave(value)!!
    }
    suspend fun motionInfoGetList(value: HashMap<String, Any>):BaseResult<MapVoBean>{
        return apiInterface?.motionInfoGetList(value)!!
    }

}