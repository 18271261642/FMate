package com.example.test


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.weather.ServerWeatherViewModel
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.bean.TimeBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.utils.HexDump
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_test_net_layout.*
import java.util.*


class TestNetActivity : BaseActivity<ServerWeatherViewModel>(), BleWrite.HistoryCallInterface,BleWrite.SpecifySleepSourceInterface {


    val handler : Handler =  object : Handler(Looper.myLooper()!!){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0x00){
                val holidayWeatherList = msg.obj;
               // analysisWeather(holidayWeatherList as MutableList<ServerWeatherBean.Hourly>)
            }


            if(msg.what == 0x01){
                val serverWeatherBean = msg.obj;
            //    XingLianApplication.mXingLianApplication.getWeatherService()?.setWeatherData(serverWeatherBean as ServerWeatherBean)
                anslysisTodayWeather(serverWeatherBean as ServerWeatherBean)
            }
        }
    }

    override fun layoutId(): Int {
        return R.layout.activity_test_net_layout

    }

    override fun initView(savedInstanceState: Bundle?) {
        testNetBtn.setOnClickListener {

            mViewModel.getWeatherServer("113.88,22.55")
        }

        //3.4.61 APP 端获取睡眠大数据缓存数据数据记录
        getBtn1.setOnClickListener {

            val resultByte = CmdUtil.getFullPackage(byteArrayOf(0x02,0x3D,0x00))
            showLogTv.text = "write bytes= "+HexDump.bytesToString(resultByte)
            BleWrite.writeCommByteArray(resultByte,false,this)
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

        val weatherService = XingLianApplication.getXingLianApplication().getWeatherService()
        val cityStr = Hawk.get<String>("city")


        weatherTvShow.text = "天气返回="+Gson().toJson(weatherBean)

        weatherService?.setWeatherData(weatherBean,cityStr)



//        val stringBuffer = StringBuffer()
//        stringBuffer.append("010014")
//        val calendar = Calendar.getInstance()
//        //时间戳 4个byte
//        val currTime =calendar.timeInMillis/1000;
//
//        val constanceMils = 946656000L
//
//        val currTimeLong = currTime - constanceMils
//
//        //Log.e("111","-----相差="+currTimeLong)
//
//        val timeByte = HexDump.toByteArray(currTimeLong)
//        stringBuffer.append(HexDump.bytesToString(timeByte))
//
//
//        //天气类型 一个byte
//        val weatherType = weatherBean.statusCode.toByte()
//        stringBuffer.append(String.format("%02x",weatherType))
//
//        Log.e("TT","--------天气类型="+stringBuffer.toString())
//
//        //当前时刻的温度 2 个byte
//        val currTempByte = HexDump.toByteArrayTwo(weatherBean.temp.toInt() * 10)
//        val tmpStr = HexDump.bytesToString(currTempByte)
//        stringBuffer.append(tmpStr)
//
//     //   stringBuffer.append("00FA")
//
//        Log.e("当前时刻温度","-----currTempByte="+weatherBean.temp.toInt()+" "+ Arrays.toString(currTempByte) +" "+HexDump.bytesToString(currTempByte))
//
//        //当天最高温度 2 个byte
//        val currMaxTempByte = HexDump.toByteArrayTwo(weatherBean.tempMax.toInt() * 10)
//        stringBuffer.append(HexDump.bytesToString(currMaxTempByte))
//        //当天最低温度 2 个byte
//        val currMinTempByte = HexDump.toByteArrayTwo(weatherBean.tempMin.toInt() * 10)
//        stringBuffer.append(HexDump.bytesToString(currMinTempByte))
//        //空气质量指数 2 个byte
//        val airAqiByte = HexDump.toByteArrayTwo(weatherBean.airAqi.toInt())
//        stringBuffer.append(HexDump.bytesToString(airAqiByte))
//        //相对湿度 2 个byte
//        val humidityByte = HexDump.toByteArrayTwo(weatherBean.humidity.toInt())
//        stringBuffer.append(HexDump.bytesToString(humidityByte))
//        //紫外线指数 1个byte
//        val uvIndexByte =weatherBean.uvIndex.toInt().toByte()
//        stringBuffer.append(String.format("%02x",uvIndexByte))
//        //日出时间
//        val sunriseTime = weatherBean.sunrise
//        //日出时 一个byte
//        val sunriseHourByte = DateUtil.getHHmmForHour(sunriseTime).toByte()
//        stringBuffer.append(String.format("%02x",sunriseHourByte))
//        //日出分 一个byte
//        val sunriseMinuteByte = DateUtil.getHHmmForMinute(sunriseTime).toByte()
//        stringBuffer.append(String.format("%02x",sunriseMinuteByte))
//        //日落时间
//        val sunsetTime = weatherBean.sunset
//        //日落时 一个byte
//        val sunsetHourByte = DateUtil.getHHmmForHour(sunsetTime).toByte()
//        stringBuffer.append(String.format("%02x",sunsetHourByte))
//        //日落分 一个byte
//        val sunsetMinuteByte = DateUtil.getHHmmForMinute(sunsetTime).toByte()
//        stringBuffer.append(String.format("%02x",sunsetMinuteByte))
//
//        stringBuffer.append("3100065B9D5B89533A")
//
//        val weatherContentStr = stringBuffer.toString()
//
//
//        Log.e("天气", "---111---当前天气转换=$weatherContentStr")
//
//        val wArray = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER,ByteUtil.hexStringToByte(weatherContentStr))
//
//        Log.e("天气","-----attttt="+ByteUtil.getHexString(wArray))
//
//        val resultB = CmdUtil.getFullPackage(wArray)
//        Log.e("天气","----11--转换当天="+ByteUtil.getHexString(resultB))
//
//
//
//        BleWrite.writeWeatherCall(resultB,false)
//        //明天天气
//        writeTomorrow(weatherBean.tomorrow)
//
//        //后天天气
//        writeDayAfterTomorrow(weatherBean.dayAfterTomorrow)
//
//        //大后天
//        writeThredeeTodayData(weatherBean.threeDaysFromNow)
//
//        //24小时天气
//        analysisWeather(weatherBean.hourly)

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
        stringBuffer.append(String.format("%02x",weatherType))


        //当前时刻的温度 2 个byte
        stringBuffer.append("FFFF")

        //当天最高温度 2 个byte
        val currMaxTempByte = HexDump.toByteArray(tomorrow.tempMax.toInt() * 10)
        stringBuffer.append(HexDump.bytesToString(currMaxTempByte))
        //当天最低温度 2 个byte
        val currMinTempByte =  HexDump.toByteArrayTwo(tomorrow.tempMin * 10)
        stringBuffer.append(HexDump.bytesToString(currMinTempByte))
        //空气质量指数 2 个byte
        stringBuffer.append(tomorrow.airAqi)
        //相对湿度 2 个byte
        val humidityByte = tomorrow.humidity?.toInt()?.let { HexDump.toByteArrayTwo(it) }
        stringBuffer.append(HexDump.bytesToString(humidityByte))
        //紫外线指数 1个byte
        val uvIndexByte = tomorrow.uvIndex.toInt().toByte()
        stringBuffer.append(String.format("%02x",uvIndexByte))
        //日出时间
        val sunriseTime = tomorrow.sunrise
        //日出时 一个byte
        val sunriseHourByte = DateUtil.getHHmmForHour(sunriseTime).toByte()
        stringBuffer.append(String.format("%02x",sunriseHourByte))
        //日出分 一个byte
        val sunriseMinuteByte = DateUtil.getHHmmForMinute(sunriseTime).toByte()
        stringBuffer.append(String.format("%02x",sunriseMinuteByte))
        //日落时间
        val sunsetTime = tomorrow.sunset
        //日落时 一个byte
        val sunsetHourByte = DateUtil.getHHmmForHour(sunsetTime).toByte()
        stringBuffer.append(String.format("%02x",sunsetHourByte))
        //日落分 一个byte
        val sunsetMinuteByte = DateUtil.getHHmmForMinute(sunsetTime).toByte()
        stringBuffer.append(String.format("%02x",sunsetMinuteByte))
        stringBuffer.append("3100065B9D5B89533A")

        val weatherContentStr = stringBuffer.toString()

        Log.e("天气", "---22---当前天气转换=$weatherContentStr")

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
        stringBuffer.append(String.format("%02x",weatherType))
        //当前时刻的温度 2 个byte
        stringBuffer.append("FFFF")

        //当天最高温度 2 个byte
        val currMaxTempByte = HexDump.toByteArrayTwo(weatherBean.tempMax.toInt() * 10)
        stringBuffer.append(HexDump.bytesToString(currMaxTempByte))
        //当天最低温度 2 个byte
        val currMinTempByte = HexDump.toByteArrayTwo(weatherBean.tempMin.toInt() * 10)
        stringBuffer.append(HexDump.bytesToString(currMinTempByte))
        //空气质量指数 2 个byte
        stringBuffer.append(weatherBean.airAqi)
        //相对湿度 2 个byte
        val humidityByte = HexDump.toByteArrayTwo(weatherBean.humidity.toInt())
        stringBuffer.append(HexDump.bytesToString(humidityByte))
        //紫外线指数 1个byte
        val uvIndexByte =weatherBean.uvIndex.toInt().toByte()
        stringBuffer.append(String.format("%02x",uvIndexByte))
        //日出时间
        val sunriseTime = weatherBean.sunrise
        //日出时 一个byte
        val sunriseHourByte = DateUtil.getHHmmForHour(sunriseTime).toByte()
        stringBuffer.append(String.format("%02x",sunriseHourByte))
        //日出分 一个byte
        val sunriseMinuteByte = DateUtil.getHHmmForMinute(sunriseTime).toByte()
        stringBuffer.append(String.format("%02x",sunriseMinuteByte))
        //日落时间
        val sunsetTime = weatherBean.sunset
        //日落时 一个byte
        val sunsetHourByte = DateUtil.getHHmmForHour(sunsetTime).toByte()
        stringBuffer.append(String.format("%02x",sunsetHourByte))
        //日落分 一个byte
        val sunsetMinuteByte = DateUtil.getHHmmForMinute(sunsetTime).toByte()
        stringBuffer.append(String.format("%02x",sunsetMinuteByte))
        stringBuffer.append("3100065B9D5B89533A")
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
        stringBuffer.append(String.format("%02x",weatherType))

        //当前时刻的温度 2 个byte
        stringBuffer.append("FFFF")
        //当天最高温度 2 个byte
        val currMaxTempByte = HexDump.toByteArrayTwo(weatherBean.tempMax.toInt() * 10)
        stringBuffer.append(HexDump.bytesToString(currMaxTempByte))
        //当天最低温度 2 个byte
        val currMinTempByte = HexDump.toByteArrayTwo(weatherBean.tempMin.toInt() * 10)
        stringBuffer.append(HexDump.bytesToString(currMinTempByte))
        //空气质量指数 2 个byte
        stringBuffer.append(weatherBean.airAqi)
        //相对湿度 2 个byte
        val humidityByte = HexDump.toByteArrayTwo(weatherBean.humidity.toInt())
        stringBuffer.append(HexDump.bytesToString(humidityByte))
        //紫外线指数 1个byte
        val uvIndexByte =weatherBean.uvIndex.toInt().toByte()
        stringBuffer.append(String.format("%02x",uvIndexByte))
        //日出时间
        val sunriseTime = weatherBean.sunrise
        //日出时 一个byte
        val sunriseHourByte = DateUtil.getHHmmForHour(sunriseTime).toByte()
        stringBuffer.append(String.format("%02x",sunriseHourByte))
        //日出分 一个byte
        val sunriseMinuteByte = DateUtil.getHHmmForMinute(sunriseTime).toByte()
        stringBuffer.append(String.format("%02x",sunriseMinuteByte))
        //日落时间
        val sunsetTime = weatherBean.sunset
        //日落时 一个byte
        val sunsetHourByte = DateUtil.getHHmmForHour(sunsetTime).toByte()
        stringBuffer.append(String.format("%02x",sunsetHourByte))
        //日落分 一个byte
        val sunsetMinuteByte = DateUtil.getHHmmForMinute(sunsetTime).toByte()
        stringBuffer.append(String.format("%02x",sunsetMinuteByte))

        stringBuffer.append("3100065B9D5B89533A")

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


        stringBuffer.append("02$tiemByteStr")

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



    override fun HistoryCallResult(key: Byte, mList: ArrayList<TimeBean>?) {
        Log.e("睡眠大数据","------key="+key+" 集合="+Gson().toJson(mList))
    }

    override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {
        if (specifySleepSourceBean != null) {
            showLogTv.text = "back bytes="+Gson().toJson(specifySleepSourceBean)
            Log.e("最终结果返回","-----sepcit="+specifySleepSourceBean.endTime +" "+specifySleepSourceBean.remark)
            val constanceMils = 946656000L
            mViewModel.postSleepSourceServer(specifySleepSourceBean.remark,specifySleepSourceBean.startTime+constanceMils,specifySleepSourceBean.endTime+constanceMils,specifySleepSourceBean.avgActive,specifySleepSourceBean.avgHeartRate)
        }
    }

    override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {
            Log.e("得到时间戳","-------时间戳="+startTime+" "+endTime)
        showLogTv.text = "back bytes= "+HexDump.bytesToString(startTime) +" endTime="+HexDump.bytesToString(endTime)
            val btArray = startTime?.get(0)?.let {
                byteArrayOf(0x02,0x3F, it, startTime[1],
                    startTime[2], startTime[3]
                )
            }

            val resultByte = CmdUtil.getFullPackage(btArray)
            val startLongTime = startTime?.get(0)?.let { HexDump.getIntFromBytes(it,startTime[1],startTime[2],startTime[3]) }
            val endLongTime =
                endTime?.get(0)?.let { HexDump.getIntFromBytes(it,endTime[1],endTime[2],endTime[3]) }

            if (startLongTime != null && endLongTime != null) {
                showLogTv.text = "wrete bytes= "+HexDump.bytesToString(resultByte)
                BleWrite.writeSpecifySleepSourceCall(resultByte,false,startLongTime.toLong(),endLongTime.toLong(),this)
            }

    }

}