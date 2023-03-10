package com.app.test


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.base.BaseActivity
import com.app.fmate.dialog.DateSelectDialogView
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.network.api.weather.ServerWeatherViewModel
import com.app.fmate.network.api.weather.bean.ServerWeatherBean
import com.app.fmate.service.OnWeatherStatusListener
import com.app.fmate.ui.bp.DbManager
import com.app.fmate.ui.bp.PPG1CacheDb
import com.app.fmate.view.CusDfuAlertDialog
import com.app.fmate.view.DateUtil
import com.google.gson.Gson
import com.hjq.permissions.XXPermissions
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.bean.TimeBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.listener.MeasureBigBpListener
import com.shon.connector.utils.HexDump
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_test_net_layout.*
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


            if(msg.what == 0x02){
                showLogTv.text = ""+stringBuilder.toString()
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
            val intent = Intent()
            intent.action = com.app.fmate.Config.WEATHER_START_LOCATION_ACTION
           sendBroadcast(intent)
           // mViewModel.getWeatherServer("113.88,22.55")
        }

        //3.4.61 APP ????????????????????????????????????????????????
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


        getBpRecordBtn.setOnClickListener {
            val weatherService = XingLianApplication.getXingLianApplication().getWeatherService()
            weatherService?.getDevicePPG1CacheRecord()
        }


        getGoalBtn.setOnClickListener {
            showLogTv.text = Hawk.get("ppg_cache","?????????")
        }




        getAllDbPPGBtn.setOnClickListener {
            stringBuilder.delete(0,stringBuilder.length)
            val userInfo = Hawk.get(com.app.fmate.Config.database.USER_INFO, LoginBean())
           // XingLianApplication.getXingLianApplication().getWeatherService()?.uploadPPGCacheData()
            val todayList = DbManager.getDbManager().getDayPPGData(userInfo.user.userId,userInfo.user.mac,DateUtil.getCurrDate())

            TLog.error("---sieze="+todayList.size)

            showppg(todayList)

        }

    }

    val stringBuilder = StringBuffer()

    private fun showppg(todayList : MutableList<PPG1CacheDb>){
        Thread(Runnable {
            if(todayList != null &&todayList.size > 0){
                todayList.forEach {
                    stringBuilder.append(it.data+"\n")
                }
            }

         handler.sendEmptyMessageDelayed(0x02,1000)
        }).start()
    }



    private fun measureBp(){
        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x00,0x01,0x0B)

        var resultArray = CmdUtil.getFullPackage(cmdArray)


        BleWrite.writeStartOrEndDetectBp(true,0x03,object : MeasureBigBpListener{
            override fun measureStatus(status: Int,deviceTime : String) {
                TLog.error("-----????????????="+status)
            }

            override fun measureBpResult(bpValue: MutableList<Int>?,time : String) {
                TLog.error("--------??????="+Gson().toJson(bpValue))
                BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
                    override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {
                        TODO("Not yet implemented")
                    }

                    override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {
                        TODO("Not yet implemented")
                    }

                })
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

        val dateDialog = DateSelectDialogView(this,R.style.edit_AlertDialog_style)
        dateDialog.show()
        dateDialog.setCurrentShowDate(System.currentTimeMillis())




//        cusDufAlert = CusDfuAlertDialog(this,R.style.edit_AlertDialog_style)
//        cusDufAlert!!.show()
//        cusDufAlert!!.setCancelable(false)
//        cusDufAlert!!.setOnCusDfuClickListener(object : CusDfuAlertDialog.OnCusDfuClickListener {
//            override fun onCancelClick() {
//                cusDufAlert!!.dismiss()
//            }
//
//            override fun onSUreClick() {
//                cusDufAlert!!.dismiss()
//
//            }
//
//        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.serverWeatherData.observe(this){
            Log.e("??????",Gson().toJson(it))


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


    //????????????
    private fun anslysisTodayWeather(weatherBean: ServerWeatherBean){

        val weatherService = XingLianApplication.getXingLianApplication().getWeatherService()
        val cityStr = Hawk.get<String>("city")


        weatherTvShow.text = "????????????="+Gson().toJson(weatherBean)

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
        Log.e("???????????????","------key="+key+" ??????="+Gson().toJson(mList))
    }

    override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {
        if (specifySleepSourceBean != null) {
            showLogTv.text = "back bytes="+Gson().toJson(specifySleepSourceBean)
            Log.e("??????????????????","-----sepcit="+specifySleepSourceBean.endTime +" "+specifySleepSourceBean.remark)
            val constanceMils = 946656000L
            mViewModel.postSleepSourceServer(specifySleepSourceBean.remark,specifySleepSourceBean.startTime+constanceMils,specifySleepSourceBean.endTime+constanceMils,specifySleepSourceBean.avgActive,specifySleepSourceBean.avgHeartRate)
        }
    }

    override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {
            Log.e("???????????????","-------?????????="+startTime+" "+endTime)
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

            Log.e("??????????????????","-----acion="+action)
            if(action == "com.example.xingliansdk.test_weather"){
                var wStr = Hawk.get("test_weather","")
                weatherTvShow.text = "????????????="+wStr
            }
        }

    }

    override fun backWeatherStatus(str: String?) {
        weatherTvShow.text = "????????????=$str"
    }

}