package com.example.xingliansdk.ui.login.viewMode

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.login.LoginApi
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.requestCustom
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.shon.connector.utils.TLog
import okhttp3.MultipartBody


class UserViewModel : BaseViewModel() {
    val result: MutableLiveData<LoginBean> = MutableLiveData()
    val resultImg: MutableLiveData<Any> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    val msgOutLogin: MutableLiveData<String> = MutableLiveData()
    val resultOutLogin: MutableLiveData<Any> = MutableLiveData()
    val resultDelete: MutableLiveData<Any> = MutableLiveData()
    val msg1: MutableLiveData<String> = MutableLiveData()
    fun setUserInfo(value: HashMap<String, String>) {
        requestCustom(
            {
                LoginApi.loginApi.setUserInfo(value)
            }, {
                TLog.error("==" + Gson().toJson(it))
                result.postValue(it)
            }
        ) { code, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }

    fun outLogin(context: Context) {
        requestCustom({ LoginApi.loginApi.outLogin() },
            {
                resultOutLogin.postValue(it)

            }
        ) { code, message ->
            msgOutLogin.postValue(message)
        }
    }
    fun userDelete(verifyCode: String) {
        requestCustom({ LoginApi.loginApi.userDelete(verifyCode) },
            {
                resultDelete.postValue(it)
            }
        ) { code, message ->
            ShowToast.showToastLong(message!!)
        }
    }

    fun setImg(file: MultipartBody.Part)
    {
        requestCustom({LoginApi.loginApi.headImg(file)},
            {
                resultImg.postValue(it)
            }
        ) { code, message -> }
    }

}