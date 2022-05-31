package com.example.xingliansdk.ui.bp

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlertDialog
import android.os.*
import android.view.View
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.dialog.MeasureBpDialogView
import com.example.xingliansdk.dialog.OnCommDialogClickListener
import com.example.xingliansdk.network.api.jignfan.JingfanBpViewModel
import com.example.xingliansdk.utils.AppActivityManager
import com.example.xingliansdk.utils.TimeUtil
import com.example.xingliansdk.utils.Utils
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.bluetooth.BLEManager
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.listener.MeasureBigBpListener
import com.shon.connector.utils.HexDump
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_card_edit.*
import kotlinx.android.synthetic.main.activity_card_edit.titleBar
import kotlinx.android.synthetic.main.activity_measure_bp_layout.*
import java.lang.StringBuilder

/**
 * 测量血压界面
 * Created by Admin
 *Date 2022/5/7
 */
class MeasureNewBpActivity : BaseActivity<JingfanBpViewModel>(),MeasureBigBpListener,View.OnClickListener{


    private var measureDialog : MeasureBpDialogView ?= null

    private val instant by lazy { this }

    var totalSecond = 0
    //记录超时的时间，2分钟
    var timeOutSecond = 0

    var savePath : String ?= null

    //手表测量血压的时间，上传后台
    var deviceMeasureTime : String ?= null

    private var alertDialog : AlertDialog.Builder ?=null

    private val handler : Handler = object :  Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x01){

            }

            if(msg.what == 0x00){

                //判断是否断开
                    if(!XingLianApplication.getXingLianApplication().getDeviceConnStatus()){
                        ShowToast.showToastLong("已断开连接!")
                        measureDialog?.cancel()
                        Config.IS_APP_STOP_MEASURE_BP = false
                        AppActivityManager.getInstance().finishActivity(this@MeasureNewBpActivity)
                        return
                    }

                timeOutSecond++
                if(timeOutSecond >=120){    //超时了
                    totalSecond = 0
                    timeOutSecond = 0
                    showMeasureDialog(false)
                    return
                   // stopMeasure(false)
                }

                if(totalSecond>=100)
                    totalSecond = 0
                totalSecond+=3
                startCountTime()
            }

        }
    }


    override fun layoutId(): Int {
       return R.layout.activity_measure_bp_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()

        savePath = Environment.getExternalStorageDirectory().path+"/Download/";


        measureBpAgainTv.setOnClickListener(this)

        //BLEManager.getInstance().dataDispatcher.clearAll()
        measureBp()

        showMeasureDialog(true)

    }


    private fun showMeasureDialog(isFail : Boolean){
        try {
            val isForeground = Utils.isForeground(this@MeasureNewBpActivity, MeasureNewBpActivity::class.java.name)
            if(!isForeground)
                return
            if(measureDialog == null){
                measureDialog = MeasureBpDialogView(this)
            }

            measureDialog!!.show()
            measureDialog!!.setCancelable(false)
            if(!isFail){
                measureDialog!!.setMeasureStatus(false,false)
                measureDialog!!.setMiddleSchedule(-1f)
                totalSecond = 0
            }

            measureDialog!!.setOnCommDialogClickListener(object : OnCommDialogClickListener{
                override fun onConfirmClick(code: Int) {  //再次测量按钮
                    TLog.error("------再次测量="+code)
                    measureBp()
                }

                override fun onCancelClick(code: Int) {
                    TLog.error("-----totalSecond="+totalSecond)
                    if(timeOutSecond != 0){
                        backAlert()
                        return
                    }
                    measureDialog?.dismiss()
                    AppActivityManager.getInstance().finishActivity(this@MeasureNewBpActivity)
                }

            })
        }catch (e : Exception){
            e.printStackTrace()
        }

    }



    override fun createObserver() {
        super.createObserver()

        //成功返回
        mViewModel.uploadJfBp.observe(this){
            TLog.error("---------后台返回="+ Gson().toJson(it))
            Config.isNeedTimeOut = false
            Config.DEVICE_AUTO_MEASURE_BP_ACTION
            if(measureDialog != null)
                measureDialog?.dismiss()
            totalSecond = 0
            timeOutSecond = 0
            stopMeasure(it as MeasureBpBean)
            showResult(it)
        }


        //后台非成功返回
        mViewModel.msgJfUploadBp.observe(this){
            TLog.error("---------后台飞200返回="+ Gson().toJson(it))
            Config.isNeedTimeOut = false
          //  measureDialog?.setMeasureStatus(true)
            totalSecond = 0
            timeOutSecond = 0
            showFailMeasure()
            stopMeasure(false)
        }


    }


    private fun showFailMeasure(){

        handler.removeMessages(0x00)
        showMeasureDialog(false)

        totalSecond = 0
        timeOutSecond = 0

    }

    @SuppressLint("SetTextI18n")
    private fun showResult(measureBpBean: MeasureBpBean){
        measureBpResultDayTv.text = measureBpBean.date
        measureBpResultTimeTv.text = measureBpBean.time
        measureBpResultHBpTv.text = (measureBpBean.sbp.toInt()).toString()+" mmHg"
        measureBpResultLBpTv.text = measureBpBean.dbp.toInt().toString()+" mmHg"
        measureBpResultHeartTv.text = measureBpBean.heartRate.toInt().toString() + " 次/分"

    }

    private fun measureBp(){
        Config.isNeedTimeOut = true
        BLEManager.getInstance().dataDispatcher.clear("")
        totalSecond = 0
        timeOutSecond = 0
        BleWrite.writeStartOrEndDetectBp(true,0x03,this)

    }

    private fun startCountTime(){
        if(measureDialog != null)
            measureDialog!!.setMiddleSchedule(totalSecond.toFloat())

        handler.sendEmptyMessageDelayed(0x00,1000)

    }

    //开始测量
    override fun measureStatus(status: Int,deviceTime : String) {
        TLog.error("-----测量装填="+status)
        if(status == 0x01){ //手表主动结束掉
            Config.isNeedTimeOut = false
            totalSecond = 0
            timeOutSecond = 0
            showFailMeasure()
        }else{
            this.deviceMeasureTime = deviceTime
            totalSecond = 0
            timeOutSecond = 0
            startCountTime()
        }

    }

    //测量结果
    override fun measureBpResult(bpValue: MutableList<Int>,time : String) {

        val hashMap = HashMap<String,String>()
        hashMap["data"] = bpValue.toString()
        hashMap["createTime"] = time

        val stringBuilder = StringBuilder()
        bpValue.forEachIndexed { index, i ->
            if(index == bpValue.size-1){
                stringBuilder.append(i)
            }else{
                stringBuilder.append(i)
                stringBuilder.append(",")
            }
        }

       // GetJsonDataUtil().writeTxtToFile("时间="+time+" "+Gson().toJson(stringBuilder.toString()),savePath,"signal_bp"+System.currentTimeMillis()+".json")
        if(deviceMeasureTime != null){
            mViewModel.uploadJFBpData(stringBuilder.toString(), deviceMeasureTime!!)
        }

        //stopMeasure()
    }


    private fun stopMeasure(measureBpBean: MeasureBpBean ){
        handler.removeMessages(0x00)
        //时间
        val longTime = TimeUtil.formatTimeToLong(deviceMeasureTime,0)
        val timeArray = HexDump.toByteArray(longTime-946656000L)

        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x00,0x01,0x07,0x02,0x00,0x07,timeArray[0],timeArray[1],timeArray[2],timeArray[3],
            measureBpBean.sbp.toInt().toByte(),measureBpBean.dbp.toInt().toByte(),measureBpBean.heartRate.toInt().toByte()
        )

        val resultArray = CmdUtil.getFullPackage(cmdArray)
        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {

            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {
1
            }

        })
    }

    private fun stopMeasure(isFinish : Boolean){
        handler.removeMessages(0x00)
        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x00,0x01,0x01)

        val resultArray = CmdUtil.getFullPackage(cmdArray)
        TLog.error("-----sssss="+resultArray)
        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {

            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {

            }

        })

        if(isFinish){
            measureDialog?.cancel()
            Config.IS_APP_STOP_MEASURE_BP = false
            AppActivityManager.getInstance().finishActivity(this@MeasureNewBpActivity)

        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.measureBpAgainTv->{
                measureDialog?.show()
                measureDialog?.setMiddleSchedule(-1f)
                measureBp()
            }
        }
    }

    private fun stop(){
        totalSecond = 0
        //记录超时的时间，2分钟
       timeOutSecond = 0
        Config.IS_APP_STOP_MEASURE_BP = true
        stopMeasure(true)
    }


    override fun onDestroy() {
        super.onDestroy()
        Config.isNeedTimeOut = false
    }


    private fun backAlert(){
        if(alertDialog == null){
            alertDialog = AlertDialog.Builder(this@MeasureNewBpActivity)
        }
        alertDialog!!.setTitle("提醒")
        alertDialog!!.setMessage("是否要终止测量?")
        alertDialog!!.setPositiveButton("确定"
        ) { p0, p1 ->
            p0.cancel()

            TLog.error("---------终止测量-------")
            stop()
          // handler.sendEmptyMessage(0x01)
        }.setNegativeButton("取消"
        ) { p0, p1 ->
            p0.cancel()
        }
        alertDialog!!.create().show()
    }

}