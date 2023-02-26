package com.app.fmate.ui.login.viewMode

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.api.login.LoginApi
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.network.changSang.api.ChangSangApi
import com.app.fmate.network.changSang.api.ChangSangBean
import com.app.fmate.network.requestCustom
import com.app.fmate.network.requestCustomChangs
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.shon.connector.utils.TLog

class LoginViewModel : BaseViewModel() {
    val result: MutableLiveData<LoginBean> = MutableLiveData()
    val resultPassword: MutableLiveData<LoginBean> = MutableLiveData()
    var resultUpdatePhone: MutableLiveData<Any> = MutableLiveData()
    var resultSaveAppeal: MutableLiveData<Any> = MutableLiveData()
    val areaCodeResult: MutableLiveData<LoginBean> = MutableLiveData()
    var resultCheckPassword: MutableLiveData<Any> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    var msgUpdatePassword: MutableLiveData<LoginBean> = MutableLiveData()
    var msgCheckVerifyCode: MutableLiveData<Any> = MutableLiveData()

    fun loginRegistered(value: HashMap<String, String>) {
        requestCustom({
            LoginApi.loginApi.getLogin(value)
        },
            {
                result.postValue(it)
            }) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                msg.postValue(it)
                ShowToast.showToastLong(it)
            }
        }
    }


    fun getVerifyCode(phone: String, areaCode: String, password: String, type: String = "0") {
        if (phone.isNullOrEmpty()) {
            ShowToast.showToastLong("请输入手机号")
            return
        }
        requestCustom(
            {
                LoginApi.loginApi.getVerifyCode(phone, areaCode, password, type)
            }, {
                TLog.error("==" + Gson().toJson(it))
                areaCodeResult.postValue(it)
            }
        ) { code, message ->
            message?.let {
                ShowToast.showToastLong(it)
            }
        }
    }

    fun setPassword(phone: String, password: String, areaCode: String, verifyCode: String) {
        requestCustom({
            LoginApi.loginApi.register(phone, password, areaCode, verifyCode)
        },
            {
                TLog.error("首次设置密码")
                resultPassword.postValue(it)
            }
        ) { code, message ->
            message?.let {
                ShowToast.showToastLong(it)
            }
        }
    }

    fun updatePassword(value: HashMap<String, String>) {
        requestCustom(
            {
                LoginApi.loginApi.updatePassword(value)
            }, {
                msgUpdatePassword.postValue(it)
            }
        ) { code, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }

    fun checkVerifyCode(value: HashMap<String, String>) {
        requestCustom(
            {
                LoginApi.loginApi.checkVerifyCode(value)
            }, {
                msgCheckVerifyCode.postValue(it)
            }
        ) { _, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }

    fun checkPassword(value: HashMap<String, String>) {
        requestCustom(
            {
                LoginApi.loginApi.checkPassword(value)
            }, {
                resultCheckPassword.postValue(it)
            }
        ) { _, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }


    fun updatePhone(value: HashMap<String, String>) {
        requestCustom(
            {
                LoginApi.loginApi.updatePhone(value)
            }, {
                resultUpdatePhone.postValue(it)
            }
        ) { _, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }

    fun saveAppeal(value: HashMap<String, String>) {
        requestCustom(
            {
                LoginApi.loginApi.saveAppeal(value)
            }, {
                resultSaveAppeal.postValue(it)
            }
        ) { _, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }
    var resultChangSang: MutableLiveData<Any> = MutableLiveData()
    fun changSangLogin(value: HashMap<String, String>) {
        requestCustomChangs(
            {
                ChangSangApi.mChangSangApi.login(value)
            }, {
                resultChangSang.postValue(it)
            }
        ) { _, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }
    var resultChangSangUpdate: MutableLiveData<ChangSangBean> = MutableLiveData()
    fun changSangUpdateLoad(value: HashMap<String, String>) {
        requestCustomChangs(
            {
                ChangSangApi.mChangSangApi.updateLoad(value)
            }, {
                resultChangSangUpdate.postValue(it)
            }
        ) { _, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }

    fun changSangFetchPar(value: HashMap<String, String>) {
        requestCustomChangs(
            {
                ChangSangApi.mChangSangApi.fetchPar(value)
            }, {
                resultChangSang.postValue(it)
            }
        ) { _, message ->
            message?.let {
                msg.postValue(it)
                TLog.error("==" + Gson().toJson(it))
                ShowToast.showToastLong(it)
            }
        }
    }
}