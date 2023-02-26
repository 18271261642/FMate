package com.app.fmate.network.api.weightView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult
import com.app.fmate.bean.UpdateWeight
import java.util.*

open class WeightApi private constructor() : AppApi<WeightInterface>() {
    companion object {
        val weightApi: WeightApi by lazy { WeightApi() }
    }

    suspend fun getWeight(type: String, date: String): BaseResult<WeightModeBean> {
        val data = apiInterface?.getWeight(type, date)
        return data!!
    }

    suspend fun setWeight(value:  String): BaseResult<UpdateWeight> {
        val data = apiInterface?.updateWeight(value)
        return data!!
    }
    suspend fun deleteWeight(value: HashMap<String, String>): BaseResult<Any> {
        val data = apiInterface?.deleteWeight(value)
        return data!!
    }
}