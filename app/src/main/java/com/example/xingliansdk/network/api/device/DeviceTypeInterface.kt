package com.example.xingliansdk.network.api.device

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Admin
 *Date 2022/6/17
 */
interface DeviceTypeInterface {
    @GET("/product/get_info")
    suspend fun getDeviceType(@Query("productNumber") productNumber : String): BaseResult<Any>


    //获取产品属性列表
    @GET("/product/get_all_category")
    suspend fun getDeviceCategory() : BaseResult<DeviceCategoryBean>


}