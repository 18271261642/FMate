package com.example.xingliansdk.network.api.dailyActiveBean

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.weightView.WeightApi
import com.example.xingliansdk.network.api.weightView.WeightModeBean
import com.example.xingliansdk.network.requestCustomBig

open class DailyActiveModel : BaseViewModel() {
    val result: MutableLiveData<Any> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun getDailyActive(type: String, date: String) {
        requestCustomBig({
            DailyActiveApi.dailyActiveApi.getDailyActive(type, date)
        }, {
            result.postValue(it)
        }, { code, message ->

            message?.let {
                msg.postValue(it)
            }
        })
    }
}