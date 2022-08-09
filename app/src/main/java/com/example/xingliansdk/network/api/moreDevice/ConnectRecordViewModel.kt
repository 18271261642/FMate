package com.example.xingliansdk.network.api.moreDevice

import androidx.lifecycle.MutableLiveData
import com.amap.api.mapcore.util.it
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.moreDevice.ConnectRecordApi.Companion.connectRecordApi
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.ui.deviceconn.ConnRecordListBean

/**
 * Created by Admin
 *Date 2022/7/27
 */
class ConnectRecordViewModel : BaseViewModel() {


    val recordDeviceResult: MutableLiveData<ConnRecordListBean> = MutableLiveData()
    val recordMsg: MutableLiveData<String> = MutableLiveData()

    //获取已经绑定过的列表
    fun getConnRecordDevice() {
        requestCustom({ connectRecordApi.getConnectedRecord() },
            { recordDeviceResult.postValue(it) }) { code, message ->
            recordMsg.postValue(message)
        }
    }

}