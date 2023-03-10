package com.app.fmate.network.api.dailyActiveBean

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.requestCustomBig

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