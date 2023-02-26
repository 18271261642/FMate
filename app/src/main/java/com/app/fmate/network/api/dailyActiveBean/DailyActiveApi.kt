package com.app.fmate.network.api.dailyActiveBean

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

open class DailyActiveApi private constructor() : AppApi<DailyActiveInterface>() {
    companion object {
        val dailyActiveApi: DailyActiveApi by lazy { DailyActiveApi() }
    }

    suspend fun getDailyActive(type: String, date: String): BaseResult<Any> {
        val data = apiInterface?.getDailyActive(type, date)
        return data!!
    }


}