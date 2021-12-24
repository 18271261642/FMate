package com.example.xingliansdk.ui.bloodOxygen.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.network.api.bloodOxygen.BloodOxygenApi
import com.example.xingliansdk.network.api.bloodOxygen.BloodOxygenVoBean
import com.example.xingliansdk.network.api.homeView.HomeViewApi
import com.example.xingliansdk.network.requestCustomBig
import java.util.HashMap


open class BloodOxygenViewModel : BaseViewModel() {
    val result: MutableLiveData<BloodOxygenVoBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun getBloodOxygen(startTime: String, endTime: String) {
        requestCustomBig({
            BloodOxygenApi.bloodOxygenApi.getBloodOxygen(startTime, endTime)
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