package com.app.fmate.network.api.meView

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.api.dialView.RecommendDialBean
import com.app.fmate.network.api.dialView.RecommendDialViewApi
import com.app.fmate.network.api.login.LoginApi
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.network.api.meView.MeViewApi.Companion.mMeViewApi
import com.app.fmate.network.api.moreDevice.ConnectRecordApi
import com.app.fmate.network.requestCustom
import com.app.fmate.ui.deviceconn.ConnRecordListBean
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


    //???????????????
    val recordDeviceResult: MutableLiveData<ConnRecordListBean> = MutableLiveData()
    val recordMsg: MutableLiveData<String> = MutableLiveData()

    //??????????????????????????????
    fun getConnRecordDevice() {
        requestCustom({ ConnectRecordApi.connectRecordApi.getConnectedRecord() },
            { recordDeviceResult.postValue(it) }) { code, message ->
            recordMsg.postValue(message)
        }
    }

    //??????????????????
    val deleteRecord : MutableLiveData<Any> = MutableLiveData()
    val deleteMsg : MutableLiveData<Any> = MutableLiveData()

    //??????Mac????????????
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