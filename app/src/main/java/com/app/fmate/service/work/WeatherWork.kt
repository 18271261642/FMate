package com.app.fmate.service.work

import android.content.Context
import com.app.fmate.Config
import com.app.fmate.XingLianApplication.Companion.TIME_START
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.service.core.IWork
import com.app.fmate.view.DateUtil
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.qweather.sdk.bean.air.AirNowBean
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.QWeather
import com.shon.connector.bean.DataBean
import com.shon.connector.utils.TLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.*

/**
 * 天气查询任务
 */
class WeatherWork : IWork {
    private lateinit var context: Context
    var tempStatus=false
    override fun init(context: Context) {
        super.init(context)
        this.context = context
//        TLog.error("来的次数")
    }

    var dataList: ArrayList<DataBean> = arrayListOf()
    override fun onReceivedMessage(eMessage: SNEvent<*>) {
        dataList = Hawk.get("weatherList", dataList)
        //dataList.reverse()
//        TLog.error("dataList=="+dataList.size)
//        TLog.error("Message.code =="+eMessage.code)
        if (eMessage.code == Config.eventBus.DEVICE_CONNECT_WEATHER_SERVICE && dataList.size > 0) {
          //  TLog.error("dataList==${Gson().toJson(dataList)}")
//            for (i in 0 until dataList.size) {
//                TLog.error("天气发送+=" + dataList[i].temperature+"   dataList.size=="+ dataList.size)
//                if(i==0) {
//                    BleWrite.writeWeatherCall(dataList[i], true)
//                }
//                else
//                    BleWrite.writeWeatherCall(dataList[i],false)
//
//            }
            dataList = arrayListOf()
            Hawk.get("weatherList", dataList)
            return
        }
        if (eMessage.code != Config.eventBus.LOCATION_INFO||tempStatus) {
            //不是更新地理位置的消息，不处理
            return
        }
        if(!tempStatus)
        {
            tempStatus=true
        }

        dataList = arrayListOf()
//        TLog.error("初始化多次")
        val local: String = eMessage.data as String
//        TLog.error("local++$local")
//        TLog.error("local++${Gson().toJson(eMessage)}")
        //获取地理位置信息，格式为 ："经度,维度"
        GlobalScope.launch {

            //获取当前天气信息
            val weatherInfoNow = getWeatherInfoNow(local)
            TLog.error("weatherInfoNow = ${Gson().toJson(weatherInfoNow)}")
            //获取空气质量信息
            val airNow = getAirNow(local)
         //   TLog.error("airNow = ${Gson().toJson(airNow)}")
            //获取3天之内的信息
            val weather3D = getWeather3D(local)
            TLog.error("weather3D++" + Gson().toJson(weather3D))
            weather3D.find {
                //只显示今天的信息
//                TLog.error("找了几次" + it.fxDate)
                it.fxDate == DateUtil.getDate(DateUtil.YYYY_MM_DD, Date())

            }?.let {
//                TLog.error("dailyBean  = ${Gson().toJson(it)}")
                //组装 dataBean
                val dataBean = getDataBean(it)
                dataBean.temperature = weatherInfoNow.temp
                dataBean.airQuality = airNow.aqi
                if (Hawk.get<String>("city").isNotEmpty()) {
                    dataBean.unicodeType = DataBean.TEMPERATURE_FEATURES_UNICODE
                    dataBean.unicodeContent = Hawk.get("city")
                }
                dataList.add(dataBean)
//                TLog.error("dataBean = ${Gson().toJson(dataBean)}")
            }
            //给硬件想要的预留的三天的数据
            for (i in 1 until weather3D.size) {
                val dataBean = getDataBean(weather3D[i])
//                TLog.error("dataBean++"+Gson().toJson(dataBean))
                dataBean.temperature = "65535" //即默认的 0xffff
                dataBean.airQuality = airNow.aqi
                if (Hawk.get<String>("city").isNotEmpty()) {
                    dataBean.unicodeType = DataBean.TEMPERATURE_FEATURES_UNICODE
                    dataBean.unicodeContent = Hawk.get("city")
                }
                dataList.add(dataBean)
           //     TLog.error("dataBean = ${Gson().toJson(dataBean)}")
//                TLog.error("dataList = ${dataList.size}")
                // BleWrite.writeWeatherCall(dataBean)
            }
            Hawk.put("weatherList", dataList)
            dataList.clear()
        }
//        TLog.error("长度++=+" + dataList.size)
//        if (dataList.size > 0) {
//            Handler().postDelayed({
//                TLog.error("dataList+" + Gson().toJson(dataList))
                Hawk.put("weatherList", dataList)
                dataList.clear()
//            }, 200)
//        }
    }


    private fun getDataBean(dailyBean: WeatherDailyBean.DailyBean): DataBean {
        val dataBean = DataBean()
        dataBean.time =
            DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD, dailyBean.fxDate) / 1000 - TIME_START
        dataBean.uvIndex = dailyBean.uvIndex //紫外线
        dataBean.highestTemperatureToday = dailyBean.tempMax  //最高温
        dataBean.lowestTemperatureToday = dailyBean.tempMin //最低温
        dataBean.humidity = dailyBean.humidity //相对湿度
        dataBean.sunriseHours = getIntValueFromTimeString(dailyBean.sunrise, 0)  //
        dataBean.sunriseMin = getIntValueFromTimeString(dailyBean.sunrise, 1)
        dataBean.sunsetHours = getIntValueFromTimeString(dailyBean.sunset, 0)
        dataBean.sunsetMin = getIntValueFromTimeString(dailyBean.sunset, 1)

        dataBean.weatherType = WeatherUtil.getWeatherCode(dailyBean.iconDay)
        return dataBean
    }

    private fun getIntValueFromTimeString(timeString: String, index: Int): Int {
        val string = timeString.split(":")[index]
        return string.toInt()
    }

    private suspend fun getWeatherInfoNow(location: String): WeatherNowBean.NowBaseBean {
        val channel: Channel<WeatherNowBean.NowBaseBean> = Channel()
        QWeather.getWeatherNow(context, location, object : QWeather.OnResultWeatherNowListener {
            override fun onError(p0: Throwable?) {
            }

            override fun onSuccess(p0: WeatherNowBean?) {
                GlobalScope.launch {
                    channel.send(p0!!.now)
                }
            }
        })
        return channel.receive();
    }

    private suspend fun getWeather3D(location: String): List<WeatherDailyBean.DailyBean> {
        val channel: Channel<List<WeatherDailyBean.DailyBean>> = Channel()
        QWeather.getWeather3D(context, location, object : QWeather.OnResultWeatherDailyListener {
            override fun onError(p0: Throwable?) {
                TLog.error("p0 $p0")
            }


            override fun onSuccess(p0: WeatherDailyBean?) {

                p0?.let {
                    GlobalScope.launch {
                        channel.send(p0.daily)
                    }
                }
            }
        })
        return channel.receive();
    }

    suspend fun getAirNow(location: String): AirNowBean.NowBean {
        val channel: Channel<AirNowBean.NowBean> = Channel()
        QWeather.getAirNow(
            context,
            location,
            null,
            object : QWeather.OnResultAirNowListener {
                override fun onError(p0: Throwable?) {
                }

                override fun onSuccess(p0: AirNowBean?) {
                    p0?.let {
                        GlobalScope.launch {
                            channel.send(it.now)
                        }
                    }
                }

            })
        return channel.receive()
    }
}