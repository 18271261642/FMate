package com.example.xingliansdk.ui.setting.vewmodel

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.login.LoginApi
import com.example.xingliansdk.network.api.login.LoginBean
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
}

