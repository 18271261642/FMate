package com.example.xingliansdk.ui.setting.vewmodel

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.UIUpdate.UIUpdateApi
import com.example.xingliansdk.network.api.UIUpdate.UIUpdateBean

import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.utils.ExcelUtil
import com.google.gson.Gson
import com.shon.connector.utils.TLog
import com.shon.net.DownLoadRequest
import com.shon.net.callback.DownLoadCallback

class UIUpdateViewModel :BaseViewModel() {
    val result: MutableLiveData<UIUpdateBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun findUpdate(number:String,uuid:String="") {
//        request(
//            otaInterface.findUpdate("",0),
//            {result},false
//        )
        requestCustom({
            UIUpdateApi.uiUpdateApi.getUpdateZipFll(number,uuid)
        }, {
            result.postValue(it)
            TLog.error("res++"+ Gson().toJson(it))
        }
        ) { code, message ->
            TLog.error("res++" + message)
            msg.postValue(message)
        }
    }
    fun downLoadBin(bean: UIUpdateBean,callback:DownLoadCallback){
        DownLoadRequest(bean.ota).startDownLoad(
            "${ExcelUtil.filePath}/${bean?.fileName}"
        ,callback
        )
    }
}