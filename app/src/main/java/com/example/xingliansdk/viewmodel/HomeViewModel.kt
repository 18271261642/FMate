package com.example.xingliansdk.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.network.api.homeView.HomeCardVoBean
import com.example.xingliansdk.network.api.homeView.HomeViewApi
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.network.requestCustomBig
import com.example.xingliansdk.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlin.collections.HashMap


class HomeViewModel : BaseViewModel() {
    val result: MutableLiveData<Any> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    val resultBloodOxygen: MutableLiveData<Any> = MutableLiveData()
    val resultSleep: MutableLiveData<Any> = MutableLiveData()
    fun setHeartRate(startTime: String, endTime: String, heartRateList: IntArray) {
        requestCustom({
            HomeViewApi.mHomeViewApi.setHeartRate(startTime, endTime, heartRateList)
        }, {
            result.postValue(it)
        }
        ) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }
    fun setDailyActive(startTime: Long, endTime: Long, data: String) {
        requestCustom({
            HomeViewApi.mHomeViewApi.saveDailyActive(startTime, endTime, data)
        }, {
            result.postValue(it)
        }
        ) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }
    fun setTemperature(startTime: String, endTime: String, heartRateList: IntArray) {
        requestCustom({
            HomeViewApi.mHomeViewApi.setTemperature(startTime, endTime, heartRateList)
        }, {
            result.postValue(it)
        }
        ) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    fun setPressure(startTime: String, endTime: String, data: String) {
        requestCustom({
            HomeViewApi.mHomeViewApi.setPressure(startTime, endTime,data)
        }, {
            result.postValue(it)
        }
        ) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    fun saveBloodPressure(startTime: String, endTime: String, data: String) {
        requestCustom({
            HomeViewApi.mHomeViewApi.saveBloodPressure(startTime, endTime,data)
        }, {
            result.postValue(it)
        }
        ) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }


    fun setBloodOxygen(value: HashMap<String, Any>) {
        requestCustom({
            HomeViewApi.mHomeViewApi.saveBloodOxygen(value)
        }, {
            resultBloodOxygen.postValue(it)
        }
        ) { _, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    fun setBloodOxygen(startTime: String, endTime: String, list: IntArray) {
        requestCustom({
            HomeViewApi.mHomeViewApi.saveBloodOxygen(startTime, endTime, list)
        }, {
            resultBloodOxygen.postValue(it)
        }
        ) { _, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    fun setSleep(hashMap: HashMap<String, Any>) {
        requestCustom({
            HomeViewApi.mHomeViewApi.saveSleep(hashMap)
        }, {
            resultSleep.postValue(it)
        }
        ) { _, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    fun setSleep(
        startTime: String,
        endTime: String,
        apneaTime: String,
        apneaSecond: String,
        avgHeartRate: String,
        minHeartRate: String,
        maxHeartRate: String,
        respiratoryQuality: String,
        sleepList: String
    ) {
        requestCustom({
            HomeViewApi.mHomeViewApi.saveSleep(
                startTime,
                endTime,
                apneaTime,
                apneaSecond,
                avgHeartRate,
                minHeartRate,
                maxHeartRate,
                respiratoryQuality,
                sleepList
            )
        }, {
            resultSleep.postValue(it)
        }
        ) { _, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }
    val resultPopular: MutableLiveData<PopularScienceBean> = MutableLiveData()
    fun getPopular(value: HashMap<String, Any>) {
        requestCustom({
            HomeViewApi.mHomeViewApi.getPopular(value)
        }, {
            resultPopular.postValue(it)
        }) { code, message ->
            message?.let {
                msg.postValue(it)
            }
        }
    }

    val resultHomeCard: MutableLiveData<HomeCardVoBean> = MutableLiveData()
    fun getHomeCard() {
        requestCustomBig({
            HomeViewApi.mHomeViewApi.getHomeCard()
        }, {
            resultHomeCard.postValue(it)
        }, { code, message ->
            message?.let {
                msg.postValue(it)
            }
        })
    }
}