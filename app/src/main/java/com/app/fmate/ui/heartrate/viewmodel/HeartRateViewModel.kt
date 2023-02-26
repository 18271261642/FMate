package com.app.fmate.ui.heartrate.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.bean.PopularScienceBean
import com.app.fmate.network.api.heartView.HeartRateApi
import com.app.fmate.network.api.homeView.HomeViewApi
import com.app.fmate.network.requestCustomBig
import java.util.HashMap


open class HeartRateViewModel : BaseViewModel() {
    val result: MutableLiveData<Any> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun getHeartRate(startTime: String, endTime: String) {
        requestCustomBig({
            HeartRateApi.heartRateApi.getHeartRate(startTime, endTime)
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