package com.app.fmate.ui.bloodOxygen.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.bean.PopularScienceBean
import com.app.fmate.network.api.bloodOxygen.BloodOxygenApi
import com.app.fmate.network.api.bloodOxygen.BloodOxygenVoBean
import com.app.fmate.network.api.homeView.HomeViewApi
import com.app.fmate.network.requestCustomBig
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