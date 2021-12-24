package com.example.xingliansdk.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.callback.UnPeekLiveData
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.api.mainView.MainApi
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
}