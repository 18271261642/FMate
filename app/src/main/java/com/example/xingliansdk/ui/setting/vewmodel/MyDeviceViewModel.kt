package com.example.xingliansdk.ui.setting.vewmodel

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.device.DeviceTypeApi
import com.example.xingliansdk.network.api.login.LoginApi
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.api.moreDevice.ConnectRecordApi
import com.example.xingliansdk.network.api.otaUpdate.OTAUpdateApi
import com.example.xingliansdk.network.api.otaUpdate.OTAUpdateBean
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.utils.ExcelUtil
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.shon.connector.utils.TLog
import com.shon.net.DownLoadRequest
import com.shon.net.callback.DownLoadCallback

class MyDeviceViewModel : BaseViewModel() {

    val result: MutableLiveData<OTAUpdateBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    val resultUserInfo: MutableLiveData<LoginBean> = MutableLiveData()


    //获取设备平台类型汇顶平台或nordic平台
    val deviceType : MutableLiveData<Any> = MutableLiveData()



    fun findUpdate(number:String,code:Int) {
//        request(
//            otaInterface.findUpdate("",0),
//            {result},false
//        )
        requestCustom({
            OTAUpdateApi.otaUpdateApi.getUpdateZipFll(number,code)
        }, {
            result.postValue(it)
            TLog.error("res++"+Gson().toJson(it))
            }
        ) { code, message ->
            TLog.error("res++" + message)
            msg.postValue(message)
        }
    }
    fun downLoadZIP(bean:OTAUpdateBean,callback: DownLoadCallback)
    {
        DownLoadRequest(bean.ota).startDownLoad(
            "${ExcelUtil.filePath}/${bean?.fileName}",
            callback
        )
    }

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

    //获取设备类型
    fun getDeviceInfoType(productNumber: String) {
        requestCustom({
            DeviceTypeApi.deviceTypeApi.getDeviceTypeInfo(productNumber)
        }, {
            deviceType.postValue(it)
        }) { code, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }


    //删除连接记录
    val deleteRecord : MutableLiveData<Any> = MutableLiveData()
    val deleteMsg : MutableLiveData<Any> = MutableLiveData()

    //根据Mac删除记录
    fun deleteRecordByMac(mac : String){
        requestCustom({ ConnectRecordApi.connectRecordApi.deleteRecordByMac(mac)},{deleteRecord.postValue(it)}){
                code, message ->
            deleteMsg.postValue(message)
        }
    }


    //设置戒指心率状态
    val ringHtData : MutableLiveData<Any> = MutableLiveData()
    val ringHtMsg : MutableLiveData<Any> = MutableLiveData()

    //戒指温度
    val ringTempData : MutableLiveData<Any> = MutableLiveData()
    val ringTempMsg : MutableLiveData<Any> = MutableLiveData()

    fun saveRingHtData(map : HashMap<String,String>){
        requestCustom({DeviceTypeApi.deviceTypeApi.saveRingHtStatus(map)},{
            ringHtData.postValue(it)
        },{
            code, message ->
            ringHtMsg.postValue(message)
        })
    }

    fun saveRingTempData(map : HashMap<String,String>){
        requestCustom({DeviceTypeApi.deviceTypeApi.saveRingTempStatus(map)},{
            ringTempData.postValue(it)
        },{
            code, message ->
            ringTempMsg.postValue(message)
        })
    }
}

