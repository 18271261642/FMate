package com.example.xingliansdk.network.api.jignfan

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.POST
import retrofit2.http.QueryMap
import java.util.HashMap

interface JingfanBpInterface {

    //标记惊帆血压，血压校准 测量
    @POST("/jingfan/blood_pressure_calibration")
    suspend fun markJingfanBp(@QueryMap value: HashMap<String, String>
    ): BaseResult<Any>


}