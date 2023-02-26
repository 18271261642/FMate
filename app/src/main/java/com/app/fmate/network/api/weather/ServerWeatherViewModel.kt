package com.app.fmate.network.api.weather

import androidx.lifecycle.MutableLiveData
import com.app.fmate.XingLianApplication
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.api.weather.bean.ServerWeatherBean
import com.app.fmate.network.requestCustom
import com.app.fmate.network.requestCustomBig
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog

class ServerWeatherViewModel : BaseViewModel(){


     val serverWeatherData : MutableLiveData<ServerWeatherBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()

    val resultSleep: MutableLiveData<Any> = MutableLiveData()

    fun getWeatherServer(locationStr : String){
        requestCustom({
            ServerWeatherApi.serverWeatherApi.getServerWeatherData(locationStr)
        },
            {
                serverWeatherData.postValue(it)
            }) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                msg.postValue(it)
                ShowToast.showToastLong(it)
            }
        }

    }


    fun postSleepSourceServer(remark : String,startTime : Long,endTime : Long,avgActive : IntArray,avgHeartRate : IntArray) {
        requestCustomBig({
           ServerWeatherApi.serverWeatherApi.postSleepSourcesData(remark,startTime,endTime,avgActive,avgHeartRate)
        }, {
            resultSleep.postValue(it)
            TLog.error("-------上传睡眠返回="+it.toString())
            val weatherService = XingLianApplication.getXingLianApplication().getWeatherService()
            weatherService?.getDevicePPG1CacheRecord()

        }) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                msg.postValue(it)
                //ShowToast.showToastLong(it)
            }
        }
    }
}