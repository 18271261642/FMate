package com.example.xingliansdk.network.api.meView

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.network.api.dialView.RecommendDialViewApi
import com.example.xingliansdk.network.api.login.LoginApi
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.api.meView.MeViewApi.Companion.mMeViewApi
import com.example.xingliansdk.network.api.moreDevice.ConnectRecordApi
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.ui.deviceconn.ConnRecordListBean
import com.google.gson.Gson
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog


class MeViewModel : BaseViewModel() {
    val result: MutableLiveData<Any> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()


    fun getDialImg(pNumber : String ) {
        requestCustom({
            mMeViewApi.getDialImg(pNumber)
        }, {
            result.postValue(it)
        }
        ) { code, message ->
            message?.let {
                msg.postValue(it)
                ShowToast.showToastLong(it)
            }
        }
    }

    val resultDialImg: MutableLiveData<RecommendDialBean> = MutableLiveData()
    val msgDialImg: MutableLiveData<String> = MutableLiveData()
    fun findDialImg(hashMap: HashMap<String,String>) {
        requestCustom({
            RecommendDialViewApi.mRecommendDialViewApi.findDial(hashMap)
        }, {
            resultDialImg.postValue(it)
        }
        ) { code, message ->
            message?.let {
                msgDialImg.postValue(it)
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    val resultCheckDial: MutableLiveData<Any> = MutableLiveData()
    val msgCheckDial: MutableLiveData<String> = MutableLiveData()
   fun checkDialSate(data:String)
   {
       requestCustom({
           RecommendDialViewApi.mRecommendDialViewApi.checkDialSate(data)
       }, {
           resultCheckDial.postValue(it)
       }
       ) { code, message ->
           message?.let {
               msgCheckDial.postValue(it)
               ShowToast.showToastLong(it)
           }
       }
   }


    //连接的记录
    val recordDeviceResult: MutableLiveData<ConnRecordListBean> = MutableLiveData()
    val recordMsg: MutableLiveData<String> = MutableLiveData()

    //获取已经绑定过的列表
    fun getConnRecordDevice() {
        requestCustom({ ConnectRecordApi.connectRecordApi.getConnectedRecord() },
            { recordDeviceResult.postValue(it) }) { code, message ->
            recordMsg.postValue(message)
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




    private val resultUserInfo: MutableLiveData<LoginBean> = MutableLiveData()


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