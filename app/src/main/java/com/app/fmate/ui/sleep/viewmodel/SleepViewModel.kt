package com.app.fmate.ui.sleep.viewmodel
import androidx.lifecycle.MutableLiveData
import com.app.fmate.bean.NetSleepBean
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.bean.PopularScienceBean
import com.app.fmate.network.api.homeView.HomeViewApi
import com.app.fmate.network.api.sleepView.SleepApi
import com.app.fmate.network.requestCustomBig
import java.util.HashMap


class SleepViewModel : BaseViewModel() {
    val msg: MutableLiveData<String> = MutableLiveData()
    val result: MutableLiveData<NetSleepBean> = MutableLiveData()
    fun getSleep(date: String) {
        requestCustomBig({
            SleepApi.sleepApi.getSleep(date)
        }, {
            result.postValue(it)
        }, { code, message ->

            message?.let {
                msg.postValue(it)
            }
        })
    }
    val resultPopular: MutableLiveData<PopularScienceBean> = MutableLiveData()
    fun getPopular(value: HashMap<String, Any>) {
        requestCustomBig({
            HomeViewApi.mHomeViewApi.getPopular(value)
        }, {
            resultPopular.postValue(it)
        }, { code, message ->
            message?.let {
                msg.postValue(it)
            }
        })
    }
}