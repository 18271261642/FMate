package com.example.xingliansdk.network.api.weather

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.network.requestCustomBig
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import me.hgj.jetpackmvvm.base.appContext

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