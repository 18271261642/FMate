package com.app.fmate.ui.login.viewMode

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.api.login.LoginApi
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.network.api.mainView.MainApi
import com.app.fmate.network.requestCustom
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

    val getUserInfoResult : MutableLiveData<LoginBean> = MutableLiveData()



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


    fun userInfo(){
        requestCustom({
            MainApi.mMainApi.getPersonalInfo()
        },{
            TLog.error("zhengcit="+Gson().toJson(it))
            getUserInfoResult.postValue(it)
        }) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                //ShowToast.showToastLong(it)
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