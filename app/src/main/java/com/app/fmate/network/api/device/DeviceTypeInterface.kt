package com.app.fmate.network.api.device

import com.app.fmate.network.BaseResult
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.HashMap

/**
 * Created by Admin
 *Date 2022/6/17
 */
interface DeviceTypeInterface {
    //获取设备类型，nordic或goodx芯片
    @GET("/product/get_info")
    suspend fun getDeviceType(@Query("productNumber") productNumber : String): BaseResult<Any>


    //获取产品属性列表
    @GET("/product/get_all_category")
    suspend fun getDeviceCategory() : BaseResult<DeviceCategoryBean>


    //戒指，保存心率开关状态
    @POST("/user/save_periodic_measurement_heart_rate")
    suspend fun saveRingHeartStatus(@QueryMap value: HashMap<String, String>) : BaseResult<Boolean>

    //戒指，保存体温周期
    @POST("/user/save_periodic_measurement_temp")
    suspend fun saveRingTempData(@QueryMap value: HashMap<String, String>) : BaseResult<Boolean>
}