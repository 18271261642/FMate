package com.app.fmate.network.api.moreDevice

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult
import com.app.fmate.ui.deviceconn.ConnRecordListBean
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