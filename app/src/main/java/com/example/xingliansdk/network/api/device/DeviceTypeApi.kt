package com.example.xingliansdk.network.api.device

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

/**
 *获取设备平台汇顶或nordic
 * Created by Admin
 *Date 2022/6/17
 */
class DeviceTypeApi private constructor() : AppApi<DeviceTypeInterface>() {

    companion object{
        val deviceTypeApi : DeviceTypeApi by lazy { DeviceTypeApi() }
    }


    suspend fun getDeviceTypeInfo(productNumber : String):BaseResult<Any>{
        return apiInterface?.getDeviceType(productNumber)!!
    }

    suspend fun getAllDeviceCategoryData() : BaseResult<DeviceCategoryBean>{
        return apiInterface?.getDeviceCategory()!!
    }


    //保存戒指心率周期开关
    suspend fun saveRingHtStatus(value : HashMap<String,String>) : BaseResult<Boolean>{
        return apiInterface?.saveRingTempData(value)!!
    }

    //保存戒指温度
    suspend fun saveRingTempStatus(value: HashMap<String, String>) : BaseResult<Boolean>{
        return apiInterface?.saveRingTempData(value)!!
    }
}