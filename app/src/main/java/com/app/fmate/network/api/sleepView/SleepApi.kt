package com.app.fmate.network.api.sleepView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult
import com.app.fmate.bean.NetSleepBean

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