package com.example.xingliansdk.network.api.moreDevice

import androidx.lifecycle.MutableLiveData
import com.amap.api.mapcore.util.it
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.login.LoginApi
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.api.moreDevice.ConnectRecordApi.Companion.connectRecordApi
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.ui.deviceconn.ConnRecordListBean
import com.google.gson.Gson
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog

/**
 * Created by Admin
 *Date 2022/7/27
 */
class ConnectRecordViewModel : BaseViewModel() {


    val recordDeviceResult: MutableLiveData<ConnRecordListBean> = MutableLiveData()
    val recordMsg: MutableLiveData<String> = MutableLiveData()

    //删除连接记录
    val deleteRecord : MutableLiveData<Any> = MutableLiveData()
    val deleteMsg : MutableLiveData<Any> = MutableLiveData()

    //获取已经绑定过的列表
    fun getConnRecordDevice() {
        requestCustom({ connectRecordApi.getConnectedRecord() },
            { recordDeviceResult.postValue(it) }) { code, message ->
            recordMsg.postValue(message)
        }
    }

    //根据Mac删除记录
    fun deleteRecordByMac(mac : String){
        requestCustom({ connectRecordApi.deleteRecordByMac(mac)},{deleteRecord.postValue(it)}){
            code, message ->
            deleteMsg.postValue(message)
        }
    }

    private val resultUserInfo: MutableLiveData<LoginBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()

    fun setUserInfo(value: HashMap<String, String>) {
        requestCustom(
            {
                LoginApi.loginApi.setUserInfo(value)
            }, {
                TLog.error("==" + Gson().toJson(it))
                resultUserInfo.postValue(it)
            }
        ) { code, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }
}