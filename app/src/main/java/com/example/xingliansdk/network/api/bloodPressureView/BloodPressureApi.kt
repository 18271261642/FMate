package com.example.xingliansdk.network.api.bloodPressureView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.bean.UpdateWeight
import java.util.*

open class BloodPressureApi private constructor() : AppApi<BloodPressureInterface>() {
    companion object {
        val bloodPressureApi: BloodPressureApi by lazy { BloodPressureApi() }
    }

    suspend fun getBloodPressure(  date: String): BaseResult<BloodPressureVoBean> {
        val data = apiInterface?.getBloodPressure(date)
        return data!!
    }

    suspend fun saveBloodPressure(createTime: Long,systolicPressure:Int,diastolicPressure: Int): BaseResult<Any> {
        val data = apiInterface?.saveBloodPressure(createTime,systolicPressure,diastolicPressure)
        return data!!
    }
    suspend fun deleteBloodPressure(value: HashMap<String, String>): BaseResult<Any> {
        val data = apiInterface?.deleteBloodPressure(value)
        return data!!
    }

}