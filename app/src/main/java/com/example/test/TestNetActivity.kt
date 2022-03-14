package com.example.test

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.weather.ServerWeatherViewModel
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean
import com.example.xingliansdk.view.CusDfuAlertDialog
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shon.bluetooth.core.call.WriteCall
import com.shon.bluetooth.util.ByteUtil
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.write.settingclass.TestWeatherCall
import com.shon.connector.call.write.settingclass.WeatherCall
import com.shon.connector.utils.HexDump
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_test_net_layout.*
import java.lang.StringBuilder
import java.util.*


class TestNetActivity : BaseActivity<ServerWeatherViewModel>() {


    val handler : Handler =  object : Handler(Looper.myLooper()!!){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0x00){
                val holidayWeatherList = msg.obj;
               // analysisWeather(holidayWeatherList as MutableList<ServerWeatherBean.Hourly>)
            }


            if(msg.what == 0x01){
                val serverWeatherBean = msg.obj;
                anslysisTodayWeather(serverWeatherBean as ServerWeatherBean)
            }
        }
    }

    override fun layoutId(): Int {
        return R.layout.activity_test_net_layout

    }

    override fun initView(savedInstanceState: Bundle?) {
        testNetBtn.setOnClickListener {
            mViewModel.getWeatherServer("116.41,39.92")

        }

        //3.4.61 APP 端获取睡眠大数据缓存数据数据记录
        getBtn1.setOnClickListener {
           // val byarray =
        }

    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.serverWeatherData.observe(this){
            Log.e("天气",Gson().toJson(it))



            val msg = handler.obtainMessage();
            msg.what = 0x01
            msg.obj = it
            handler.sendMessage(msg)


            val holidayWeatherList = it.hourly

            val message = handler.obtainMessage()
            message.what = 0x00
            message.obj = holidayWeatherList
            handler.sendMessage(message)

        }
    }


    //今天天气
    private fun anslysisTodayWeather(weatherBean: ServerWeatherBean){

        val stringBuffer = StringBuffer()
        stringBuffer.append("010014")
        val calendar = Calendar.getInstance()
        //时间戳 4个byte
        val currTime =getZeroMills()/1000;

        val constanceMils = 946656000L

        val currTimeLong = currTime - constanceMils

        //Log.e("111","-----相差="+currTimeLong)

        val timeByte = HexDump.toByteArray(currTimeLong)
        stringBuffer.append(HexDump.bytesToString(timeByte))


        //天气类型 一个byte
        val weatherType = weatherBean.statusCode.toByte()
        stringBuffer.append(weatherType)

        Log.e("TT","--------天气类型="+stringBuffer.toString())

        //当前时刻的温度 2 个byte
        val currTempByte = HexDump.toByteArrayTwo(weatherBean.temp.toInt())
        stringBuffer.append(HexDump.bytesToString(currTempByte))

        Log.e("当前时刻温度","-----currTempByte="+weatherBean.temp.toInt()+" "+ Arrays.toString(currTempByte) +" "+HexDump.bytesToString(currTempByte))

        //当天最高温度 2 个byte
        val currMaxTempByte = HexDump.toByteArrayTwo(weatherBean.tempMax.toInt())
        stringBuffer.append(HexDump.bytesToString(currMaxTempByte))
        //当天最低温度 2 个byte
        val currMinTempByte = HexDump.toByteArrayTwo(weatherBean.tempMin.toInt())
        stringBuffer.append(HexDump.bytesToString(currMinTempByte))
        //空气质量指数 2 个byte
        val airAqiByte = HexDump.toByteArrayTwo(weatherBean.airAqi.toInt())
        stringBuffer.append(HexDump.bytesToString(airAqiByte))
        //相对湿度 2 个byte
        val humidityByte = HexDump.toByteArrayTwo(weatherBean.humidity.toInt())
        stringBuffer.append(HexDump.bytesToString(humidityByte))
        //紫外线指数 1个byte
        val uvIndexByte =weatherBean.uvIndex.toInt().toByte()
        stringBuffer.append(uvIndexByte)
        //日出时间
        val sunriseTime = weatherBean.sunrise
        //日出时 一个byte
        val sunriseHourByte = DateUtil.getHHmmForHour(sunriseTime).toByte()
        stringBuffer.append(sunriseHourByte)
        //日出分 一个byte
        val sunriseMinuteByte = DateUtil.getHHmmForMinute(sunriseTime).toByte()
        stringBuffer.append(sunriseMinuteByte)
        //日落时间
        val sunsetTime = weatherBean.sunset
        //日落时 一个byte
        val sunsetHourByte = DateUtil.getHHmmForHour(sunsetTime).toByte()
        stringBuffer.append(sunsetHourByte)
        //日落分 一个byte
        val sunsetMinuteByte = DateUtil.getHHmmForMinute(sunsetTime).toByte()
        stringBuffer.append(sunsetMinuteByte)

        Log.e("天气","---111---当前天气转换="+stringBuffer.toString())

        val weatherContentStr = stringBuffer.toString()

        val wArray = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER,HexDump.stringToByte(weatherContentStr))

        val resultB = CmdUtil.getFullPackage(wArray)
        Log.e("天气","----11--转换当天="+ByteUtil.getHexString(CmdUtil.getFullPackage(wArray)))

        BleWrite.writeWeatherCall(resultB,false)
        //明天天气
        writeTomorrow(weatherBean.tomorrow)

        //后天天气
        writeDayAfterTomorrow(weatherBean.dayAfterTomorrow)

        //大后天
        writeThredeeTodayData(weatherBean.threeDaysFromNow)

        //24小时天气
        analysisWeather(weatherBean.hourly)

    }

    //明天天气
    private fun writeTomorrow(tomorrow: ServerWeatherBean.Tomorrow) {
        val stringBuffer = StringBuffer()
        stringBuffer.append("010014")

        //时间戳 4个byte
        var currTime = getZeroMills()/1000;
        currTime += 86400L;
        val constanceMils = 946656000L

        val currTimeLong = currTime - constanceMils

        //Log.e("111","-----相差="+currTimeLong)

        val timeByte = HexDump.toByteArray(currTimeLong)
        stringBuffer.append(HexDump.bytesToString(timeByte))


        //天气类型 一个byte
        val weatherType = tomorrow.statusCode.toByte()
        stringBuffer.append(tomorrow.statusCode)


        //当前时刻的温度 2 个byte
        stringBuffer.append("FFFF")

        //当天最高温度 2 个byte
        val currMaxTempByte = tomorrow.tempMax?.toInt()?.let { HexDump.toByteArrayTwo(it) }
        stringBuffer.append(HexDump.bytesToString(currMaxTempByte))
        //当天最低温度 2 个byte
        val currMinTempByte = tomorrow.tempMin.let { HexDump.toByteArrayTwo(it) }
        stringBuffer.append(HexDump.bytesToString(currMinTempByte))
        //空气质量指数 2 个byte
        stringBuffer.append(tomorrow.airAqi)
        //相对湿度 2 个byte
        val humidityByte = tomorrow.humidity?.toInt()?.let { HexDump.toByteArrayTwo(it) }
        stringBuffer.append(HexDump.bytesToString(humidityByte))
        //紫外线指数 1个byte
        val uvIndexByte = tomorrow.uvIndex.toInt().toByte()
        stringBuffer.append(uvIndexByte)
        //日出时间
        val sunriseTime = tomorrow.sunrise
        //日出时 一个byte
        val sunriseHourByte = DateUtil.getHHmmForHour(sunriseTime).toByte()
        stringBuffer.append(sunriseHourByte)
        //日出分 一个byte
        val sunriseMinuteByte = DateUtil.getHHmmForMinute(sunriseTime).toByte()
        stringBuffer.append(sunriseMinuteByte)
        //日落时间
        val sunsetTime = tomorrow.sunset
        //日落时 一个byte
        val sunsetHourByte = DateUtil.getHHmmForHour(sunsetTime).toByte()
        stringBuffer.append(sunsetHourByte)
        //日落分 一个byte
        val sunsetMinuteByte = DateUtil.getHHmmForMinute(sunsetTime).toByte()
        stringBuffer.append(sunsetMinuteByte)

        Log.e("天气","---22---当前天气转换="+stringBuffer.toString())

        val weatherContentStr = stringBuffer.toString()

        val wArray = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER,HexDump.stringToByte(weatherContentStr))

        val resultB = CmdUtil.getFullPackage(wArray)
        BleWrite.writeWeatherCall(resultB,false)


    }

    //后天天气
    private fun writeDayAfterTomorrow(weatherBean: ServerWeatherBean.DayAfterTomorrow){
        val stringBuffer = StringBuffer()
        stringBuffer.append("010014")

        //时间戳 4个byte
        var currTime = getZeroMills()/1000;
        currTime += 86400L*2;
        val constanceMils = 946656000L

        val currTimeLong = currTime - constanceMils

        //Log.e("111","-----相差="+currTimeLong)

        val timeByte = HexDump.toByteArray(currTimeLong)
        stringBuffer.append(HexDump.bytesToString(timeByte))


        //天气类型 一个byte
        val weatherType = weatherBean.statusCode
        stringBuffer.append(weatherType)
        //当前时刻的温度 2 个byte
        stringBuffer.append("FFFF")

        //当天最高温度 2 个byte
        val currMaxTempByte = HexDump.toByteArrayTwo(weatherBean.tempMax.toInt())
        stringBuffer.append(HexDump.bytesToString(currMaxTempByte))
        //当天最低温度 2 个byte
        val currMinTempByte = HexDump.toByteArrayTwo(weatherBean.tempMin.toInt())
        stringBuffer.append(HexDump.bytesToString(currMinTempByte))
        //空气质量指数 2 个byte
        stringBuffer.append(weatherBean.airAqi)
        //相对湿度 2 个byte
        val humidityByte = HexDump.toByteArrayTwo(weatherBean.humidity.toInt())
        stringBuffer.append(HexDump.bytesToString(humidityByte))
        //紫外线指数 1个byte
        val uvIndexByte =weatherBean.uvIndex.toInt().toByte()
        stringBuffer.append(uvIndexByte)
        //日出时间
        val sunriseTime = weatherBean.sunrise
        //日出时 一个byte
        val sunriseHourByte = DateUtil.getHHmmForHour(sunriseTime).toByte()
        stringBuffer.append(sunriseHourByte)
        //日出分 一个byte
        val sunriseMinuteByte = DateUtil.getHHmmForMinute(sunriseTime).toByte()
        stringBuffer.append(sunriseMinuteByte)
        //日落时间
        val sunsetTime = weatherBean.sunset
        //日落时 一个byte
        val sunsetHourByte = DateUtil.getHHmmForHour(sunsetTime).toByte()
        stringBuffer.append(sunsetHourByte)
        //日落分 一个byte
        val sunsetMinuteByte = DateUtil.getHHmmForMinute(sunsetTime).toByte()
        stringBuffer.append(sunsetMinuteByte)

        val weatherContentStr = stringBuffer.toString()

        val wArray = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER,HexDump.stringToByte(weatherContentStr))

        val resultB = CmdUtil.getFullPackage(wArray)
        Log.e("天气","---22--转换当天="+ByteUtil.getHexString(CmdUtil.getFullPackage(wArray)))
        BleWrite.writeWeatherCall(resultB,false)

    }



    private fun writeThredeeTodayData(weatherBean: ServerWeatherBean.ThreeDaysFromNow){
        val stringBuffer = StringBuffer()
        stringBuffer.append("010014")

        //时间戳 4个byte
        var currTime = getZeroMills()/1000;
        currTime += 86400L*3;
        val constanceMils = 946656000L

        val currTimeLong = currTime - constanceMils

        //Log.e("111","-----相差="+currTimeLong)

        val timeByte = HexDump.toByteArray(currTimeLong)
        stringBuffer.append(HexDump.bytesToString(timeByte))


        //天气类型 一个byte
        val weatherType = weatherBean.statusCode.toByte()
        stringBuffer.append(weatherType)

        //当前时刻的温度 2 个byte
        stringBuffer.append("FFFF")
        //当天最高温度 2 个byte
        val currMaxTempByte = HexDump.toByteArrayTwo(weatherBean.tempMax.toInt())
        stringBuffer.append(HexDump.bytesToString(currMaxTempByte))
        //当天最低温度 2 个byte
        val currMinTempByte = HexDump.toByteArrayTwo(weatherBean.tempMin.toInt())
        stringBuffer.append(HexDump.bytesToString(currMinTempByte))
        //空气质量指数 2 个byte
        stringBuffer.append(weatherBean.airAqi)
        //相对湿度 2 个byte
        val humidityByte = HexDump.toByteArrayTwo(weatherBean.humidity.toInt())
        stringBuffer.append(HexDump.bytesToString(humidityByte))
        //紫外线指数 1个byte
        val uvIndexByte =weatherBean.uvIndex.toInt().toByte()
        stringBuffer.append(uvIndexByte)
        //日出时间
        val sunriseTime = weatherBean.sunrise
        //日出时 一个byte
        val sunriseHourByte = DateUtil.getHHmmForHour(sunriseTime).toByte()
        stringBuffer.append(sunriseHourByte)
        //日出分 一个byte
        val sunriseMinuteByte = DateUtil.getHHmmForMinute(sunriseTime).toByte()
        stringBuffer.append(sunriseMinuteByte)
        //日落时间
        val sunsetTime = weatherBean.sunset
        //日落时 一个byte
        val sunsetHourByte = DateUtil.getHHmmForHour(sunsetTime).toByte()
        stringBuffer.append(sunsetHourByte)
        //日落分 一个byte
        val sunsetMinuteByte = DateUtil.getHHmmForMinute(sunsetTime).toByte()
        stringBuffer.append(sunsetMinuteByte)

        val weatherContentStr = stringBuffer.toString()

        val wArray = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER,HexDump.stringToByte(weatherContentStr))

        val resultB = CmdUtil.getFullPackage(wArray)
        Log.e("天气","---22--转换当天="+ByteUtil.getHexString(CmdUtil.getFullPackage(wArray)))
        BleWrite.writeWeatherCall(resultB,false)
    }



    private  fun analysisWeather(hourList : MutableList<ServerWeatherBean.Hourly>){
//        val type = object : TypeToken<List<ServerWeatherBean.HourlyItem>>(){}.type
//        val hourList : List<ServerWeatherBean.HourlyItem> = Gson().fromJson(hourListStr,type)

        //时间戳
        val calendar = Calendar.getInstance()
        val currTime = calendar.timeInMillis / 1000;
        val constanceMils = 946656000L
        val timeByte = HexDump.toByteArray(currTime-constanceMils)

        val tiemByteStr = HexDump.bytesToString(timeByte)


        val stringBuffer = StringBuffer()


        stringBuffer.append("02"+tiemByteStr+"1803")

        hourList.forEach {
            //类型
            val type = it.statusCode
            //温度
            val temputerV = it.temp
            //温度两个byte
            val byteTem = HexDump.toByteArrayTwo(temputerV)

            val typeStr = String.format("%02d",(if(type<8)type else 0xff))
            val tmpStr = HexDump.bytesToString(byteTem)

           // TLog.error("t","------="+typeStr+tmpStr)
            stringBuffer.append(typeStr+tmpStr)
        }


        val byte = CmdUtil.getPlayer(
            Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER,HexDump.stringToByte(stringBuffer.toString()))

        val resultByte = CmdUtil.getFullPackage(byte)
        TLog.error("天气", ByteUtil.getHexString(byte)+"\n"+ByteUtil.getHexString(resultByte))


        BleWrite.writeWeatherCall(resultByte,false)
    }

    fun getZeroMills(): Long {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        return cal.timeInMillis
    }
}