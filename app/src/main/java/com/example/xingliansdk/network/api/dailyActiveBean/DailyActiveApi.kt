package com.example.xingliansdk.network.api.dailyActiveBean

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.bean.UpdateWeight
import java.util.*

open class DailyActiveApi private constructor() : AppApi<DailyActiveInterface>() {
    companion object {
        val dailyActiveApi: DailyActiveApi by lazy { DailyActiveApi() }
    }

    suspend fun getDailyActive(type: String, date: String): BaseResult<Any> {
        val data = apiInterface?.getDailyActive(type, date)
        return data!!
    }


}