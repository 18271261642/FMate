package com.app.fmate.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.api.appUpdate.AppUpdateApi
import com.app.fmate.network.api.appUpdate.AppUpdateBean
import com.app.fmate.network.requestCustom
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog


class AppStartViewModel : BaseViewModel() {
    val result: MutableLiveData<AppUpdateBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    val test: MutableLiveData<String> = MutableLiveData()
    fun appUpdate(appName: String, versionCode: Int) {
        requestCustom(
            { AppUpdateApi.AppUpdateApi.getApp(appName, versionCode) },
            {
                result.postValue(it)
            }
        ) { code, message ->
            message?.let {
                TLog.error("it==" + it + "==code ==" + code)
                msg.postValue(code.toString())
                if (code != 312)
                    ShowToast.showToastLong(it)
            }
        }
    }
    fun appLoginTest() {
        requestCustom(
            { AppUpdateApi.AppUpdateApi.getToken()!! },
            {
                test.postValue(it)

            }
        ) { code, message ->
            message?.let {
                TLog.error("it==" + it)
                msg.postValue(code.toString())
//                    if (code!=312)
//                        ShowToast.showToastLong(it)
            }
        }
    }

}