package com.example.xingliansdk.network.api.moreDevice

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.ui.deviceconn.ConnRecordListBean
import com.example.xingliansdk.ui.deviceconn.ConnectedDeviceBean
import java.util.*

/**
 * Created by Admin
 *Date 2022/7/27
 */
class ConnectRecordApi private constructor() : AppApi<ConnectRecordInterface>(){

    companion object{
        val connectRecordApi : ConnectRecordApi by lazy { ConnectRecordApi() }
    }

    //获取连接记录
    suspend fun getConnectedRecord() : BaseResult<ConnRecordListBean>{
        return apiInterface?.getConnectRecord()!!
    }

    //根据Mac删除连接记录
    suspend fun deleteRecordByMac(mac : String) : BaseResult<Any>{
        return apiInterface?.deleteUserConnRecord(mac.toLowerCase(Locale.ROOT))!!
    }
}