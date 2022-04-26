package com.example.xingliansdk.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.callback.UnPeekLiveData
import com.example.xingliansdk.network.api.appUpdate.AppUpdateApi
import com.example.xingliansdk.network.api.appUpdate.AppUpdateBean
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.api.mainView.MainApi
import com.example.xingliansdk.network.api.otaUpdate.OTAUpdateApi
import com.example.xingliansdk.network.api.otaUpdate.OTAUpdateBean
import com.example.xingliansdk.network.api.weather.ServerWeatherApi
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.utils.ShowToast
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog


class MainViewModel : BaseViewModel() {
    //获取数据库保存数据
    var textValue = MutableLiveData<Int>()
    var address = MutableLiveData<String>()
    var name =MutableLiveData<String>()
    val result: UnPeekLiveData<LoginBean> = UnPeekLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    var userInfo= UnPeekLiveData<LoginBean>()


    val appResult : MutableLiveData<AppUpdateBean> = MutableLiveData()
    val appUpdateMsg: MutableLiveData<String> = MutableLiveData()

    val serverWeatherData : MutableLiveData<ServerWeatherBean> = MutableLiveData()

    val resultOta: MutableLiveData<OTAUpdateBean> = MutableLiveData()

    init {
        textValue.value = Hawk.get<Int>("step")
        address.value = Hawk.get<String>("address")
       userInfo.value=LoginBean()
    }

    fun userInfo(){
        requestCustom({
                    MainApi.mMainApi.getPersonalInfo()
        },{
            TLog.error("zhengcit="+Gson().toJson(it))
          result.postValue(it)
        }) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }

    }
    open fun setUserInfo(userInfo:LoginBean)
    {
        this.userInfo.value=userInfo
    }
    open fun  getUserInfo(): LoginBean {
        return userInfo.value ?: LoginBean()
    }

    open fun setName(name: String) {
        this.name.value = name
    }

    open fun  getName(): String {
        return name.value ?: ""
    }

    open fun setAddress(address: String) {
        this.address.value = address
    }

    open fun getAddress(): String {
        return address.value ?: ""
    }

    open fun setText(text: Int) {
        this.textValue.value = text
    }

    open fun getText(): Int {
        return textValue.value ?: 0
    }


    fun findUpdate(number:String,code:Int) {
//        request(
//            otaInterface.findUpdate("",0),
//            {result},false
//        )
        requestCustom({
            OTAUpdateApi.otaUpdateApi.getUpdateZipFll(number,code)
        }, {
            resultOta.postValue(it)
            TLog.error("res++"+Gson().toJson(it))
        }
        ) { code, message ->
            TLog.error("res++" + message)
            msg.postValue(message)
        }
    }


    fun getWeatherServer(locationStr : String){

        requestCustom({
            ServerWeatherApi.serverWeatherApi.getServerWeatherData(locationStr)
        },
            {
                serverWeatherData.postValue(it)
            }) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                msg.postValue(it)
                ShowToast.showToastLong(it)
            }
        }

    }


    //APP版本更新
    fun appUpdate(appName: String, versionCode: Int) {
        requestCustom(
            { AppUpdateApi.AppUpdateApi.getApp(appName, versionCode) },
            {
                appResult.postValue(it)
            }
        ) { code, message ->
            message?.let {
                TLog.error("it==" + it + "==code ==" + code)
                appUpdateMsg.postValue(code.toString())
                if (code != 312)
                    ShowToast.showToastLong(it)
            }
        }
    }
}