package com.app.fmate.network.api.tempView

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.bean.PopularScienceBean
import com.app.fmate.network.api.homeView.HomeViewApi
import com.app.fmate.network.requestCustomBig
import java.util.HashMap

open class TempViewModel : BaseViewModel() {
    val result: MutableLiveData<TemperatureVoBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun getTemp(startTime: String, endTime: String) {
        requestCustomBig({
            TempApi.tempApi.getTemp(startTime, endTime)
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