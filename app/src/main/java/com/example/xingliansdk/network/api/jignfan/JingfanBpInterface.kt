package com.example.xingliansdk.network.api.jignfan

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.ui.bp.MeasureBpBean
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.QueryMap
import java.util.HashMap

interface JingfanBpInterface {

    //标记惊帆血压，血压校准 测量
    @POST("/jingfan/blood_pressure_calibration")
    suspend fun markJingfanBp(@QueryMap value: HashMap<String, Any>
    ): BaseResult<Any>


    /**
     *  resultMap.put(("data"+(checkCount+1)),bpValue)
    resultMap.put(("sbp"+(checkCount+1)),checkHBpTv.text.toString())
    resultMap.put(("dbp"+(checkCount+1)),checkLBpTv.text.toString())
     */
//
//    @FormUrlEncoded
//    @POST("/jingfan/blood_pressure_calibration")
//    suspend fun markJingfanBp(@Field("data") data1 : String,@Field("data2") data2 : String,@Field("data3") data3 : String,
//                              @Field("sbp1") sbp1 : Int,@Field("sbp2") sbp2 : Int,@Field("sbp3") sbp3 : Int,
//                              @Field("dbp1") dbp1 : Int, @Field("dbp2") dbp2 : Int, @Field("dbp3") dbp3 : Int
//    ): BaseResult<Any>

    //上传数据，保存用户血压值
    @FormUrlEncoded
    @POST("/jingfan/upload_data")
    suspend fun uploadJingfanBp(
        @Field("data") data: String,
        @Field("createTime") time : String

    ): BaseResult<MeasureBpBean>
}