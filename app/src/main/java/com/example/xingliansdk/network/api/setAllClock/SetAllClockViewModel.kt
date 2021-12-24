package com.example.xingliansdk.network.api.setAllClock

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.network.api.homeView.HomeViewApi
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.network.requestCustomBig
import java.util.HashMap

open class SetAllClockViewModel : BaseViewModel() {
    val resultRemind: MutableLiveData<ClockListBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun getRemind(type: String="0") {
        requestCustom({
            SetAllClockApi.clockApi.getRemind(type)
        }, {
            resultRemind.postValue(it)
        }, { _, message ->
            message?.let {
                msg.postValue(it)
            }
        })
    }
    val resultAlarmClock: MutableLiveData<Any> = MutableLiveData()
    fun saveAlarmClock(value: HashMap<String,String>) {
        requestCustom({
            SetAllClockApi.clockApi.saveAlarmClock(value)
        }, {
            resultAlarmClock.postValue(it)
        }, { code, message ->
            message?.let {
                msg.postValue(it)
            }
        })
    }
    fun saveTakeMedicine(value: HashMap<String,String>) {
        requestCustom({
            SetAllClockApi.clockApi.saveTakeMedicine(value)
        }, {
            resultAlarmClock.postValue(it)
        }, { code, message ->
            message?.let {
                msg.postValue(it)
            }
        })
    }
    fun saveSchedule(value: HashMap<String,String>) {
        requestCustom({
            SetAllClockApi.clockApi.saveSchedule(value)
        }, {
            resultAlarmClock.postValue(it)
        }, { code, message ->
            message?.let {
                msg.postValue(it)
            }
        })
    }
}