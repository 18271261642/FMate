package com.example.xingliansdk.network.api.jignfan

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.network.BaseResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.QueryMap
import java.util.HashMap

interface JingfanBpInterface {

    //标记惊帆血压，血压校准 测量
    @POST("/jingfan/blood_pressure_calibration")
    suspend fun markJingfanBp(@QueryMap value: HashMap<String, String>
    ): BaseResult<Any>


    //上传数据，保存用户血压值
    @FormUrlEncoded
    @POST("/jingfan/upload_data")
    suspend fun uploadJingfanBp(
        @Field("data") data: IntArray,
        @Field("createTime") time : String

    ): BaseResult<Any>
}