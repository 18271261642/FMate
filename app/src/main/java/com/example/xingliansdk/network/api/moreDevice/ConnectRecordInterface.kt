package com.example.xingliansdk.network.api.moreDevice

import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.ui.deviceconn.ConnRecordListBean
import com.example.xingliansdk.ui.deviceconn.ConnectedDeviceBean
import retrofit2.http.GET

/**
 * 获取已连接记录
 * Created by Admin
 *Date 2022/7/27
 */
interface ConnectRecordInterface {

    @GET("/user/get_equip_conn_record")
    suspend fun getConnectRecord() : BaseResult<ConnRecordListBean>
}