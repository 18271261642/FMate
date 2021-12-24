package com.example.xingliansdk.network.api.sleepView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.bean.NetSleepBean

open class SleepApi private constructor() : AppApi<SleepInterface>() {
    companion object {
        val sleepApi: SleepApi by lazy { SleepApi() }
    }

    suspend fun getSleep(date: String): BaseResult<NetSleepBean> {
        val data = apiInterface?.getSleep(date)
        return data!!
    }
//
//    suspend fun setWeight(value: HashMap<String, String>): BaseResult<Any> {
//        val data = apiInterface?.userUpdate(value)
//        return data!!
//    }
}