package com.example.xingliansdk.network.api.moreDevice

import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.ui.deviceconn.ConnRecordListBean
import com.example.xingliansdk.ui.deviceconn.ConnectedDeviceBean
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 获取已连接记录
 * Created by Admin
 *Date 2022/7/27
 */
interface ConnectRecordInterface {

    //获取连接记录
    @GET("/user/get_equip_conn_record")
    suspend fun getConnectRecord() : BaseResult<ConnRecordListBean>

    //删除设备连接记录
    @POST("/user/delete_equip_conn_record")
    suspend fun deleteUserConnRecord(@Query("mac") mac : String) : BaseResult<Any>
}