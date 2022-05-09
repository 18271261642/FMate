package com.example.test


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.weather.ServerWeatherViewModel
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean
import com.example.xingliansdk.service.OnWeatherStatusListener
import com.example.xingliansdk.service.work.BleWork
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.LogcatHelper
import com.example.xingliansdk.view.CusDfuAlertDialog
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
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
import java.io.File
import java.util.*


class TestNetActivity : BaseActivity<ServerWeatherViewModel>(), BleWrite.HistoryCallInterface,
    BleWrite.SpecifySleepSourceInterface ,OnWeatherStatusListener{




    private var cusDufAlert : CusDfuAlertDialog? = null
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




        XXPermissions.with(this).permission(Manifest.permission.READ_EXTERNAL_STORAGE).request { permissions, all -> }


        val isSms = XXPermissions.isGranted(this,Manifest.permission.READ_SMS)
        showLogTv.text = "permission="+isSms


        testNetBtn.setOnClickListener {

            BleWork().startLocation(this)
           // mViewModel.getWeatherServer("113.88,22.55")
        }

        //3.4.61 APP 端获取睡眠大数据缓存数据数据记录
        getBtn1.setOnClickListener {

            val resultByte = CmdUtil.getFullPackage(byteArrayOf(0x02,0x3D,0x00))
            showLogTv.text = "write bytes= "+HexDump.bytesToString(resultByte)
            BleWrite.writeCommByteArray(resultByte,false,this)
        }


        showDialogBtn.setOnClickListener {
            showOtaAlert();
        }

        shareLogBtn.setOnClickListener {
            measureBp()
        }


        smsBtn.setOnClickListener { 
            XXPermissions.with(this).permission(Manifest.permission.READ_SMS).request { permissions, all ->
                val isSms = XXPermissions.isGranted(this,Manifest.permission.READ_SMS)
                var conT =  permissions.toString()
                showLogTv.text = "permission="+conT+" "+isSms
            }
        }

    }


    private fun measureBp(){
        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x03)

        var resultArray = CmdUtil.getFullPackage(cmdArray)

        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {
                TODO("Not yet implemented")
            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(broadcastReceiver, IntentFilter("com.example.xingliansdk.test_weather"))
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun showOtaAlert() {

        cusDufAlert = CusDfuAlertDialog(this,R.style.edit_AlertDialog_style)
        cusDufAlert!!.show()
        cusDufAlert!!.setCancelable(false)
        cusDufAlert!!.setOnCusDfuClickListener(object : CusDfuAlertDialog.OnCusDfuClickListener {
            override fun onCancelClick() {
                cusDufAlert!!.dismiss()
            }

            override fun onSUreClick() {
                cusDufAlert!!.dismiss()

            }

        })
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
                BleWrite.writeSpecifySleepSourceCall(resultByte,true,startLongTime.toLong(),endLongTime.toLong(),this)
            }

    }


    private  val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return

            Log.e("主页定位成功","-----acion="+action)
            if(action == "com.example.xingliansdk.test_weather"){
                var wStr = Hawk.get("test_weather","")
                weatherTvShow.text = "天气返回="+wStr
            }
        }

    }

    override fun backWeatherStatus(str: String?) {
        weatherTvShow.text = "天气返回=$str"
    }

}